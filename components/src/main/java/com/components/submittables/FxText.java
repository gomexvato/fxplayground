package com.components.submittable;

import com.core.Unit;
import com.components.converters.Converter;
import com.components.converters.DoubleUnitStringConverter;
import com.components.boxes.FxVBox;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 *  Simple text with an attached converter
 */
public class FxText extends FxVBox {
    private final StringConverter<Double> converter;
    private final Text text;

    public FxText(Unit unit, String format) {
        this.converter = new DoubleUnitStringConverter(format, unit);
        this.text = new Text();
        add(text);
        style("-fx-padding: 5 0 0 0");
    }

    public FxText setText(String value) {
        text.setText(value);
        return this;
    }

    public FxText set(double value) {
        text.setText(converter.toString(value));
        return this;
    }

    public FxText setDouble(Double value) {
        set(value);
        return this;
    }

    public void setUnitFormat(Unit unit, String format) {
        if(converter instanceof DoubleUnitStringConverter) {
            DoubleUnitStringConverter xconverter = (DoubleUnitStringConverter) converter;
            double value = xconverter.fromString(text.getText());
            xconverter.setUnitFormat(unit, format);
            text.setText(xconverter.toString(value));
        }
    }

    public FxText bind(StringExpression binding) {
        text.textProperty().bind(binding);
        return this;
    }
}
