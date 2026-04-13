package com.components.submittable;

import com.core.Unit;
import com.core.utils.DoubleUtils;
import com.core.utils.ListUtils;
import com.components.Fx;
import com.utils.FxDisposable;
import com.sun.javafx.scene.control.skin.SpinnerSkin;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import static com.utils.ObservableListUtils.addOrRemove;

/**
 * Too many scenarios to verify and be aware of when updating this class:
 * Happy paths:
 * 1 - Increment/decrement with buttons and get expected response.
 * 2 - Update textfield with valid value using enter or tab and get expected response/update.
 * 3 - Update textfield with un-existing value and expect the closest value to be submitted/received.
 * 4 - Update textfield, show dirty or invalid state and escape correctly.
 * Other paths:
 * 1 - Increment/decrement and get no response; revert to correct value.
 * 2 - Update textfield with valid value and get no response; revert correctly.
 * 3 - Update textfield with un-existing value but submit closest and get no response; revert correctly.
 * Different states:
 * - Check all paths consistently show dirty/invalid/intermittent states.
 * - Check all paths consistently show units and data types for Integer or Double based fields.
 */
public class FxSubmittableSpinner extends Spinner<Double> implements FxDisposable {

    private static final String STYLE_DIRTY = "fx-dirty";
    private static final String STYLE_INVALID = "fx-invalid";
    private static final String STYLE_INDETERMINATE = "fx-indeterminate";

