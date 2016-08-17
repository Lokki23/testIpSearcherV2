package com.Igor.SearchIp.Containers;

import com.Igor.SearchIp.Siec6;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Игорь on 11.08.2016.
 */


public class TreeViewManager {
    private TreeItem<Siec6> rootNode =
            new TreeItem<>(new Siec6("Main", "", "", "", "", "", "", ""));
    TreeView<Siec6> treeView;

    private List<Siec6> data;

    public TreeView<Siec6> getTreeView() {
        return treeView;
    }

    public TreeViewManager(TreeView treeView) {

        final ContextMenu contextMenu = new ContextMenu();
        contextMenu.setOnShowing(event -> System.out.println("Showing"));
        contextMenu.setOnShown(event -> System.out.println("Show"));

        Menu addItem = new Menu("Add");
        MenuItem addHomeSiec = new MenuItem("Home network", new ImageView(new Image("Icons/network.png")));
        MenuItem addFreeSiec = new MenuItem("Free network", new ImageView(new Image("Icons/open_network.png")));
        MenuItem addBusySiec = new MenuItem("Busy network", new ImageView(new Image("Icons/close_network.png")));

        addItem.getItems().addAll(addHomeSiec, addFreeSiec, addBusySiec);


        addHomeSiec.setOnAction(event -> {
            TreeItem<Siec6> siec = (TreeItem<Siec6>) treeView.getSelectionModel().getSelectedItem();
            if(siec != null){
                new AddSiecDialog(siec, this, AddSiecDialog.NETWORK_TYPE.HOME_NETWORK).show();
            }else
                new AddSiecDialog(rootNode, this, AddSiecDialog.NETWORK_TYPE.HOME_NETWORK).show();
        });
        addFreeSiec.setOnAction(event -> {
            TreeItem<Siec6> siec = (TreeItem<Siec6>) treeView.getSelectionModel().getSelectedItem();
            if(siec != null){
                new AddSiecDialog(siec, this, AddSiecDialog.NETWORK_TYPE.FREE_NETWORK).show();
            }else
                new AddSiecDialog(rootNode, this, AddSiecDialog.NETWORK_TYPE.FREE_NETWORK).show();
        });
        addBusySiec.setOnAction(event -> {
            TreeItem<Siec6> siec = (TreeItem<Siec6>) treeView.getSelectionModel().getSelectedItem();
            if(siec != null){
                new AddSiecDialog(siec, this, AddSiecDialog.NETWORK_TYPE.BUSY_NETWORK).show();
            }else
                new AddSiecDialog(rootNode, this, AddSiecDialog.NETWORK_TYPE.BUSY_NETWORK).show();
        });

        MenuItem item2 = new MenuItem("Delete");
        item2.setOnAction(e -> {
            TreeItem<Siec6> siec = (TreeItem<Siec6>) treeView.getSelectionModel().getSelectedItem();
            if(siec != null){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Delete network: " + siec.getValue());
                alert.setContentText("Are you sure?");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    remove(siec);
                    siec.getParent().getChildren().remove(siec);
                    treeView.getSelectionModel().clearSelection();
                    upload();
                }
            }
        });
        contextMenu.getItems().addAll(addItem, item2);

        rootNode.setExpanded(true);
        this.treeView = treeView;
        this.treeView.setRoot(rootNode);
        treeView.setContextMenu(contextMenu);

