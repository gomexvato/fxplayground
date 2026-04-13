package com.components.submittable;

import com.components.Fx;
import com.components.boxes.FxHBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.utils.ObservableListUtils.addOrRemove;

/**
 * Submittable Toggle buttons
 *
 * Supports internally the following states
 * IDLE - ready for action
 * SUBMITTING - submitting value to the backend model
 * UPDATING - updating ui with response from model
 *
 * @param <T> type of value to be submitted
 */
public class FxSubmittableToggleButton<T> extends FxHBox {
    private final static String STYLE_CLASS = "fx-submittable-toggle-button";
    private final static String STYLE_INDETERMINATE = "fx-indeterminate";
    private final static String STYLE_TEMP_DISABLE = "fx-temp-disable";

    private static class XToggleButton extends ToggleButton {
        public XToggleButton(String text, ToggleGroup group) {
            super(text);
            getStyleClass().add(STYLE_CLASS);
            setToggleGroup(group);
        }

        @Override
        public void fire() {
            if(!isDisable() && !isSelected()) {
                setSelected(!isSelected()); //Only toggle if not already selected
                fireEvent(new ActionEvent());
            }
        }
    }

    public final XToggleButton button;
    private final T value;
    private final BooleanProperty indeterminateProperty = Fx.bProp(false);
    private final AtomicBoolean disabledTemporarily = new AtomicBoolean(false);

    public FxSubmittableToggleButton(String text, T value) {
        this(text, value, null);
    }

    public FxSubmittableToggleButton(String text, T value, Integer minWidth) {
        this(text, value, minWidth, null);
    }

    FxSubmittableToggleButton(String text, T value, Integer minWidth, ToggleGroup group) {
        this.button = new XToggleButton(text, group);
        this.button.setUserData(this);
        this.button.getStyleClass().add(STYLE_CLASS);
        this.value = value;
        if(minWidth!=null) button.setMinWidth(minWidth);
        add(button);
        arm();
    }

    private void arm() {
        // Prevents click when button is submitting a value.
        // This is similar to setting the button to disable but
        // with the benefit that the component stays focused.
        button.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if(indeterminateProperty.get()) {
                event.consume();
            }
        });
    }

    public FxSubmittableToggleButton<T> addStyleClass(String clazz) {
        button.getStyleClass().add(clazz);
        return this;
    }

    public FxSubmittableToggleButton<T> disable(boolean value) {
        button.setDisable(value);
        return this;
    }

    public FxSubmittableToggleButton<T> indeterminate(boolean value) {
        indeterminateProperty.set(value);
        addOrRemove(button.getStyleClass(), STYLE_INDETERMINATE, value);
        if(!value) {
            addOrRemove(button.getStyleClass(), STYLE_TEMP_DISABLE, false);
            disabledTemporarily.set(false);
        }
        return this;
    }

    // Prevents clicks but stays focused
    public FxSubmittableToggleButton<T> temporaryDisabled(boolean value) {
        disabledTemporarily.set(value);
        addOrRemove(button.getStyleClass(), STYLE_TEMP_DISABLE, value);
        return this;
    }

    public ReadOnlyBooleanProperty indeterminateProperty() {
        return indeterminateProperty;
    }

    public FxSubmittableToggleButton<T> selected(boolean value) {
        button.setSelected(value);
        return this;
    }
}
