package de.unibremen.bhuman.ptfc.control;

import de.unibremen.bhuman.ptfc.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

import java.io.IOException;

public class MainController {

    @FXML
    private Tab classificationTab;

    @FXML
    private Tab trainingGenerationTab;

    @FXML
    private Tab fcnnTab;

    @FXML
    void initialize() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/classify.fxml"));
        classificationTab.setContent(fxmlLoader.load());

        FXMLLoader fxmlLoader1 = new FXMLLoader(Main.class.getResource("fxml/training.fxml"));
        trainingGenerationTab.setContent(fxmlLoader1.load());

        FXMLLoader fxmlLoader2 = new FXMLLoader(Main.class.getResource("fxml/nettrain.fxml"));
        fcnnTab.setContent(fxmlLoader2.load());
    }
}