        treeView.setCellFactory(e -> new CustomCell());
    }


    public void setData(List<Siec6> Siec6List){

        data = Siec6List;
    }

    public List<Siec6> getData() {
        return data;
    }

    public void upload() {
        Collections.sort(data,(o1, o2) -> o1.isBigger(o2));
        rootNode.getChildren().clear();
        for (Siec6 siec :
                data) {
            TreeItem<Siec6> empLeaf = new TreeItem<Siec6>(siec);
            TreeItem<Siec6> node = searchParent(rootNode, siec);
            node.getChildren().add(empLeaf);
        }
    }

    private TreeItem<Siec6> searchParent(TreeItem<Siec6> node, Siec6 siec){
        for (TreeItem<Siec6> depNode : node.getChildren()){
            if(siec.thisIsParentNetwortk(depNode.getValue())){
                return searchParent(depNode, siec);
            }
        }
        return node;
    }

    public TreeItem<Siec6> find(TreeItem<Siec6> startNode,Siec6 siec){
        for(TreeItem<Siec6> depNode : startNode.getChildren()){
            if(depNode.getValue().equals(siec)){
                return depNode;
            }else{
                TreeItem<Siec6> node = find(depNode, siec);
            if(node == null)
                continue;
            else return node;
            }
        }
        return null;
    }

    public TreeItem<Siec6> find(TreeItem<Siec6> startNode, String column, String value){

        for(TreeItem<Siec6> depNode : startNode.getChildren()){
            if(depNode.getValue().getValue(column).equals(value)){
                return depNode;
            }else {
                TreeItem<Siec6> node = find(depNode, column, value);
                if(node == null)
                    continue;
                else return node;
            }
        }
        return null;
    }

    public void selectItems(TreeItem<Siec6> start, String key, String value){
        for(TreeItem<Siec6> depNode : start.getChildren()){
            if(depNode.getValue().getValue(key) != null){
                if(depNode.getValue().getValue(key).equals(value))
                    treeView.getSelectionModel().select(depNode);
            }
            selectItems(depNode, key, value);
        }
    }
    public void selectItem(TreeItem<Siec6> start, Siec6 siec){
        for(TreeItem<Siec6> depNode : start.getChildren()){
            if(depNode.getValue() == siec){
                treeView.getSelectionModel().select(depNode);
                return;
            }
            selectItem(depNode, siec);
        }
    }

    public void remove(TreeItem<Siec6> start){
        for(TreeItem<Siec6> depNode : start.getChildren()){
            data.remove(depNode.getValue());
            remove(depNode);
        }
        data.remove(start.getValue());
    }

    public static void getFreeSiecs(TreeItem<Siec6> treeItem, List list){
        for (int i = 0; i < treeItem.getChildren().size(); i++) {
            getFreeSiecs(treeItem.getChildren().get(i), list);
        }

        if(treeItem.getChildren().size() == 0 && (treeItem.getValue().getStatus() == null || treeItem.getValue().getStatus().isEmpty())) {
            list.add(new Siec6(treeItem.getValue().getAddress(), treeItem.getValue().getMask(), treeItem.getValue().getCountIp(),
                    "", "", "", "", ""));
        }else {
            List<Siec6> siec6s = new ArrayList<>();
            for(int i = 0; i < treeItem.getChildren().size(); i++){
                siec6s.add(treeItem.getChildren().get(i).getValue());
            }

            Collections.sort(siec6s, (o1, o2) -> Siec6.isBigger(o1,o2));

            for (int i = 0; i < siec6s.size(); i++) {
                Siec6 siec = siec6s.get(i);
                Siec6 siec2;

                if (i == 0) {
                    if (!treeItem.getValue().getAddress().equals(siec.getAddress())) {
                        list.add(new Siec6(treeItem.getValue().getAddress(), "",
                                String.valueOf(Siec6.minus(siec.getAddress(), treeItem.getValue().getAddress()))));
                    }
                }
                if (i == siec6s.size() - 1) {
                    siec2 = new Siec6(Siec6.generatedIpSiec(treeItem.getValue().getAddress(),
                            Integer.parseInt(treeItem.getValue().getCountIp())), "", "", "", "", "", "", "");
                    if (Siec6.generatedIpSiec(siec.getAddress(), Integer.parseInt(siec.getCountIp())).equals(
                            Siec6.generatedIpSiec(treeItem.getValue().getAddress(), Integer.parseInt(treeItem.getValue().getCountIp()))))
                        break;
                } else
                    siec2 = siec6s.get(i+1);

                if (Siec6.generatedIpSiec(siec.getAddress(), Integer.parseInt(siec.getCountIp())).equals(siec2.getAddress())) {
                    continue;
                }
                String addr1 = Siec6.generatedIpSiec(siec.getAddress(), Integer.parseInt(siec.getCountIp()));
                list.add(new Siec6(addr1, "",
                        String.valueOf(Siec6.minus(siec2.getAddress(), addr1)), "", "", "", "", ""));
            }
        }
    }

    public TreeItem<Siec6> getRootNode() {
        return rootNode;
    }

    class CustomCell extends TreeCell<Siec6> {
        @Override
        protected void updateItem(Siec6 item, boolean empty) {
            super.updateItem(item, empty);

            // If the cell is empty we don't show anything.
            if (isEmpty()) {
                setGraphic(null);
                setText(null);
            } else {

                HBox cellBox = new HBox(10);
                Label labelAddMasCount = new Label(item.getAddress() + " [" + item.getMask() + "]   {"+item.getCountIp()+"}");
                Label labelOpenInfo = new Label(null, new ImageView(new Image("Icons/more.png")));
                Label labelInfo = new Label();
                Label icon;
                String url;
                if(item.getStatus() == null || item.getStatus().isEmpty()){
                    url = "Icons/network.png";
                    cellBox.setStyle("-fx-border-color: darkgrey;");
                }else
                if(item.getStatus().equals("n")) {
//                    labelAddMasCount.setStyle("-fx-background-color: #CEFFCE;");
                    url = "Icons/open_network.png";
                }else {
//                    labelAddMasCount.setStyle("-fx-background-color: #FFE2E2;");
                    url = "Icons/close_network.png";
                }
                icon = new Label(null, new ImageView(new Image(url)));
                labelOpenInfo.setOnMouseClicked(event ->{
                    if(labelInfo.getText().isEmpty()){
                        labelInfo.setText("{client='"+item.getClient()+
                                "', type='"+item.getType()+"', priority='" + item.getPriority()+"', date='"+ item.getDate() +"'}");
                    }else
                        labelInfo.setText("");
                });
                List<Siec6> list = new ArrayList<>();
                TreeViewManager.getFreeSiecs(getTreeItem(), list);
                int n = 0;
                for(Siec6 siec : list){
                    n += Integer.parseInt(siec.getCountIp());
                }
                Label countFreeIp = new Label(String.valueOf(n));
                if(n > 0)
                    countFreeIp.setStyle("-fx-background-color: chartreuse;");
                else
                    countFreeIp.setStyle("-fx-background-color: lightcoral;");
                 cellBox.getChildren().addAll(icon, labelAddMasCount, countFreeIp, labelOpenInfo, labelInfo);
                // We set the cellBox as the graphic of the cell.
                setGraphic(cellBox);
                setText(null);

            }
        }
    }
}
