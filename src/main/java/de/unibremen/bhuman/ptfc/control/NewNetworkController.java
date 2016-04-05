package de.unibremen.bhuman.ptfc.control;

import de.unibremen.bhuman.ptfc.InfoWindow;
import de.unibremen.bhuman.ptfc.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.File;

public class NewNetworkController {

    @FXML
    private TextField locationField;

    @FXML
    private TextField networkName;

    @Getter
    private static File saveDirectory;

    @Getter
    private static String filename;

    @Getter
    private static boolean canceled = false;

    @FXML
    void cancel(ActionEvent event) {
        saveDirectory = null;
        filename = null;
        canceled = true;
        ((Stage)locationField.getScene().getWindow()).close();
    }

    @FXML
    void confirm(ActionEvent event) {
        if(saveDirectory != null && !networkName.getText().equals("")) {
            filename = networkName.getText();
            ((Stage)locationField.getScene().getWindow()).close();
        } else {
            InfoWindow.showInfo("Warning!", "You did not provide all needed information!", locationField.getScene().getWindow());
        }
    }

    @FXML
    void open(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File tmp = directoryChooser.showDialog(Main.getMainWindow());
        if(tmp != null && tmp.exists()) {
            saveDirectory = tmp;
            locationField.setText(saveDirectory.getAbsolutePath());
        }
    }

}
