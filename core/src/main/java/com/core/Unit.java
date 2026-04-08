package com.core;

public enum Unit {
    NONE(""),
    MILLI_OHMS("m\u03a9"),
    KILO_OHMS("k\u03a9"),
    MICRO_SECS("\u03BCs"),
    NANO_SECS("ns"),
    MILLI_SECS("ms"),
    VOLTS("V"),
    MILLI_VOLTS("mV"),
    AMPS("A"),
    MILLI_AMPS("mA"),
    MV_A("mv/A"),
    MV_W("mv/W"),
    MV_MICRO_SECS("mv/\u03BCs"),
    WATTS("W"),
    CELSIUS("degC"),
    KHZ("kHz");

    public final String text;

    Unit(String text) {
        this.text = text;
    }
}
