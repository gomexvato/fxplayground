package com.components.submittable;


import com.core.utils.ArrayUtils;
import com.core.utils.ObjectUtils;
import com.components.Fx;
import com.components.boxes.FxHBox;
import com.utils.ObservableListUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * Submittable ChoiceBox
 *
 * Supports internally the following states
 * IDLE - ready for action
 * SUBMITTING - submitting value to the backend model
 * UPDATING - updating ui with response from model
 * REVERTING - no response from model, reverting.
 * SETTING_ITEMS - refreshing options/choices
 *
 * @param <T> type of value to be submitted
 */
public class FxSubmittableChoiceBox<T> extends FxHBox implements FxSubmittable {
    private static final String STYLE_CLASS= "fx-submittable-choice-box";
    private static final String STYLE_INDETERMINATE = "fx-indeterminate";
    public final XChoiceBox<T> choicebox;
    public final T fallbackValue;

    private final ObjectProperty<T> controllerProperty = Fx.oProp();
    private final ObjectProperty<T> submittedProperty = Fx.oProp(); //Temp
    private final ObjectProperty<T> stateProperty = Fx.oProp(); //Final

    private Map<T, Pair<T, String>> vMap;

    private enum State {IDLE, SUBMITTING, UPDATING, REVERTING, SETTING_ITEMS}
    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);

    private final Duration DEFAULT_SUBMIT_TIMEOUT = Duration.seconds(5);
    private final Duration timeout;
    private PauseTransition pauseTransition;

    private final IntFunction<T[]> generator;

    private final BooleanProperty indeterminateProperty = Fx.bProp(false);
    private Predicate<T> submittableValueFn;
    private BiConsumer<FxSubmittableChoiceBox<T>, T> consumer;
    private BiConsumer<FxSubmittableChoiceBox<T>, T> unsubmittableValueConsumer;

    private final StringConverter<T> converter;
    private final ConsumerValue.Options valueOptions = new ConsumerValue.Options();

    // Prevents showing menu when value is being submitted
    // Disabling the choicebox is not an option because the component loses focus.
    private final class XChoiceBox<T> extends ChoiceBox<T> {
        private final ObservableList<T> items;
        public XChoiceBox(ObservableList<T> items) {
            this.items = items;
            setItems(items);
        }
        @Override public void show() {if(!indeterminateProperty.get()) {super.show();}}
    }

    @SafeVarargs
    public FxSubmittableChoiceBox(
            T fallbackValue,
            IntFunction<T[]> generator,
            Duration timeout,
            Pair<T, String>... pairs
    ) {
        this(fallbackValue, generator, timeout, null, pairs);
    }

    @SafeVarargs
    public FxSubmittableChoiceBox(
            T fallbackValue,
            IntFunction<T[]> generator,
            Duration timeout,
            Predicate<T> submittableValueFn,
            Pair<T, String>... pairs
    ) {
        this.submittableValueFn = submittableValueFn;
        this.fallbackValue = fallbackValue;
        this.generator = generator;
        this.timeout = timeout == null ? DEFAULT_SUBMIT_TIMEOUT : timeout;
        this.choicebox = new XChoiceBox<>(
                FXCollections.observableArrayList(ArrayUtils.map(pairs, Pair::getKey, generator)));
        this.choicebox.getStyleClass().add(STYLE_CLASS);
        this.vMap = ArrayUtils.reduceToMap(pairs, (m, p) -> m.xput(p.getKey(), p));
        this.converter = new StringConverter<T>() {
            @Override
            public String toString(T value) {
                if (vMap.get(value) != null) return vMap.get(value).getValue();
                throw new RuntimeException("Unexpected value: " + value);
            }

            @Override public T fromString(String s) {return null;}
        };
        this.choicebox.setConverter(converter);
        add(choicebox);
        arm();


    }

    public FxSubmittableChoiceBox<T> indeterminate(boolean value) {
        ObservableListUtils.addOrRemove(choicebox.getStyleClass(), STYLE_INDETERMINATE, value);
        indeterminateProperty.set(value);
        return this;
    }

    public FxSubmittableChoiceBox<T> disable(boolean value) {
        choicebox.setDisable(value);
        return this;
    }

    public FxSubmittableChoiceBox<T> setValue(T t) {
        controllerProperty.set(ConsumerValue.get(t, valueOptions));
        return this;
    }

    public FxSubmittableChoiceBox<T> set(T t) {
        controllerProperty.set(ConsumerValue.get(t, valueOptions));
        return this;
    }

    /**
     * Resubmits value even if the selection is still the same
     */
    public FxSubmittableChoiceBox<T> reset(T t) {
        if(ObjectUtils.equals(stateProperty.get(), t) && consumer!=null) {
            consumer.accept(this, t);
        } else {
            controllerProperty.set(ConsumerValue.get(t, valueOptions));
        }
        return this;
    }

    public FxSubmittableChoiceBox<T> consume(BiConsumer<FxSubmittableChoiceBox<T>, T> xconsumer) {
        if(this.consumer != null) throw new RuntimeException("Consumer already assigned!");
        this.consumer = xconsumer;
        return this;
    }

    public void setDouble(Double value) {
        if(vMap.size() > 0) {
            Object key = vMap.keySet().stream().findFirst().get();
            if (key instanceof Double) {
                setValue((T) value);
            } else if (key instanceof Integer) {
                setValue((T) Integer.valueOf((int) (double) value));
            }
        }
    }

    public T[] getValues() {
        return choicebox.getItems().toArray(generator.apply(0));
    }

    public FxSubmittableChoiceBox<T> minWidth(int width) {
        choicebox.setMinWidth(width);
        return this;
    }

    public FxSubmittableChoiceBox<T> style(String style) {
        choicebox.setStyle(style);
        return this;
    }

    private void arm() {
        // Triggered with V values when user clicks on the box or from the controller listener
        choicebox.valueProperty().addListener((o, ov, nv) -> {
            if(nv!=null && state.compareAndSet(State.IDLE, State.SUBMITTING)) submitting(nv);
            else if(state.compareAndSet(State.UPDATING, State.IDLE)) idle(nv);
            else if(state.compareAndSet(State.REVERTING, State.IDLE) ||
                    (state.compareAndSet(State.SETTING_ITEMS, State.IDLE))) idle(null);
        });

        // Triggered externally with V values to update the view
        controllerProperty.addListener((o, ov, nv) -> {
            if(nv!=null) {
                if(state.compareAndSet(State.IDLE, State.UPDATING) ||
                   state.compareAndSet(State.SUBMITTING, State.UPDATING)) {
                    if(choicebox.getValue() == null || !ObjectUtils.equals(choicebox.getValue(), nv)) {
                        if(vMap.containsKey(nv)) {
                            choicebox.setValue(nv);
                        } else {
                            choicebox.setValue(null);
                            if(state.compareAndSet(State.UPDATING, State.IDLE)) {
                                idle(null);
                            }
                        }
                    } else if(state.compareAndSet(State.UPDATING, State.IDLE)) {
                        idle(nv);
                    }
                }
                stopTimeout();
                stateProperty.set(nv);
            }
        });
    }

    private void submitting(T value) {
        controllerProperty.set(null);
        submittedProperty.set(value);
        startTimeout();
        if(consumer!=null) {
            if(submittableValueFn == null || submittableValueFn.test(value)) {
                consumer.accept(this, ConsumerValue.set(value, valueOptions));
            } else if(submittableValueFn != null) {
                unsubmittableValueConsumer.accept(this, value);
            }
        }
    }

    private void idle(T newValue) {
        if(newValue!=null) stateProperty.set(newValue);
        //clearing in order to submit same value at a later time
        submittedProperty.set(null);
        controllerProperty.set(null);
        stopTimeout();
    }


    private void startTimeout() {
        indeterminate(true);
        pauseTransition = Fx.pauseBeforeRun(timeout, () -> {
            if(state.compareAndSet(State.SUBMITTING, State.REVERTING)) { // No response received
                reverting();
            }
            stopTimeout();
        });
    }

    private void reverting() {
        choicebox.setValue(stateProperty.get());
    }

    private void stopTimeout() {
        indeterminate(false);
        if(pauseTransition!=null) {
            pauseTransition.stop();
            pauseTransition = null;
        }
    }

    public ReadOnlyObjectProperty<T> submittedProperty() {
        return submittedProperty;
    }

    public ReadOnlyObjectProperty<T> selectedProperty() {
        return stateProperty;
    }

    public ReadOnlyBooleanProperty indeterminateProperty() {
        return indeterminateProperty;
    }

    public FxSubmittableChoiceBox<T> submittableValue(Predicate<T> predicateFn,
                                                      BiConsumer<FxSubmittableChoiceBox<T>, T> unsubmittableValueConsumer) {
        this.submittableValueFn = predicateFn;
        this.unsubmittableValueConsumer = unsubmittableValueConsumer;
        return this;
    }

    @SafeVarargs
    public final void setItems(Pair<T, String>... pairs) {
        setItems(null, pairs);
    }

    @SafeVarargs
    public final void setItems(T dftValue, Pair<T, String>... pairs) {
        if(state.compareAndSet(State.IDLE, State.SETTING_ITEMS)) {
            Platform.runLater(() -> {
                this.vMap = ArrayUtils.reduceToMap(pairs, (m, p) -> m.xput(p.getKey(), p));
                choicebox.items.setAll(ArrayUtils.map(pairs, Pair::getKey, generator));
                if(vMap.containsKey(stateProperty.get())) {
                    controllerProperty.set(stateProperty.get());
                } else if(dftValue !=null || fallbackValue!=null) {
                    if(state.compareAndSet(State.SETTING_ITEMS, State.SUBMITTING)||
                        state.compareAndSet(State.IDLE, State.SUBMITTING)) {
                        stateProperty.setValue(null);
                        submitting(dftValue != null ? dftValue : fallbackValue);
                    }
                } else {
                    // This nullifies things
                    controllerProperty.set(stateProperty.get());
                }
            });
        }
    }

    public Pair<T, String>[] getItems() {
        return vMap.values().toArray(new Pair[0]);
    }

    public void submit(T value) {
        if(vMap.containsKey(value) && state.compareAndSet(State.IDLE, State.SUBMITTING)) {
            choicebox.setValue(value);
            submitting(value);
        }
    }

    public FxSubmittableChoiceBox<T> setAllWidths(int width) {
        choicebox.setPrefWidth(width);
        choicebox.setMinWidth(width);
        choicebox.setMaxWidth(width);
        return this;
    }

    public FxSubmittableChoiceBox<T> armDisable(BooleanProperty prop) {
        indeterminateProperty.addListener((o, ov, nv) -> prop.set(nv));
        return this;
    }

    public FxSubmittableChoiceBox<T> times(int value) {
        this.valueOptions.times = value;
        return this;
    }

    public FxSubmittableChoiceBox<T> dividedBy(int value) {
        this.valueOptions.divided = value;
        return this;
    }

    public int selectedIndex() {
        return choicebox.getSelectionModel().getSelectedIndex();
    }

    @Override
    public void dispose() {
        super.dispose();
        pauseTransition = null;
        submittableValueFn = null;
        consumer = null;
        unsubmittableValueConsumer = null;
        if(vMap != null) {
            vMap.clear();
            vMap = null;
        }
    }
}