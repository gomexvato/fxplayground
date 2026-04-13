package com.components.submittable;

import com.core.utils.ArrayUtils;
import com.components.Fx;
import com.components.boxes.FxVBox;
import com.utils.ObservableListUtils;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;

public class FxSubmittableComboBox<T> extends FxVBox {
    private static final String STYLE_CLASS= "fx-submittable-combo-box";
    private static final String STYLE_INDETERMINATE = "fx-indeterminate";
    public final ComboBox<T> combobox;

    private final ObjectProperty<T> controllerProperty = Fx.oProp();
    private final ObjectProperty<T> submittedProperty = Fx.oProp(); //Temp
    private final ObjectProperty<T> stateProperty = Fx.oProp(); //Final

    private Map<T, Pair<T, String>> vMap;

    private enum State {IDLE, SUBMITTING, UPDATING, REVERTING}
    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);

    private final Duration DEFAULT_SUBMIT_TIMEOUT = Duration.seconds(5);
    private final Duration timeout;
    private PauseTransition pauseTransition;

    private final IntFunction<T[]> generator;

    private final BooleanProperty indeterminateProperty = Fx.bProp(false);
    private BiConsumer<FxSubmittableComboBox<T>, T> consumer;

    private final StringConverter<T> converter;
    private final ConsumerValue.Options valueOptions = new ConsumerValue.Options();

    public FxSubmittableComboBox(IntFunction<T[]> generator, Duration timeout, Pair<T, String>[] pairs) {
        this.generator = generator;
        this.vMap = ArrayUtils.reduceToMap(pairs, (m, p) -> m.xput(p.getKey(), p));
        this.timeout = timeout == null ? DEFAULT_SUBMIT_TIMEOUT : timeout;
        this.combobox = new ComboBox<>();
        combobox.getStyleClass().add(STYLE_CLASS);
        combobox.setItems(FXCollections.observableArrayList(ArrayUtils.map(pairs, Pair::getKey, generator)));
        this.converter = new StringConverter<T>() {
            @Override
            public String toString(T value) {
                if (vMap.get(value) != null) return vMap.get(value).getValue();
                return "";
            }

            @Override public T fromString(String s) {return null;}
        };
        combobox.setConverter(converter);
        combobox.setEditable(false);
        add(combobox);
        arm();
    }

    public FxSubmittableComboBox<T> indeterminate(boolean value) {
        ObservableListUtils.addOrRemove(combobox.getStyleClass(), STYLE_INDETERMINATE, value);
        indeterminateProperty.set(value);
        return this;
    }

    public FxSubmittableComboBox<T> disable(boolean value) {
        combobox.setDisable(value);
        return this;
    }

    public FxSubmittableComboBox<T> setValue(T t) {
        controllerProperty.set(ConsumerValue.get(t, valueOptions));
        return this;
    }

    public FxSubmittableComboBox<T> set(T t) {
        controllerProperty.set(ConsumerValue.get(t, valueOptions));
        return this;
    }

    public FxSubmittableComboBox<T> consume(BiConsumer<FxSubmittableComboBox<T>, T> xconsumer) {
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
                double dValue = value;
                int intValue = (int) dValue;
                setValue((T) Integer.valueOf(intValue));
            }
        }
    }

    public T[] getValues() {
        return combobox.getItems().toArray(generator.apply(0));
    }

    public FxSubmittableComboBox<T> minWidth(int width) {
        combobox.setMinWidth(width);
        return this;
    }

    private void arm() {
        // Triggered with V values when user clicks on the box or from the controller listener
        combobox.valueProperty().addListener((o, ov, nv) -> {
            if(nv!=null && state.compareAndSet(State.IDLE, State.SUBMITTING)) submitting(nv);
            else if(state.compareAndSet(State.UPDATING, State.IDLE)) idle(nv);
            else if(state.compareAndSet(State.REVERTING, State.IDLE)) idle(null);
        });

        // Triggered externally with V values to update the view
        controllerProperty.addListener((o, ov, nv) -> {
            if(nv!=null) {
                if(state.compareAndSet(State.IDLE, State.UPDATING) ||
                        state.compareAndSet(State.SUBMITTING, State.UPDATING)) {
                    if(combobox.getValue() != nv) {
                        if(vMap.containsKey(nv)) {
                            combobox.setValue(nv);
                        } else {
                            combobox.setValue(null);
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
            consumer.accept(this, ConsumerValue.set(value, valueOptions));
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
        combobox.setValue(stateProperty.get());
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

    public Pair<T, String>[] getItems() {
        return vMap.values().toArray(new Pair[0]);
    }

    public void submit(T value) {
        if(vMap.containsKey(value) && state.compareAndSet(State.IDLE, State.SUBMITTING)) {
            combobox.setValue(value);
            submitting(value);
        }
    }

    public FxSubmittableComboBox<T> setAllWidths(int width) {
        combobox.setPrefWidth(width);
        combobox.setMinWidth(width);
        combobox.setMaxWidth(width);
        return this;
    }

    public FxSubmittableComboBox<T> times(int value) {
        this.valueOptions.times = value;
        return this;
    }

    public FxSubmittableComboBox<T> armDisable(BooleanProperty prop) {
        indeterminateProperty.addListener((o, ov, nv) -> prop.set(nv));
        return this;
    }

    @Override
    public void dispose() {
        super.dispose();
        pauseTransition = null;
        consumer = null;
        if(vMap != null) {
            vMap.clear();
            vMap = null;
        }
    }
}