package com.components.submittable;

import com.components.Fx;
import com.components.boxes.FxHBox;
import com.utils.ObservableListUtils;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/**
 * Submittable RadioButton that can be toggled
 *
 * Supports internally the following states
 * IDLE - ready for action
 * SUBMITTING - submitting value to the backend model
 * UPDATING - updating ui with response from model
 * REVERTING - no response from model, reverting.
 *
 * @param <T> type of value to be submitted
 */
public class FxSubmittableRadioBox<T> extends FxHBox {
    public final ObjectProperty<T> controller = Fx.oProp();
    public final BooleanProperty disableProperty = Fx.bProp();
    private final ObjectProperty<T> selectedProperty = Fx.oProp();
    private final BooleanProperty stateProperty = Fx.bProp();
    private final BooleanProperty indeterminateProperty = Fx.bProp(false);

    public final RadioButton button = new RadioButton();
    private final T onValue;
    private final T offValue;
    private final BiConsumer<ObjectProperty<T>, T> consumer;

    private enum State {IDLE, SUBMITTING, UPDATING, REVERTING}
    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);

    private final int DEFAULT_SUBMIT_TIMEOUT_SECS = 5;
    private int timeoutSecs = DEFAULT_SUBMIT_TIMEOUT_SECS;
    private PauseTransition pauseTransition;

    private final static String STYLE_CLASS = "fx-submittable-radio-box";
    private final static String STYLE_INDETERMINATE = "fx-indeterminate";

    public static FxSubmittableRadioBox<Boolean> box() {
        return new FxSubmittableRadioBox<>(true, false, null);
    }

    public static FxSubmittableRadioBox<Integer> iBox() {
        return new FxSubmittableRadioBox<>(1, 0, null);
    }

    public static FxSubmittableRadioBox<Double> dBox() {
        return new FxSubmittableRadioBox<>(1.0, 0.0, null);
    }

    public FxSubmittableRadioBox(
        T onValue,
        T offValue,
        BiConsumer<ObjectProperty<T>, T> consumer
    ) {
        this.onValue = onValue;
        this.offValue = offValue;
        this.consumer = consumer;
        button.getStyleClass().add(STYLE_CLASS);
        add(button);
        arm();
    }

    private void arm() {
        // Similar to disable but without losing focus
        button.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if(disableProperty.get() || indeterminateProperty.get()) {
                event.consume();
            }
        });

        disableProperty.addListener((o, ov, nv) ->
                ObservableListUtils.addOrRemove(button.getStyleClass(), "fx-disabled", nv));

        // Triggered only when submitting a value
        selectedProperty.addListener((o, ov, nv) -> {
            if(nv!=null) {
                controller.set(null);
                startTimeout();
                if (consumer != null) consumer.accept(controller, nv);
            }
        });
        // Triggered by: user click, controller or when reverting
        button.selectedProperty().addListener((o, ov, nv) -> {
            if(state.compareAndSet(State.IDLE, State.SUBMITTING)) submitting(nv ? onValue : offValue);
            if(state.compareAndSet(State.UPDATING, State.IDLE)) idle(nv);
            if(state.compareAndSet(State.REVERTING, State.IDLE)) idle(null);
        });
        // Triggered by controller.set(value)
        controller.addListener((o, ov, nv) -> {
            if(nv!=null) {
                if(state.compareAndSet(State.IDLE, State.UPDATING) ||
                        state.compareAndSet(State.SUBMITTING, State.UPDATING)) {
                    boolean newSelection = nv.equals(onValue);
                    if(button.isSelected() != newSelection) {
                        button.setSelected(newSelection);
                    } else if(state.compareAndSet(State.UPDATING, State.IDLE)) {
                        idle(newSelection);
                    }
                }
            }
        });
    }

    private void idle(Boolean newValue) {
        if(newValue!=null) stateProperty.set(newValue);
        //clearing in order to submit same value at a later time
        selectedProperty.set(null);
        stopTimeout();
    }

    private void submitting(T value) {
        selectedProperty.set(value);
    }

    // Started only when submitting a value
    private void startTimeout() {
        indeterminate(true);
        pauseTransition = Fx.pauseBeforeRun(Duration.seconds(timeoutSecs), () -> {
            if(state.compareAndSet(State.SUBMITTING, State.REVERTING)) { // No response received
                button.setSelected(stateProperty.get());
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

    private T selectedValue() {
        return button.isSelected() ? onValue : offValue;
    }

    public FxSubmittableRadioBox<T> select(T value) {
        controller.set(value);
        return this;
    }

    public FxSubmittableRadioBox<T> select(boolean value) {
        select(value ? onValue : offValue);
        return this;
    }

    public FxSubmittableRadioBox<T> disable(boolean value) {
        button.setDisable(value);
        return this;
    }

    public FxSubmittableRadioBox<T> indeterminate(boolean value) {
        indeterminateProperty.set(value);
        ObservableListUtils.addOrRemove(button.getStyleClass(), STYLE_INDETERMINATE, value);
        return this;
    }

    public boolean isSelected() {
        return stateProperty().get();
    }

    /**
     * Read only property to keep track of user clicks (partial selection).
     * It is only set internally with T or null values.
     * The final state of the radio button is kept on the stateProperty when
     * the external model confirms the update/submission.
     *
     * Listeners to this property should only act on non-null values.
     * Nullifying/resetting this property after being set is necessary to
     * allow the listener to act on multiple events with the same value.
     *
     * @return read only property with T or null
     */
    public ReadOnlyObjectProperty<T> selectedProperty() {
        return selectedProperty;
    }

    /**
     * This property keeps track of the final value (not partial)
     * of the radio button.
     * The component submits a selection (partial value)
     * through the selectedProperty and updates
     * the stateProperty when a response is received
     * via the controller.
     *
     * @return property with true/false
     */
    public ReadOnlyBooleanProperty stateProperty() {
        return stateProperty;
    }

    public FxSubmittableRadioBox<T> armDisable(BooleanProperty prop) {
        indeterminateProperty.addListener((o, ov, nv) -> prop.set(nv));
        return this;
    }

    @Override
    public void dispose() {
        super.dispose();
        pauseTransition = null;
    }
}