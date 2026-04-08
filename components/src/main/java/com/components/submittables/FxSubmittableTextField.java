package com.components.submittables;

import com.components.Fx;
import com.components.boxes.FxHBox;
import com.components.converters.Converter;
import com.components.converters.DoubleUnitStringConverter;
import com.core.Unit;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.utils.ObservableListUtils.addOrRemove;

/**
 * Submittable TextField
 *
 * Supports internally the following states
 * IDLE - ready for action
 * SUBMITTING - submitting value to the backend model
 * UPDATING - updating ui with response from model
 * REVERTING - no response from model, reverting.
 *
 * @param <T> type of value to be submitted
 */
public class FxSubmittableTextField<T> extends FxHBox implements FxSubmittable {

    private static class XTextField extends TextField {
        final StackPane lock = new LockIcon();
        private static final String STYLE_CLASS = "fx-submittable-text-field";
        private static final String STYLE_LOCKED = "fx-locked";
        private final BooleanProperty lockedProperty = Fx.bProp();

        XTextField(String value, boolean editable) {
            super(value);
            getStyleClass().addAll(STYLE_CLASS);
            setPrefColumnCount(7);
            setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
            setAlignment(Pos.BASELINE_RIGHT);
            lockedProperty.addListener((o, ov, nv) -> {
                addOrRemove(getChildren(), lock, !nv);
                addOrRemove(getStyleClass(), STYLE_LOCKED, !nv);
            });
            setEditable(editable);
        }
    }

    public final XTextField textfield;
    private final StringConverter<T> converter;

    private final ObjectProperty<T> controllerProperty = Fx.oProp();
    private final ObjectProperty<T> stateProperty = Fx.oProp();
    private final StringProperty submittedProperty = Fx.sProp();
    private final StringProperty invalidMessageProperty = Fx.sProp("");

    // Used when the text field is not bound to any property
    private BiConsumer<FxSubmittableTextField<T>, T> consumer;

    // For cases where the text field is bound to a prop for rendering purposes but
    // the submissions needs to go do a different place
    private BiConsumer<FxSubmittableTextField<T>, T> hijackConsumer;

