package com.example.components.converters;


import com.example.core.Unit;
import javafx.util.converter.DoubleStringConverter;

import java.text.DecimalFormat;

public class DoubleUnitStringConverter extends DoubleStringConverter {
    protected Unit unit;
    private String format;
    private DecimalFormat formatter;

    public DoubleUnitStringConverter(String format, Unit unit) {
        this.unit = unit;
        this.format = format;
        this.formatter = new DecimalFormat(format + " " + unit.text);
    }

    public void setUnitFormat(Unit unit, String format) {
        this.format = format;
        this.unit = unit;
        this.formatter = new DecimalFormat(format + " " + unit.text);
    }

    @Override
    public String toString(Double value) {
        return formatter.format(value);
    }

    @Override public Double fromString(String s) {
        try {
            return Double.parseDouble(s.replace(unit.text, "").trim());
        } catch(Exception e) {
            return null;
        }
    }
}
