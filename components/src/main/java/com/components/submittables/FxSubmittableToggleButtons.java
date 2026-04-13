package com.components.submittable;

import com.core.utils.ArrayUtils;
import com.components.Fx;
import com.components.boxes.FxHBox;
import com.components.boxes.FxVBox;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;


/**
 * Submittable ToggleButtons
 *
 * Supports internally the following states
 * IDLE - ready for action
 * SUBMITTING - submitting value to the backend model
 * UPDATING - updating ui with response from model
 * REVERTING - no response from model, reverting.
 *
 * @param <T> type of value to be submitted
 */
public class FxSubmittableToggleButtons<T> extends FxHBox implements FxSubmittable<T> {
    private final boolean strictMode; // throws exception for invalid values

    private enum State {IDLE, SUBMITTING, UPDATING, REVERTING}
    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);

    private final Map<T, FxSubmittableToggleButton<T>> valueMap;
    private final Map<ToggleButton, T> toggleMap = new HashMap<>();

    private final ToggleGroup group = new ToggleGroup();
    private final ObjectProperty<T> submittedProperty = Fx.oProp();
    private final ObjectProperty<T> stateProperty = Fx.oProp();
    private final ObjectProperty<T> controllerProperty = Fx.oProp();
    private final BooleanProperty indeterminateProperty = Fx.bProp(false);

    private static final Duration DEFAULT_SUBMIT_TIMEOUT = Duration.seconds(5);
    private final Duration timeout;
    private PauseTransition pauseTransition;

    private BiConsumer<FxSubmittableToggleButtons<T>, T> consumer;

    @SafeVarargs
    public FxSubmittableToggleButtons(Pair<T, String>... pairs) {
        this(null, pairs);
    }

    @SafeVarargs
    public FxSubmittableToggleButtons(boolean strictMode, Pair<T, String>... pairs) {
        this(false, null, null, strictMode, pairs);
    }

    @SafeVarargs
    public FxSubmittableToggleButtons(Integer minWidth, Pair<T, String>... pairs) {
        this(minWidth, null, pairs);
    }

    @SafeVarargs
    public FxSubmittableToggleButtons(Integer minWidth, Duration timeout, Pair<T, String>... pairs) {
        this(false, minWidth, timeout, true, pairs);
    }

    @SafeVarargs
    private FxSubmittableToggleButtons(boolean vertical, Integer minWidth,
                                       Duration timeout, boolean strictMode,
                                       Pair<T, String>... pairs) {
        super(5);
        this.strictMode = strictMode;
        this.timeout = timeout == null ? DEFAULT_SUBMIT_TIMEOUT : timeout;
        FxVBox vBox = Fx.vBox();
        this.valueMap = ArrayUtils.reduceToMap(pairs, (a, p) -> {
            FxSubmittableToggleButton<T> tb = toggleButton(p.getValue(), p.getKey(), minWidth);
            a.xput(p.getKey(), tb);
            toggleMap.put(tb.button, p.getKey());
            if(vertical) {
                vBox.add(tb);
            } else {
                getChildren().add(tb);
            }
            return a;
        });
        if(vertical) getChildren().add(vBox);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        arm();
    }

    private FxSubmittableToggleButton<T> toggleButton(String text, T value, Integer minWidth) {
        FxSubmittableToggleButton<T> button = new FxSubmittableToggleButton<>(text, value, minWidth, group);
        button.setUserData(value);
        return button;
    }

    private void arm() {
        group.selectedToggleProperty().addListener((o, ov, nv) -> {
            submittedProperty.set(toggleMap.get(nv));
        });
        submittedProperty.addListener((o, ov, nv) -> {
            if(nv != null) {
                if (state.compareAndSet(State.IDLE, State.SUBMITTING)) {
                    indeterminateProperty.set(true);
                    valueMap.forEach((v, t) -> t.temporaryDisabled(true));
                    valueMap.get(nv).indeterminate(true);
                    if (consumer != null) {
                        consumer.accept(this, nv);
                    }
                    startTimeout();
                }
                if (state.compareAndSet(State.UPDATING, State.IDLE)) {
                    valueMap.forEach((v, t) -> t.indeterminate(false));
                    indeterminateProperty.set(false);
                    if(!valueMap.containsKey(nv) && strictMode) throw new RuntimeException("Unexpected value: "+nv);
                    if(valueMap.containsKey(nv)) {
                        group.selectToggle(valueMap.get(nv).button);
                        stateProperty.set(nv);
                    } else {
                        group.selectToggle(null);
                        stateProperty.set(null);
                    }
                    stopTimeout();
                }
                if (state.compareAndSet(State.REVERTING, State.IDLE)) {
                    valueMap.forEach((v, t) -> t.indeterminate(false));
                    indeterminateProperty.set(false);
                    group.selectToggle(valueMap.get(nv).button);
                }
            }
            submittedProperty.set(null);
            controllerProperty.set(null);//clear and get it ready for next even if same value
        });
        controllerProperty.addListener((o, ov, nv) -> {
            if(nv != null) {
                if (state.compareAndSet(State.IDLE, State.UPDATING) ||
                        state.compareAndSet(State.SUBMITTING, State.UPDATING)) {
                    submittedProperty.set(nv);
                }
            }
        });
    }

    // Started only when submitting a value
    private void startTimeout() {
        pauseTransition = Fx.pauseBeforeRun(timeout, () -> {
            if(state.compareAndSet(State.SUBMITTING, State.REVERTING)) { // No response received
                //Nothing is selected when user clicks an option and there is no response
                if(submittedProperty.get() == null && stateProperty.get() == null) {
                    valueMap.forEach((v, t) -> t.indeterminate(false));
                    group.selectToggle(null);
                    state.set(State.IDLE);
                } else {
                    submittedProperty.set(stateProperty.get());
                }
            }
        });
    }

    // Stopping when getting a response (controller.setValue) or reverting
    private void stopTimeout() {
        if(pauseTransition != null) {
            pauseTransition.stop();
            pauseTransition = null;
        }
    }

    public ReadOnlyObjectProperty<T> submittedProperty() { return submittedProperty; }
    public ReadOnlyObjectProperty<T> selectedProperty() { return stateProperty; }

    public FxSubmittableToggleButtons<T> set(T value) {
        controllerProperty.set(value);
        return this;
    }

    public void setDouble(Double value) {
        Object key = valueMap.keySet().stream().findFirst().get();
        if(key instanceof Double) {
            controllerProperty.set((T) value);
        } else if(key instanceof Integer) {
            double dValue = value;
            int intValue = (int) dValue;
            controllerProperty.set((T) Integer.valueOf(intValue));
        }
    }

    public FxSubmittableToggleButtons<T> consume(BiConsumer<FxSubmittableToggleButtons<T>, T> xconsumer) {
        if(this.consumer != null) throw new RuntimeException("Consumer already defined!");
        this.consumer = xconsumer;
        return this;
    }

    public FxSubmittableToggleButtons<T> minWidth(int width) {
        toggleMap.keySet().forEach(b -> b.setMinWidth(width));
        return this;
    }

    public FxSubmittableToggleButtons<T> width(int width) {
        return width(width, null);
    }

    public FxSubmittableToggleButtons<T> width(int width, String padding) {
        toggleMap.keySet().forEach(b -> {
            if(padding!=null) b.setStyle("-fx-padding: "+padding);
            b.setMinWidth(width);
            b.setPrefWidth(width);
            b.setMaxWidth(width);
        });
        return this;
    }

    public FxSubmittableToggleButtons<T> armDisable(BooleanProperty prop) {
        valueMap.values().forEach(t -> t.indeterminateProperty().addListener((o, ov, nv) -> prop.set(nv)));
        return this;
    }

    public FxSubmittableToggleButtons<T> spacing(int value) {
        setSpacing(value);
        return this;
    }

    public static <T> FxSubmittableToggleButtons<T> vertical(Pair<T, String>[] pairs) {
        return new FxSubmittableToggleButtons<>(true, null, null, true, pairs);
    }

    public ReadOnlyBooleanProperty indeterminateProperty() {
        return indeterminateProperty;
    }

    @Override
    public void dispose() {
        super.dispose();
        pauseTransition = null;
        consumer = null;
    }
}