package com.components.converters;

import com.core.Unit;
import javafx.util.StringConverter;

class IntegerUnitStringConverter extends StringConverter<Integer> {

    private final String unit;

    public IntegerUnitStringConverter(Unit unit) {
        this.unit = unit.text;
    }


    @Override
    public String toString(Integer value) {
        return value == null ? "" : value + " " + unit;
    }

    @Override
    public Integer fromString(String s) {
        if(s == null || s.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(s.replace(unit, "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
