package com.components;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Tooltip;

public class FxTooltip extends Tooltip {

    public FxTooltip(StringProperty prop) {
        this(prop.get());
        prop.addListener((o, ov, nv) -> setText(nv));
    }

    public FxTooltip(String msg) {
        super(msg);
    }
}
