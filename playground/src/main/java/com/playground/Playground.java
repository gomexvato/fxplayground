package com.playground;

import com.core.utils.DoubleUtils;
import com.core.utils.StringUtils;
import com.components.boxes.FxHBox;
import com.playground.components.*;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import com.components.Fx;

// Showcase app for the different components in this module
public class Playground extends Application {
    private static final double DEFAULT_DIVIDER = 0.2;
    private PFxTree<ComponentVariant[]> tree;
    private SplitPane splitPane;
    private PFxLink hamburgerLink;
    private boolean paneResizing = false;
    private boolean loading = true;
    private Double previousDivider = DEFAULT_DIVIDER;
    private static Stage primaryStage;
    private static final String version = "0.0.6";//TODO needed ?????
    private Persister persister;
    private String componentJavaPath, playgroundJavaPath;
    private final BooleanProperty viewCodeProp = new SimpleBooleanProperty(false);

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        String docsPath = getParameters().getRaw().get(0);

        String jPath = System.getProperty("user.dir");//+"/src/playground/java";
        componentJavaPath = new File(jPath).getAbsolutePath()+"/src/main/java";
        playgroundJavaPath = new File(jPath).getAbsolutePath()+"/../Playground/src/main/java";
        String persisterPath = getParameters().getRaw().get(1);
        this.persister = Persister.instance(new File(persisterPath));
        this.hamburgerLink = hamburger();
        FilesystemComponents fsc = FilesystemComponents.api.apply(docsPath);
        this.tree = new PFxTree<>(
                "Fx",
                fsc.getCategories(),
                //ArrayUtils.map(fsc.getCategories(), c -> StringUtils.convertToCamelCase(c), String[]::new),
                cat -> fsc.getSubcategories(cat),
                name -> StringUtils.convertToCamelCase(name)
        );

        BorderPane pane = new BorderPane(new Main());
        pane.setBottom(bottomBox());

        this.splitPane = new SplitPane(sidebar(), pane);
        splitPane.setDividerPosition(0, DEFAULT_DIVIDER);
        PFxScene scene = new PFxScene(splitPane, 1200, 700);
        scene.getStylesheets().addAll(stylesheets());
        stage.setScene(scene);
        stage.getIcons().add(PFxImage.image("fx.png"));
        stage.setUserData(tree);
        armPersister();
        stage.show();
        loading = false;
    }

    protected String[] stylesheets() {
        return new String[]{"styles/playground.css"};
    }

    private Node sidebar() {
        PFxVBox.setVgrow(tree, Priority.ALWAYS);
        tree.treeView.setShowRoot(false);
        return new PFxVBox(tree);
    }

    private Pane bottomBox() {
        return Fx.vBox(FxHBox.addRight(Fx.link("Source Code", () ->
                viewCodeProp.set(!viewCodeProp.get())
        ))).style("-fx-padding: 5px 30px;-fx-pref-height:30px;-fx-background-color:#efefef");
    }

    private class Main extends PFxVBox {
        Main() {
            super(0);
            PFxScrollPane scrollPane = new PFxScrollPane(content());
            add(title(), scrollPane);
            setStyle("-fx-border-color: #ddd;-fx-border-width: 10;");
            PFxVBox.setVgrow(scrollPane, Priority.ALWAYS);
        }

        private Node title() {
            PFxLabel titleLabel = PFxLabel.h3("FxComponents");
            PFxHBox box = new PFxHBox(new PFxVBox(hamburgerLink).style("-fx-alignment:center"), titleLabel);
            box.setStyle("-fx-padding: 0 5 0 5;-fx-background-color: white;");
            tree.selectedNameProperty().addListener((o, ov, nv) -> {
                if (nv != null) {
                    titleLabel.setText(nv);
                }
            });
            return box;
        }

        private Node content() {
            PFxVBox box = new PFxVBox("playground-main");
            PFxLabel description = new PFxLabel(
                    "Application to showcase, document and test components in this module.");
            description.setStyle("-fx-padding: 5");
            box.add(description);
            tree.selectedProperty().addListener((o, ov, nv) -> {
                box.clear();
                if (nv != null) {
                    ComponentVariant[] variants = nv;
                    for (ComponentVariant variant : variants) {
                        PFxTitledPane titledPane = new PFxTitledPane(
                                PFxLabel.h4(variant.getTitle()),
                                new VariantContent(
                                        componentJavaPath,
                                        playgroundJavaPath,
                                        variant,
                                        viewCodeProp
                                ));
                        titledPane.getStyleClass().add("playground-variant");
                        box.add(titledPane);
                    }
                    if(!loading) CompletableFuture.runAsync(()-> persister.persist(data()));
                }
            });
            return box;
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private void armPersister() {
        Stage stage = primaryStage;
        stage.setOnCloseRequest(event -> persister.persist(data()));
        ChangeListener listener = (o, ov, nv) -> {if(!loading && hasDimensions()) persister.persist(data());};
        hamburgerLink.selectedProperty().addListener(listener);
        stage.widthProperty().addListener(listener);
        stage.heightProperty().addListener(listener);
        stage.xProperty().addListener(listener);
        stage.yProperty().addListener(listener);
        splitPane.getDividers().get(0).positionProperty().addListener((o, ov, nv) -> {
            if(nv!=null && DoubleUtils.round((Double) ov, 1) != DoubleUtils.round((Double) nv, 1) && !paneResizing) {
                paneResizing = true;
            }
        });

        Data data = persister.retrieve(Data.class);
        if(data != null) restoreData(data);
    }

    private PFxLink hamburger() {
        PFxLink hamburger = PFxLink.hamburger();
        hamburger.selectedProperty().addListener((o, ov, nv) -> {
            if(nv) {
                previousDivider = DoubleUtils.round(splitPane.getDividerPositions()[0], 1);
                splitPane.setDividerPosition(0, previousDivider < 0.1 ? DEFAULT_DIVIDER : 0);
            } else {
                splitPane.setDividerPosition(0, previousDivider);
            }
        });
        return hamburger;
    }

    private boolean hasDimensions() {
        return !Double.isNaN(primaryStage.getWidth()) &&
                !Double.isNaN(primaryStage.getHeight()) &&
                !Double.isNaN(primaryStage.getX()) &&
                !Double.isNaN(primaryStage.getY());
    }

    private Data data() {
        return new Data(version, tree.data());
    }

    private void restoreData(Data data) {
        if(!data.version.equals(version)) { return; }
        tree.restore(data.treeData);
        primaryStage.setWidth(data.width);
        primaryStage.setHeight(data.height);
        primaryStage.setX(data.x);
        primaryStage.setY(data.y);
        if(data.divider>=0) {
            splitPane.setDividerPosition(0, data.divider);
            if(data.divider == 0) previousDivider = DEFAULT_DIVIDER;
        }
    }

    private class Data {
        final String version;
        final double width;
        final double height;
        final double x;
        final double y;
        final double divider;
        final PFxTree.Data treeData;
        Data(String version, PFxTree.Data treeData) {
            this.version = version;
            this.width = primaryStage.getWidth();
            this.height = primaryStage.getHeight();
            this.x = primaryStage.getX();
            this.y = primaryStage.getY();
            this.treeData = treeData;
            double[] positions = splitPane.getDividerPositions();
            this.divider = positions.length > 0 ? DoubleUtils.round(positions[0], 1) : DEFAULT_DIVIDER;
        }
    }
}