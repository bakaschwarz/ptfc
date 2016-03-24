package de.unibremen.bhuman.ptfc.data;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class PNGSourceCell extends ListCell<PNGSource> {

    @Override
    protected void updateItem(PNGSource item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty) {
            HBox box = new HBox();
            box.setAlignment(Pos.CENTER_LEFT);
            Label path = new Label(String.format("Path: %s - %d positive, %d negative",
                    item.getSource().getAbsolutePath(),
                    item.getPositive(),
                    item.getNegative()
                    ));
            Button button = new Button("Remove");
            button.setOnAction(event -> getListView().getItems().remove(item));
            box.getChildren().addAll(path, button);
            setGraphic(box);
        } else {
            setGraphic(null);
        }
    }
}
