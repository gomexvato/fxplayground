package com.docs.components.submittables.fx_submittable_text_field;

import com.components.Fx;
import com.components.buttons.FxLink;
import com.components.submittables.FxSubmittableTextField;
import javafx.scene.Node;
import com.playground.ComponentVariant;

public class Variant2 implements ComponentVariant {
    @Override public String getTitle() { return "State-View Separation"; }
    @Override public String getDescription() {
        return "Controls to simulate submitting/updating to/from an external model.";
    }
    @Override public Node getComponent() {
        FxSubmittableTextField<Double> tf1 = FxSubmittableTextField.milliOhms().set(1.0);
        FxSubmittableTextField<Double> tf2 = FxSubmittableTextField.amps().focus();

        FxLink l1 = Fx.link("2.2", () -> tf1.set(2.2));
        FxLink l2 = Fx.link("3.3", () -> tf1.set(3.3));
        return Fx.hBox(
            Fx.vBox(Fx.hBox(l1, l2), tf1, tf2)
        ).spacing(50);
    }
}
