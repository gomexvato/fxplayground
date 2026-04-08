package com.components;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;

public class FxBinds {
    public static <T> ObjectBinding<T> when(BooleanProperty p, T then, T otherwise) {
        return new When(p).then(then).otherwise(otherwise);
    }
}
