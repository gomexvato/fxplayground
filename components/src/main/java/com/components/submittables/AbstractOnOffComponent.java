package com.components.submittable;

import com.components.Fx;
import com.components.boxes.FxHBox;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.Control;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Abstract class to support submittable
 * components with two states.
 *
 * @param <C> Type of java fx Control
 * @param <T> Object type
 */
abstract class AbstractOnOffComponent<C extends Control, T> extends FxHBox {
    protected final ObjectProperty<T> controllerProperty = Fx.oProp();
    private final ObjectProperty<T> submittedProperty = Fx.oProp();
    private final BooleanProperty stateProperty = Fx.bProp();
    public final C component;
    protected final T onValue;
    protected final T offValue;

    private enum State {IDLE, SUBMITTING, UPDATING, REVERTING}
    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);

    private static final Duration DEFAULT_SUBMIT_TIMEOUT = Duration.seconds(5);
    private final Duration timeout;
    private PauseTransition pauseTransition;

    AbstractOnOffComponent(C component, T onValue, T offValue, Duration timeout) {
        this.component = component;
        this.onValue = onValue;
        this.offValue = offValue;
        this.timeout = timeout == null ? DEFAULT_SUBMIT_TIMEOUT : timeout;
        arm();
        add(component);
    }

    protected abstract BooleanProperty internalProperty();
    protected abstract void setInternal(boolean value);
    public abstract boolean isSelected();
    public abstract AbstractOnOffComponent<C,T> disable(boolean value);
    public abstract AbstractOnOffComponent<C,T> indeterminate(boolean value);
    protected abstract void consume(T value);

    protected void arm() {
        // Triggered only when submitting a value
        submittedProperty.addListener((o, ov, nv) -> {
            if(nv!=null) {
                controllerProperty.set(null);
                startTimeout();
                consume(nv);
            }
        });
        // Triggered by: user click, controller or when reverting
        internalProperty().addListener((o, ov, nv) -> {
            if(state.compareAndSet(State.IDLE, State.SUBMITTING)) submitting(nv ? onValue : offValue);
            if(state.compareAndSet(State.UPDATING, State.IDLE)) idle(nv);
            if(state.compareAndSet(State.REVERTING, State.IDLE)) idle(null);
        });
        // Triggered by controller.set(value)
        controllerProperty.addListener((o, ov, nv) -> {
            if(nv!=null) {
                if(state.compareAndSet(State.IDLE, State.UPDATING) ||
                   state.compareAndSet(State.SUBMITTING, State.UPDATING)) {
                    boolean newSelection = nv.equals(onValue);
                    if(isSelected() != newSelection) {
                        setInternal(newSelection);
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
        submittedProperty.set(null);
        stopTimeout();
        if(component.focusedProperty().get()) {
            component.requestFocus();
        }
    }

    private void submitting(T value) {
        submittedProperty.set(value);
    }

    private void reverting() {
        setInternal(stateProperty.get());
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

    private T selectedValue() {
        return isSelected() ? onValue : offValue;
    }

    public AbstractOnOffComponent<C, T> select(T value) {
        controllerProperty.set(value);
        return this;
    }

    public AbstractOnOffComponent<C, T> select(boolean value) {
        select(value ? onValue : offValue);
        return this;
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
    public ReadOnlyObjectProperty<T> submittedProperty() {
        return submittedProperty;
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
    public ReadOnlyBooleanProperty selectedProperty() {
        return stateProperty;
    }

    @Override
    public void dispose() {
        super.dispose();
        pauseTransition = null;
    }
}
