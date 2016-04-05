package de.unibremen.bhuman.ptfc.control;

import com.sun.org.apache.xerces.internal.dom.ParentNode;
import de.unibremen.bhuman.ptfc.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.PopOver;

import java.io.File;
import java.io.IOException;

public class NetCreateTrainController {

    @FXML
    private TextField networkField;

    @FXML
    private TextField trainField;

    @FXML
    private TextField mseField;

    @FXML
    private TextField epochesField;

    @FXML
    private TextField frequencyField;

    @FXML
    private TextField layerField;

    @FXML
    private TextArea consoleArea;

    @FXML
    private Button trainButton;

    @FXML
    private Button testButton;

    private File networkPath;

    private boolean newNet = false;

    @FXML
    void loadDataset(ActionEvent event) {

    }

    @FXML
    void loadFCNNExe(ActionEvent event) {

    }

    @FXML
    void loadNet(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File tmp = fileChooser.showOpenDialog(Main.getMainWindow());
        if(tmp != null && tmp.exists()) {
            newNet = false;
            networkPath = tmp;
            networkField.setText(networkPath.getAbsolutePath());
        }
    }

    @FXML
    void newNet(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/newnetdialog.fxml"));
        Parent node = fxmlLoader.load();
        Scene scene = new Scene(node);
        scene.getStylesheets().add(Main.class.getResource("style/main.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(Main.getMainWindow());
        stage.showAndWait();
        if(!NewNetworkController.isCanceled() && !NewNetworkController.getFilename().equals("") && NewNetworkController.getSaveDirectory() != null) {
            networkPath = new File(
                    NewNetworkController.getSaveDirectory().getAbsolutePath() + File.separator +
                            NewNetworkController.getFilename());
            networkField.setText(networkPath.getAbsolutePath());
            newNet = true;
        }
    }

    @FXML
    void openGithub(ActionEvent event) {

    }

}
