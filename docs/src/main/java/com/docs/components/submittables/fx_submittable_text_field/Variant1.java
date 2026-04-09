package com.docs.components.submittables.fx_submittable_text_field;

import com.components.Fx;
import javafx.scene.Node;
import com.playground.ComponentVariant;

public class Variant1 implements ComponentVariant {
    @Override public String getTitle() { return "States"; }
    @Override public String getDescription() {
        return "All states in one place to simplify updating and verifying styles.";
    }
    @Override public int getMinHeight() { return 300; }
    @Override public Node getComponent() {
        return Fx.vBox(
                Fx.textField().set("editable"),
                Fx.textField().set("dirty").addStyleClass("fx-dirty"),
                Fx.textField().set("invalid").addStyleClass("fx-invalid"),
                Fx.textField(false).set("locked"),
                Fx.textField().set("submitting").indeterminate(true),
                Fx.textField().set("disabled").disable(true)
        );
    }
}