    private enum State {EDITING, SUBMITTING, UPDATING, REVERTING, IDLE}
    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);

    private final ObjectProperty<Double> stateProperty = Fx.oProp();
    private final BooleanProperty indeterminateProperty = Fx.bProp(false);
    private final AtomicBoolean requiresFocus = new AtomicBoolean(false);

    private final Duration DEFAULT_TIMEOUT_SECS = Duration.seconds(5);
    private final Duration timeout = DEFAULT_TIMEOUT_SECS;
    private PauseTransition pauseTransition;

    private String lastText;

    private BiConsumer<FxSubmittableSpinner, Double> consumer;
    private final ConsumerValue.Options consumerValueOptions = new ConsumerValue.Options();
    private final Unit unit;

    private class XConverter extends StringConverter<Double> {
        private final String unit;
        private final DecimalFormat df;
        private final List<Double> items;

        XConverter(List<Double> items, Unit unit, String format) {
            this.items = items;
            this.unit = unit.text;
            this.df = new DecimalFormat(format);
        }

        @Override
        public String toString(Double value) {
            if (value == null) {
                return "";
            }
            return df.format(value)+ " " + unit;
        }

        @Override
        public Double fromString(String s) {
            try {
                if (s == null || s.trim().isEmpty()) {
                    return null;
                }
                String value = s.replace(unit, "").trim();
                return ListUtils.findClosest(items, df.parse(value).doubleValue());
            } catch (ParseException e) {
                return ListUtils.findClosest(items, getValue());
            }
        }
    }

    public FxSubmittableSpinner(List<Double> items, Unit unit) {
        this(items, unit, "#.##");
    }

    public FxSubmittableSpinner(List<Double> items, Unit unit, String format) {
        this.unit = unit;
        getStyleClass().add("fx-submittable-spinner");
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(
                FXCollections.observableArrayList(items)
        );
        valueFactory.setConverter(new XConverter(items, unit, format));
        setValueFactory(valueFactory);
        setEditable(true);
        getEditor().setAlignment(Pos.BASELINE_RIGHT);

        // Disables buttons without losing focus
        skinProperty().addListener((o, ov, nv) -> {
            if(nv instanceof SpinnerSkin) {
                SpinnerSkin<?> skin = (SpinnerSkin<?>) nv;
                skin.getChildren().stream()
                        .filter(node -> node instanceof StackPane)
                        .forEach(button -> button.disableProperty().bind(indeterminateProperty));
            }
        });

        arm();
        armEditor();
    }

    private void arm() {
        stateProperty.set(getValue());
        lastText = getEditor().getText();
        valueProperty().addListener((o, ov, nv) -> {
            if(state.compareAndSet(State.UPDATING, State.IDLE)) {
                stateProperty.set(nv);
                lastText = getEditor().getText();
            } else if(nv != null) {//&& !editing.get()) {// && !updating.get()) {
                if (state.compareAndSet(State.IDLE, State.SUBMITTING)) {
                    submitting(nv, false);
                } else if (state.compareAndSet(State.UPDATING, State.IDLE)) {
                    idle();
                } else if (state.compareAndSet(State.REVERTING, State.IDLE)) {
                    idle();
                }
            }
        });
    }

    private void armEditor() {
        doNotSelectAllWhenIndeterminate();
        getEditor().setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE) {
                // Clear focus from the editor
                getEditor().getParent().requestFocus();
            }
        });
        getEditor().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if(!event.getCode().equals(KeyCode.TAB)) {
                state.compareAndSet(State.IDLE, State.EDITING);
            }
        });

        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                reselect();
            }
        });

        getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            // can be activated just with tab/next-focus
            if(state.get() == State.EDITING) {
                boolean valid = !isInvalid();
                boolean dirty = valid && isDirty();
                addOrRemove(getStyleClass(), STYLE_INVALID, !valid);
                addOrRemove(getStyleClass(), STYLE_DIRTY, dirty);
                if (event.getCode().equals(KeyCode.ENTER)) {
                    event.consume();
                    superSubmitValue(true);
                }
            }
        });

        getEditor().focusedProperty().addListener((o, ov, nv) -> {
            if (!nv){// lost focus
                superSubmitValue(false);
            } else {
                if(getEditor().isEditable()) {
                    Platform.runLater(() -> getEditor().selectAll());
                }
            }
        });
    }

    private double getTextValue() {
        try {
            return getValueFactory().getConverter().fromString(getEditor().getText());
        } catch (Exception e) { // Invalid entry/text, we revert to previous value
            return stateProperty.get();
        }
    }

    private void superSubmitValue(boolean focusRequired) {
        requiresFocus.set(focusRequired);
        if(state.compareAndSet(State.EDITING, State.SUBMITTING)) {
            double value = getTextValue();
            // Need to check since the closest value might be the current selection
            if(!DoubleUtils.equals(stateProperty.get(), value)) {
                submitting(value, true);
            } else {
                idle();
                getEditor().setText(lastText);
                state.set(State.IDLE);
            }
        }
    }

    private void reselect() {
        Platform.runLater(() -> {
            reset();
            getEditor().selectAll();
        });
    }

    private void focusWhenRequired() {
        if(requiresFocus.get()) {
            Platform.runLater(() -> {
                getEditor().requestFocus();
                getEditor().deselect();
                getEditor().positionCaret(getEditor().getText().length());
                getEditor().selectAll();
            });
            requiresFocus.set(false);
        } else {
            getEditor().positionCaret(getEditor().getText().length());
        }
    }


    private void reset() {
        getEditor().setText(getValueFactory().getConverter().toString(stateProperty.get()));
        addOrRemove(getStyleClass(), STYLE_INVALID, false);
        addOrRemove(getStyleClass(), STYLE_DIRTY, false);
    }

    private void doNotSelectAllWhenIndeterminate() {
        indeterminateProperty.addListener((o, ov, nv) -> {
            if(isEditable()) {
                getEditor().setDisable(nv);//spinner does not lose focus
            }
        });
        getEditor().addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
                Platform.runLater(() -> getEditor().selectAll()));
    }

    private void startTimeout() {
        indeterminate(true);
        pauseTransition = Fx.pauseBeforeRun(timeout, () -> {
            if(state.compareAndSet(State.SUBMITTING, State.REVERTING)) { // No response received
                reverting();
            }
        });
    }

    // Stopping when getting a response (controller.setValue) or reverting
    private void stopTimeout() {
        indeterminate(false);
        if(pauseTransition != null) {
            pauseTransition.stop();
            pauseTransition = null;
        }
    }

    public FxSubmittableSpinner indeterminate(boolean value) {
        indeterminateProperty.set(value);
        setFocusTraversable(!value);
        addOrRemove(getStyleClass(), STYLE_INDETERMINATE, value);
        if(!value) {
            addOrRemove(getStyleClass(), STYLE_INVALID, false);
            addOrRemove(getStyleClass(), STYLE_DIRTY, false);
        }
        return this;
    }

    private void idle() {
        stopTimeout();
        addOrRemove(getStyleClass(), STYLE_DIRTY, false);
        focusWhenRequired();
    }

    // User submitting the value
    private void submitting(Double value, boolean editing) {
        if(value != null && (!editing || (editing && isDirty()))) {
            startTimeout();
            if(consumer!=null) {
                consumer.accept(this, ConsumerValue.set(value, consumerValueOptions));
            }
        }
    }

    private boolean isInvalid() {
        try {
            String text = getEditor().getText().replace(unit.text, "").trim();
            Double.parseDouble(text);
            return text.isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    // Only checked if the value is valid
    private boolean isDirty() {
        return !DoubleUtils.equals(stateProperty.get(), getEditorValue());
    }

    private double getEditorValue() {
        String text = getEditor().getText();
        return Double.parseDouble(text.replace(unit.text, "").trim());
    }

    private void reverting() {
        Double value = stateProperty.get();
        getValueFactory().setValue(value);
        indeterminate(false);
        focusWhenRequired();
        getEditor().setText(lastText);
        state.set(State.IDLE);
    }

    public void set(double rawValue) {
        if(state.compareAndSet(State.SUBMITTING, State.UPDATING) ||
                state.compareAndSet(State.IDLE, State.UPDATING)) {
            stopTimeout();
            double value = ConsumerValue.get(rawValue, consumerValueOptions);
            getValueFactory().setValue(value);
            // Same value won't trigger
            if(DoubleUtils.equals(value, getValue())) {
                stateProperty.set(value);
                lastText = getEditor().getText();
                state.set(State.IDLE);
            }
        }
    }

    public FxSubmittableSpinner minWidth(int width) {
        setMinWidth(width);
        return this;
    }

    public ReadOnlyObjectProperty<Double> stateProperty() {
        return stateProperty;
    }

    public FxSubmittableSpinner consume(BiConsumer<FxSubmittableSpinner, Double> consumer) {
        if(this.consumer!=null) throw new RuntimeException("Consumer already assigned.");
        this.consumer = consumer;
        return this;
    }

    public FxSubmittableSpinner times(int times) {
        this.consumerValueOptions.times = times;
        return this;
    }

    public FxSubmittableSpinner times1E(int times) {
        this.consumerValueOptions.times1E = times;
        return this;
    }

    public FxSubmittableSpinner dividedBy(int x) {
        this.consumerValueOptions.divided = x;
        return this;
    }

    public FxSubmittableSpinner armDisable(BooleanProperty prop) {
        indeterminateProperty.addListener((o, ov, nv) -> prop.set(nv));
        return this;
    }

    public FxSubmittableSpinner addStyleClass(String clazz) {
        getStyleClass().add(clazz);
        return this;
    }

    public FxSubmittableSpinner editable(boolean editable) {
        setEditable(editable);
        return this;
    }

    public FxSubmittableSpinner disable(boolean disable) {
        setDisable(disable);
        return this;
    }

    @Override
    public void dispose() {
        pauseTransition = null;
        lastText = null;
        consumer = null;
    }
}