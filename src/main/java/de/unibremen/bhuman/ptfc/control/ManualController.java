package de.unibremen.bhuman.ptfc.control;

import de.unibremen.bhuman.ptfc.Main;
import de.unibremen.bhuman.ptfc.data.SimpleImageCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ManualController {

    @FXML
    private ListView<Image> importView;

    @FXML
    private StackPane stack;

    @FXML
    private ListView<Image> exportView;

    @FXML
    private VBox importantBox;

    private ImageView stackImageView;

    private Canvas indicatorLayer;

    private Delta mousePosition;

    @FXML
    private void initialize() {
        mousePosition = new Delta(0.0, 0.0);
        stackImageView = new ImageView(new WritableImage(32, 32));
        stack.prefHeightProperty().bind(importantBox.heightProperty());
        stack.prefWidthProperty().bind(importantBox.widthProperty());

        stackImageView.setSmooth(false);
        stackImageView.fitHeightProperty().bind(stack.heightProperty());
        stackImageView.fitWidthProperty().bind(stack.widthProperty());
        stack.getChildren().add(stackImageView);

        indicatorLayer = new Canvas(0, 0);
        indicatorLayer.widthProperty().bind(stackImageView.getImage().widthProperty());
        indicatorLayer.heightProperty().bind(stackImageView.getImage().heightProperty());
        stack.getChildren().add(indicatorLayer);

        importView.setCellFactory(param -> new SimpleImageCell());
        importView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                stackImageView.setImage(newValue);
            } else {
                stackImageView.setImage(new WritableImage(32, 32));
            }
            indicatorLayer.widthProperty().bind(stackImageView.getImage().widthProperty());
            indicatorLayer.heightProperty().bind(stackImageView.getImage().heightProperty());
        });
        exportView.setCellFactory(param -> new SimpleImageCell());
        prepareMouseMovement();
    }

    void prepareMouseMovement() {
        stack.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            System.out.println("LLOLL");
            mousePosition.setX(event.getX());
            mousePosition.setY(event.getY());
            drawIndicator();
        });

        stack.setOnMouseDragExited(event -> {
            indicatorLayer.getGraphicsContext2D().clearRect(0, 0, indicatorLayer.getWidth(), indicatorLayer.getHeight());
        });
    }

    void drawIndicator() {
        GraphicsContext gc = indicatorLayer.getGraphicsContext2D();
        gc.clearRect(0, 0, indicatorLayer.getWidth(), indicatorLayer.getHeight());
        gc.setStroke(Color.AQUA);
        gc.setLineWidth(2);
        gc.strokeRect(mousePosition.getX() - 16, mousePosition.getY() - 16, 32, 32);
    }

    @FXML
    void loadImport(ActionEvent event) throws FileNotFoundException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File importDir = directoryChooser.showDialog(Main.getMainWindow());
        if(importDir != null && importDir.exists()) {
            importView.getItems().clear();
            for(File file : importDir.listFiles()) {
                if(FilenameUtils.getExtension(file.getName()).equals("png")) {
                    Image image = new Image(new FileInputStream(file));
                    importView.getItems().add(image);
                }
            }
        }
    }

}

@Data
@AllArgsConstructor
class Delta {
    private double x;
    private double y;
}