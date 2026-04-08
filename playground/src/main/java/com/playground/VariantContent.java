package com.playground;

import javafx.beans.property.BooleanProperty;
import com.playground.components.PFxHBox;
import com.playground.components.PFxLabel;
import com.playground.components.PFxVBox;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;
import com.playground.utils.codeview.CodeView;

class VariantContent extends PFxVBox {

    VariantContent(String componentJavaPath, String playgroundJavaPath,
                   ComponentVariant variant, BooleanProperty viewCodeProp) {
        super(10, "content");
        setStyle("-fx-background-color: white");

        PFxVBox componentBox = new PFxVBox();
        String description = variant.getDescription();
        if(description!=null) componentBox.add(PFxLabel.wrapped(description, 500));
        componentBox.add(variant.getComponent());
        componentBox.setMinWidth(500);

        int minHeight = variant.getMinHeight();
        WebView source = CodeView.get(componentJavaPath, playgroundJavaPath, variant.getClass());
        source.visibleProperty().bind(viewCodeProp);
        PFxHBox box = content(componentBox, source, minHeight);
        add(box);
        box.setMinHeight(minHeight);
    }

    private PFxHBox content(Node component, WebView source, int minHeight) {
        PFxHBox box = new PFxHBox(component, PFxHBox.spacer(), source);
        source.managedProperty().bind(source.visibleProperty());
        box.setMinHeight(minHeight);
        box.setMaxHeight(minHeight);
        PFxHBox.setHgrow(source, Priority.ALWAYS);
        return box;
    }
}
