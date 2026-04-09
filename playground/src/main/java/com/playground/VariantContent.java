package com.playground;

import com.components.Fx;
import com.components.boxes.FxHBox;
import com.components.boxes.FxVBox;
import com.playground.components.PFxLabel;
import com.playground.utils.codeview.CodeView;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;

class VariantContent extends FxVBox {

    VariantContent(String componentJavaPath, String playgroundJavaPath,
                   ComponentVariant variant, BooleanProperty viewCodeProp) {
        super(10, "content");
        setStyle("-fx-background-color: white");

        FxVBox componentBox = Fx.vBox();
        String description = variant.getDescription();
        if(description!=null) componentBox.add(PFxLabel.wrapped(description, 500));
        componentBox.add(variant.getComponent());
        componentBox.setMinWidth(500);

        int minHeight = variant.getMinHeight();
        WebView source = CodeView.get(componentJavaPath, playgroundJavaPath, variant.getClass());
        source.visibleProperty().bind(viewCodeProp);
        FxHBox box = content(componentBox, source, minHeight);
        add(box);
        box.setMinHeight(minHeight);
    }

    private FxHBox content(Node component, WebView source, int minHeight) {
        FxHBox box = Fx.hBox(component, FxHBox.spacer(), source);
        source.managedProperty().bind(source.visibleProperty());
        box.setMinHeight(minHeight);
        box.setMaxHeight(minHeight);
        FxHBox.setHgrow(source, Priority.ALWAYS);
        return box;
    }
}
