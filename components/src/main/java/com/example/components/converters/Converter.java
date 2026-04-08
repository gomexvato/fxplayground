package com.example.components.converters;

import com.example.core.Unit;
import javafx.util.StringConverter;

public class Converter {

    public static StringConverter<Double> mvA = new DoubleUnitStringConverter("0.00", Unit.MV_A);
    public static StringConverter<Double> mvW = new DoubleUnitStringConverter("0.00", Unit.MV_W);
    public static StringConverter<Double> decimalVolts = new DoubleUnitStringConverter("0.0", Unit.VOLTS);
    public static StringConverter<Double> milliOhms = new DoubleUnitStringConverter("0.000", Unit.MILLI_OHMS);
    public static StringConverter<Double> milliOhms(String format) {
        return new DoubleUnitStringConverter(format, Unit.MILLI_OHMS);
    }
    public static StringConverter<Double> milliAmps = new DoubleUnitStringConverter("0.0", Unit.MILLI_AMPS);
    public static StringConverter<Double> kOhms = new DoubleUnitStringConverter("0.000", Unit.KILO_OHMS);
    public static StringConverter<Double> amps = new DoubleUnitStringConverter("0.0", Unit.AMPS);
    public static StringConverter<Double> watts = new DoubleUnitStringConverter("0.0", Unit.WATTS);
    public static StringConverter<Double> celsius = new DoubleUnitStringConverter("0", Unit.CELSIUS);
    public static StringConverter<Double> kHz = new DoubleUnitStringConverter("0", Unit.KHZ);
    public static StringConverter<Double> microSecs = new DoubleUnitStringConverter("0", Unit.MICRO_SECS);
    public static StringConverter<Integer> iMicroSecs = new IntegerUnitStringConverter(Unit.MICRO_SECS);

    public static StringConverter<Double> milliSecs = new DoubleUnitStringConverter("0", Unit.MILLI_SECS);

    public static StringConverter<Integer> ints = new StringConverter<Integer>() {
        @Override public String toString(Integer object) {return String.valueOf(object);}
        @Override public Integer fromString(String s) {return Integer.parseInt(s);}
    };

    public static StringConverter<Double> doubles = new DoubleUnitStringConverter(null, Unit.NONE) {
        @Override public String toString(Double object) {return String.valueOf(object);}
        @Override public Double fromString(String s) {
            if(s.trim().isEmpty()) {
                return null;
            }
            return Double.parseDouble(s);
        }
    };

    public static StringConverter<Double> hex = new DoubleUnitStringConverter(null, Unit.NONE) {
        @Override public String toString(Double object) {
            return "0x" + Integer.toHexString(object.intValue()).toUpperCase();
        }

        @Override public Double fromString(String string) {
            string = string.replace("0x", "").replace("0X", "");
            try {
                return Integer.valueOf(string, 16).doubleValue();
            } catch(NumberFormatException nfe) {
                return 0.0;
            }
        }
    };
}