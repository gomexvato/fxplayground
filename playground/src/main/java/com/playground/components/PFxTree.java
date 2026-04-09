package com.playground.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Only used by playground. Not generic yet.
public class PFxTree<T> extends StackPane {
    private final StringProperty selectedNameProperty = new SimpleStringProperty();
    private final ObjectProperty<T> selectedProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<PFxTreeItem> selectedItemProperty = new SimpleObjectProperty<>(null);
    private final Map<String, T> leaves = new HashMap<>();
    private final Function<String, String> nodeNameRendererFn;
    public final TreeView treeView;

    /**
     * Single selectable Tree
     * @param rootName tree's root name
     * @param cats tree's main category names
     * @param subCatsFn <"CategoryName", <"SubCategoryName", SubCategories>>
     * @param nodeNameRendererFn function to render proper node names. It can be null.
     */
    public PFxTree(
            String rootName,
            String[] cats,
            Function<String, Map<String, Map<String, T>>> subCatsFn,
            Function<String, String> nodeNameRendererFn
    ) {
        this.nodeNameRendererFn = nodeNameRendererFn;
        TreeItem rootItem = new TreeItem(rootName);
        rootItem.setExpanded(true);
        for(String categoryName: Arrays.stream(cats).sorted().collect(Collectors.toList())) {
            PFxTreeItem item = new PFxTreeItem(renderName(categoryName), categoryName);

            rootItem.getChildren().add(item);
            Map<String, Map<String, T>> subCats = subCatsFn.apply(categoryName);
            for(String subCatName: subCats.keySet().stream().sorted().collect(Collectors.toList())) {
                PFxTreeItem subItem = new PFxTreeItem(renderName(subCatName), subCatName);
                item.getChildren().add(subItem);
                Map<String, T> subCatsMap = subCats.get(subCatName);
                for (String subCat : subCatsMap.keySet().stream().sorted().collect(Collectors.toList())) {
                    PFxTreeItem subSubItem = new PFxTreeItem(renderName(subCat), subCat);
                    subItem.getChildren().add(subSubItem);
                    leaves.put(leafId(subCatName, subCat), subCatsMap.get(subCat));
                }
            }
        }
        treeView = new TreeView(rootItem);
        treeView.setCellFactory(tree -> {
            TreeCell<String> cell = new TreeCell<String>(){
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) setText(null); else setText(item);
                }
            };
            cell.setOnMouseClicked(e -> {
                PFxTreeItem selection = (PFxTreeItem) cell.getTreeItem();
                if(selection !=null && selection.isLeaf()) {
                    selectedItemProperty.set((PFxTreeItem) cell.getTreeItem());
                }
            });
            return cell;
        });
        selectedItemProperty.addListener((o, ov, nv) -> {
            T selection = leaves.get(leafId(
                    ((PFxTreeItem)nv.getParent()).getUserData().toString(),
                    nv.getUserData().toString()));
            selectedProperty.set(selection);
            selectedNameProperty.set(nv.getValue().toString());
        });
        getChildren().add(treeView);
    }

    private String renderName(String name) {
        if(nodeNameRendererFn == null) return name;
        return nodeNameRendererFn.apply(name);
    }

    private String leafId(String subcat, String comp) {
        return (subcat+comp).replaceAll(" ", "-");
    }

    public StringProperty selectedNameProperty() {
        return selectedNameProperty;
    }

    public ObjectProperty<T> selectedProperty() {
        return selectedProperty;
    }

    public void selectIndex(int idx) {
        MultipleSelectionModel model = treeView.getSelectionModel();
        model.select(idx);
        PFxTreeItem selection = (PFxTreeItem) model.getSelectedItem();
        if(selection!=null) {
            selection.setExpanded(true);
            selectedItemProperty.set(selection);
        }
    }

    public void selectIndices(int... indices) {
        MultipleSelectionModel model = treeView.getSelectionModel();
        PFxTreeItem selection = null;
        for(int idx: indices) {
            model.select(idx);
            selection = (PFxTreeItem) model.getSelectedItem();
            selection.setExpanded(true);
        }
        if(selection!=null) selectedItemProperty.set(selection);
    }

    // Provides current selections
    public Data data() {
        Map<Integer, Map<Integer, Boolean>> map = new HashMap<>();
        TreeItem root = treeView.getRoot();
        for(int i=0;i<root.getChildren().size();i++) {
            TreeItem cat = (TreeItem) root.getChildren().get(i);
            Map<Integer, Boolean> submap = new HashMap<>();
            if(cat.isExpanded()) {
                for(int j=0;j<cat.getChildren().size();j++) {
                    submap.put(j, ((TreeItem) cat.getChildren().get(j)).isExpanded());
                }
            }
            map.put(i, submap);
        }
        return new Data(map, treeView.getSelectionModel().getSelectedIndex());
    }

    // Restores selections
    public void restore(Data data) {
        if(data==null) return;
        TreeItem root = treeView.getRoot();
        try {
            for (int i = 0; i < root.getChildren().size(); i++) {
                TreeItem cat = (TreeItem) root.getChildren().get(i);
                if (!data.selections.get(i).isEmpty()) {
                    cat.setExpanded(true);
                    for (int j = 0; j < cat.getChildren().size(); j++) {
                        ((TreeItem) cat.getChildren().get(j))
                                .setExpanded(data.selections.get(i).get(j));
                    }
                }
            }
            if(data.selectedIdx>=0) selectIndex(data.selectedIdx);
        } catch(Exception ignore) {
            // Tree might change when adding new components
        }
    }

    public class Data {
        final Map<Integer, Map<Integer, Boolean>> selections;
        final int selectedIdx;

        Data(Map<Integer, Map<Integer, Boolean>> selections, int selectedIdx) {
            this.selections = selections;
            this.selectedIdx = selectedIdx;
        }
    }
}
