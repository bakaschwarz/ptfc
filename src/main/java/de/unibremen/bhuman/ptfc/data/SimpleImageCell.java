package de.unibremen.bhuman.ptfc.data;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SimpleImageCell extends ListCell<Image> {

    ImageView view;

    public SimpleImageCell() {
        super();
        view = new ImageView();
        view.setPreserveRatio(true);
        view.setFitHeight(200.0);
        view.setFitWidth(200.0);
    }

    @Override
    protected void updateItem(Image image, boolean empty) {
        super.updateItem(image, empty);
        if(!empty) {
            view.setImage(image);
            setGraphic(view);
        } else {
            setGraphic(null);
        }
    }
}