    private enum State {SUBMITTING, UPDATING, REVERTING, IDLE}
    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);

    private final BooleanProperty indeterminateProperty = Fx.bProp(false);
    private final AtomicBoolean requiresFocus = new AtomicBoolean(false);
    private final AtomicBoolean editing = new AtomicBoolean(false);

    private final String STYLE_DIRTY = "fx-dirty";
    private final String STYLE_INVALID = "fx-invalid";

    private final Duration DEFAULT_TIMEOUT_SECS = Duration.seconds(5);
    private final Duration timeout;
    private PauseTransition pauseTransition;

    private final ConsumerValue.Options valueOptions = new ConsumerValue.Options();
    private final Pair<Predicate<T>, String>[] validations;
    private final Function<T, String> isValidFn;

    public FxSubmittableTextField() {
        this(null, Unit.NONE, true, null, null, null, null);
    }

    public FxSubmittableTextField(String format, Unit unit) {
        this(format, unit, true, null, null, null, null);
    }

    public FxSubmittableTextField(String format, Unit unit, Pair<Predicate<T>, String>... validations) {
        this(format, unit, null, validations);
    }

    public FxSubmittableTextField(BiConsumer<FxSubmittableTextField<T>, T> consumer) {
        this(null, null, true, null, consumer, null, null);
    }

    public FxSubmittableTextField(String format, Unit unit,
                                  BiConsumer<FxSubmittableTextField<T>, T> consumer,
                                  Pair<Predicate<T>, String>... validations) {
        this(format, unit, true, null, consumer, null, null, validations);
    }

    public FxSubmittableTextField(String format, Unit unit,
                                  BiConsumer<FxSubmittableTextField<T>, T> consumer,
                                  Function<T, String> isValidFn) {
        this(format, unit, true, null, consumer, null, isValidFn);
    }

    public FxSubmittableTextField(String format, Unit unit, BiConsumer<FxSubmittableTextField<T>, T> consumer) {
        this(format, unit, true, null, consumer, null, null);
    }

    public FxSubmittableTextField(String format, Unit unit, boolean editable,
                                  BiConsumer<FxSubmittableTextField<T>, T> consumer) {
        this(format, unit, editable, null, consumer, null, null);
    }

    private FxSubmittableTextField(
            String format,
            Unit unit,
            boolean editable,
            StringConverter<T> xconverter,
            BiConsumer<FxSubmittableTextField<T>, T> consumer,
            Duration timeout,
            Function<T, String> isValidFn,
            Pair<Predicate<T>, String>... validations
    ) {
        this.textfield = new XTextField("", editable);
        this.isValidFn = isValidFn;
        this.validations = validations;
        this.converter = xconverter != null ? xconverter : unit == null ? new StringConverter<T>() {
            @Override public String toString(T t) { return t.toString(); }
            @Override public T fromString(String s) { return (T) s; }
        } : (StringConverter<T>) new DoubleUnitStringConverter(format, unit);
        stateProperty.set(converter.fromString(""));
        this.consumer = consumer;
        this.timeout = timeout == null ? DEFAULT_TIMEOUT_SECS : timeout;
        add(textfield);
        arm();
    }

    public FxSubmittableTextField<T> submit(T value) {
        state.set(State.SUBMITTING);
        textfield.setText(converter.toString(value));
        submitting(value, true);
        return this;
    }

    public FxSubmittableTextField<T> set(T value) {
        controllerProperty.set(ConsumerValue.get(value, valueOptions));
        return this;
    }

    public void setDouble(Double value) {
        if(converter instanceof DoubleStringConverter) {
            controllerProperty.set(ConsumerValue.get((T) value, valueOptions));
        }
    }

    public FxSubmittableTextField<T> minWidth(int width) {
        textfield.setMinWidth(width);
        return this;
    }

    public FxSubmittableTextField<T> times(Integer value) {
        this.valueOptions.times = value;
        return this;
    }

    public FxSubmittableTextField<T> times1E(Integer value) {
        this.valueOptions.times1E = value;
        return this;
    }

    public FxSubmittableTextField<T> dividedBy(Integer value) {
        this.valueOptions.divided = value;
        return this;
    }

    /**
     * Submits whatever the user enters with an opposite sign value
     */
    public FxSubmittableTextField<T> negated() {
        return negated(true);
    }

    public FxSubmittableTextField<T> negated(boolean value) {
        this.valueOptions.negated = value;
        return this;
    }

    public FxSubmittableTextField<T> limits(double min, double max) {
        this.valueOptions.min = min;
        this.valueOptions.max = max;
        return this;
    }

    private void arm() {
        // Triggered with text when user clicks enter or field loses focus
        submittedProperty.addListener((o, ov, nv) -> {
            if(nv!=null) {
                if (state.compareAndSet(State.UPDATING, State.IDLE)) {
                    textfield.setText(nv);
                    idle(converter.fromString(nv));
                } else if (state.compareAndSet(State.REVERTING, State.IDLE)) {
                    textfield.setText(nv);
                    idle(null);
                } else if (state.compareAndSet(State.IDLE, State.SUBMITTING)) {
                    submitting(converter.fromString(nv));
                }
            }
        });

        // Triggered externally with V values to update the view or when updating/reverting
        controllerProperty.addListener((o, ov, nv) -> {
            if(nv!=null) {
                if (state.compareAndSet(State.IDLE, State.UPDATING) ||
                        state.compareAndSet(State.SUBMITTING, State.UPDATING)) {
                    try {
                        submittedProperty.set(converter.toString(nv));
                    } catch(Exception e) {
                        if(state.compareAndSet(State.UPDATING, State.REVERTING)) {
                            reverting();
                        }
                    }
                }
            }
        });
        textfield.addEventFilter(KeyEvent.ANY, e -> {
            if(indeterminateProperty.get()) { e.consume(); }
        });
        textfield.addEventFilter(MouseEvent.ANY, e -> {
            if(indeterminateProperty.get()) { e.consume(); }
        });
        textfield.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> Platform.runLater(() -> {
            if(!indeterminateProperty.get()) {
                textfield.selectAll();
            }
        }));
        textfield.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if(!event.getCode().equals(KeyCode.TAB)) {
                editing.set(true);

                boolean invalid = isInvalid();
                boolean dirty = !invalid && isDirty();
                addOrRemove(textfield.getStyleClass(), STYLE_INVALID, invalid);
                addOrRemove(textfield.getStyleClass(), STYLE_DIRTY, dirty);
                if (event.getCode().equals(KeyCode.ENTER)) {
                    event.consume();
                    Platform.runLater(textfield::requestFocus);
                    requiresFocus.set(true);
                    submitValue();
                } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                    reselect();
                }
            }
        });

        textfield.focusedProperty().addListener((o, ov, nv) -> {
            if(!nv && editing.get()) {
                if (isInvalid()) {
                    reset();
                } else if (isDirty()) {
                    requiresFocus.set(false);
                    submitValue();
                } else if(!textfield.getText().equals(converter.toString(getValue()))) {
                    textfield.setText(converter.toString(getValue()));
                }
            }
        });
    }

    // controller updating the value
    // value is null when reverting
    private void idle(T value) {
        if(value!=null) {
            stateProperty.set(value);
        }
        stopTimeout();
        //clearing in order to submit same value at a later time
        submittedProperty.set(null);
        controllerProperty.set(null);
        addOrRemove(textfield.getStyleClass(), STYLE_DIRTY, false);
        if(value!=null) { focusWhenRequired(); }
    }

    private void submitting(T value) {
        submitting(value, false);
    }

    // User submitting the value
    private void submitting(T value, boolean force) {
        if(value != null && (force || isDirty())) {
            startTimeout();
            if(hijackConsumer!=null) {
                hijackConsumer.accept(this, ConsumerValue.set(value, valueOptions));
            } else if(consumer!=null) {
                consumer.accept(this, ConsumerValue.set(value, valueOptions));
            }
        }
        submittedProperty.set(null);
    }

    private void submitValue() {
        if(!isInvalid()) {
            if (textfield.isEditable() && isDirty()) {
                submittedProperty.set(textfield.getText());
                editing.set(false);
            } else if (editing.get() && !isDirty()) {
                reselect();
            }
        }
    }

    private void reselect() {
        Platform.runLater(() -> {
            reset();
            textfield.selectAll();
        });
    }

    private boolean isInvalid() {
        try {
            if(getValue()==null || textfield.getText().isEmpty()) {
                invalidMessageProperty.set("");
                return true;
            }
            return !validate();
        } catch (Exception e) {
            //e.printStackTrace();
            return true;
        }
    }

    public BooleanBinding hasInvalidValue() {
        return invalidMessageProperty().isNotEmpty();
    }

    // Returns true if valid, false otherwise
    private boolean validate() {
        invalidMessageProperty.set("");
        T value = ConsumerValue.set(getValue(), valueOptions);
        if(validations != null) {
            for (Pair<Predicate<T>, String> validation : validations) {
                if (validation.getKey().test(value)) {
                    invalidMessageProperty.set(validation.getValue());
                    return false;
                }
            }
        }
        if(isValidFn!=null) {
            String msg = isValidFn.apply(value);
            if(msg != null) {
                invalidMessageProperty.set(msg);
                return false;
            }
        }
        return true;
    }

    private boolean isDirty() {
        return editing.get() && stateProperty.get() != null &&
                !stateProperty.get().equals(getValue());
    }

    private T getValue() {
        return converter.fromString(textfield.getText());
    }

    private void reverting() {
        if(stateProperty.get()!=null) {
            T value = stateProperty.get();
            if(value instanceof String) {
                submittedProperty.set((String)value);
            } else if(converter!=null) {
                submittedProperty.set(converter.toString(value));
            }
        } else {
            submittedProperty.set("");
        }
    }

    private void reset() {
        textfield.setText(converter.toString(stateProperty.get()));
        addOrRemove(textfield.getStyleClass(), STYLE_INVALID, false);
        addOrRemove(textfield.getStyleClass(), STYLE_DIRTY, false);
        invalidMessageProperty.set("");
    }

    // Started only when submitting a value
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

    private void focusWhenRequired() {
        if(requiresFocus.get()) {
            Platform.runLater(() -> {
                textfield.requestFocus();
                textfield.deselect();
                textfield.positionCaret(textfield.getText().length());
                textfield.selectAll();
                textfield.requestFocus();
            });
            requiresFocus.set(false);
        }
    }

    public FxSubmittableTextField<T> focus() {
        textfield.requestFocus();
        return this;
    }

    public FxSubmittableTextField<T> indeterminate(boolean value) {
        indeterminateProperty.set(value);
        textfield.setEditable(!value);
        textfield.setFocusTraversable(!value);
        addOrRemove(textfield.getStyleClass(), "fx-indeterminate", value);
        return this;
    }

    public FxSubmittableTextField<T> disable(boolean value) {
        setDisable(value);
        return this;
    }

    public ReadOnlyStringProperty invalidMessageProperty() {
        return invalidMessageProperty;
    }

    public ReadOnlyStringProperty submittedProperty() {
        return submittedProperty;
    }

    public ReadOnlyObjectProperty<T> stateProperty() {
        return stateProperty;
    }

    public FxSubmittableTextField<T> consume(BiConsumer<FxSubmittableTextField<T>, T> xconsumer) {
        if(this.consumer!=null) throw new RuntimeException("Consumer already assigned!");
        this.consumer = xconsumer;
        return this;
    }

    public FxSubmittableTextField<T> hijackConsumer(BiConsumer<FxSubmittableTextField<T>, T> xconsumer) {
        this.hijackConsumer = xconsumer;
        return this;
    }

    @Override
    public FxSubmittableTextField<T> addStyleClass(String clazz) {
        textfield.getStyleClass().add(clazz);
        return this;
    }

    public FxSubmittableTextField<T> armDisable(BooleanProperty prop) {
        indeterminateProperty.addListener((o, ov, nv) -> prop.set(nv));
        return this;
    }

    public static FxSubmittableTextField<Double> milliOhms() {
        return new FxSubmittableTextField<>("0.000", Unit.MILLI_OHMS);
    }

    public static FxSubmittableTextField<Double> milliAmps() {
        return new FxSubmittableTextField<>("0.0", Unit.MILLI_AMPS);
    }

    public static FxSubmittableTextField<Double> microSecs() {
        return new FxSubmittableTextField<>("0", Unit.MICRO_SECS);
    }

    public static FxSubmittableTextField<Double> milliSecs() {
        return new FxSubmittableTextField<>("0", Unit.MILLI_SECS);
    }

    public static FxSubmittableTextField<Double> decimalVolts() {
        return new FxSubmittableTextField<>("0.0", Unit.VOLTS);
    }

    public static FxSubmittableTextField<Double> watts() {
        return new FxSubmittableTextField<>("0.0", Unit.WATTS);
    }

    public static FxSubmittableTextField<Double> amps() {
        return new FxSubmittableTextField<>("0.0", Unit.AMPS);
    }

    public static FxSubmittableTextField<Double> mvA() {
        return new FxSubmittableTextField<>("0.000", Unit.MV_A);
    }

    public static FxSubmittableTextField<Double> mvW() {
        return new FxSubmittableTextField<>("0.000", Unit.MV_W);
    }

    public static FxSubmittableTextField<Double> celsius() {
        return new FxSubmittableTextField<>("0", Unit.CELSIUS);
    }

    public static FxSubmittableTextField<Double> kHz() {
        return new FxSubmittableTextField<>("0", Unit.KHZ);
    }

    public static FxSubmittableTextField<Double> hex() {
        return new FxSubmittableTextField<>(null, null, true, Converter.hex, null, null, null);
    }

    public static FxSubmittableTextField<Double> dTextField() {
        return new FxSubmittableTextField<>(null, null, true, Converter.doubles, null, null, null);
    }

    public void setUnitFormat(Unit unit, String format) {
        if(converter instanceof DoubleUnitStringConverter) {
            DoubleUnitStringConverter xconverter = (DoubleUnitStringConverter) converter;
            double value = xconverter.fromString(textfield.getText());
            xconverter.setUnitFormat(unit, format);
            textfield.setText(xconverter.toString(value));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        consumer = null;
        pauseTransition = null;
    }
}