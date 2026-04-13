package com.components.submittable;

import com.core.utils.DoubleUtils;
import com.components.Fx;
import com.utils.ObservableListUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.function.BiConsumer;

/**
 * Submittable CheckBox
 *
 * Supports internally the following states
 * IDLE - ready for action
 * SUBMITTING - submitting value to the backend model
 * UPDATING - updating ui with response from model
 * REVERTING - no response from model, reverting.
 *
 * @param <T> type of value to be submitted
 */
public class FxSubmittableCheckBox<T> extends AbstractOnOffComponent<CheckBox, T> implements FxSubmittable<T> {
    private final static String STYLE_CLASS = "fx-submittable-check-box";
    private final static String STYLE_INDETERMINATE = "fx-indeterminate";
    private final BooleanProperty indeterminateProperty = Fx.bProp(false);
    private BiConsumer<FxSubmittableCheckBox<T>, T> consumer;

    private static class XCheckBox extends CheckBox {
        public XCheckBox(String label) {
            super(label);
            getStyleClass().add(STYLE_CLASS);
            if(label == null) {
                setStyle("-fx-padding: 0 -6px 0 0");
            }
        }
    }

    public FxSubmittableCheckBox(
            String label,
            T selectedValue,
            T unselectedValue,
            Duration timeout
    ) {
        super(new XCheckBox(label), selectedValue, unselectedValue, timeout);
    }

    @Override
    protected void arm() {
        super.arm();
        component.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if(indeterminateProperty.get()) {
                event.consume();
            }
        });
    }

    @Override protected BooleanProperty internalProperty() { return component.selectedProperty(); }
    @Override protected void setInternal(boolean value) {
        Platform.runLater(() -> {
            component.setSelected(value);
        });
    }
    @Override public boolean isSelected() { return component.isSelected(); }
    @Override public FxSubmittableCheckBox<T> disable(boolean value) { component.setDisable(value); return this; }
    @Override public FxSubmittableCheckBox<T> indeterminate(boolean value) {
        indeterminateProperty.set(value);
        ObservableListUtils.addOrRemove(component.getStyleClass(), STYLE_INDETERMINATE, value);
        return this;
    }
    @Override protected void consume(T value) { if (consumer != null) { consumer.accept(this, value); }}

    public FxSubmittableCheckBox<T> set(T value) {
        controllerProperty.set(value);
        return this;
    }

    public void setDouble(Double value) {
        double dValue = value;
        int iValue = (int) dValue;
        if(onValue instanceof Boolean) {
            controllerProperty.set(iValue == 1 ? onValue : offValue);
        } else if(onValue instanceof Double) {
            controllerProperty.set(DoubleUtils.equals(value, (Double) onValue) ? onValue : offValue);
        } else if(onValue instanceof Integer) {
            controllerProperty.set(iValue == (Integer) onValue ? onValue : offValue);
        }
    }

    public FxSubmittableCheckBox<T> setFalse() { return set(offValue); }
    public FxSubmittableCheckBox<T> setTrue() { return set(onValue); }

    public FxSubmittableCheckBox<T> consume(BiConsumer<FxSubmittableCheckBox<T>, T> xconsumer) {
        if(this.consumer != null) throw new RuntimeException("Consumer already assigned!");
        this.consumer = xconsumer;
        return this;
    }

    public FxSubmittableCheckBox<T> armDisable(BooleanProperty prop) {
        indeterminateProperty.addListener((o, ov, nv) -> prop.set(nv));
        return this;
    }

    @Override
    public void dispose() {
        super.dispose();
        consumer = null;
    }
}