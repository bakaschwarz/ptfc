package de.unibremen.bhuman.ptfc.control;

import de.unibremen.bhuman.ptfc.InfoWindow;
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
    private Tab manualTab;

    @FXML
    void initialize() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/classify.fxml"));
        classificationTab.setContent(fxmlLoader.load());

        FXMLLoader fxmlLoader1 = new FXMLLoader(Main.class.getResource("fxml/training.fxml"));
        trainingGenerationTab.setContent(fxmlLoader1.load());

        FXMLLoader fxmlLoader2 = new FXMLLoader(Main.class.getResource("fxml/nettrain.fxml"));
        fcnnTab.setContent(fxmlLoader2.load());

        FXMLLoader fxmlLoader3 = new FXMLLoader(Main.class.getResource("fxml/manualextraction.fxml"));
        manualTab.setContent(fxmlLoader3.load());
    }


    @FXML
    void saveConfig() {
        NetCreateTrainController nctc = NetCreateTrainController.getNetCreateTrainController();
        Main.getObservableConfiguration().setStringProperty("networkPath", nctc.networkField.getText());
        Main.getObservableConfiguration().setStringProperty("trainingPath", nctc.trainField.getText());
        Main.getObservableConfiguration().setStringProperty("testPath", nctc.testField.getText());
        Main.getObservableConfiguration().setStringProperty("fcnnPath", nctc.fcnnField.getText());
        Main.getObservableConfiguration().setStringProperty("mse", nctc.mseField.getText());
        Main.getObservableConfiguration().setStringProperty("epoches", nctc.epochesField.getText());
        Main.getObservableConfiguration().setStringProperty("frequency", nctc.frequencyField.getText());
        Main.getObservableConfiguration().setStringProperty("learnRate", nctc.learnField.getText());
        Main.getObservableConfiguration().setStringProperty("layers", nctc.layerField.getText());
        InfoWindow.showInfo("Saved!", "Saved the configuration for future sessions!", Main.getMainWindow());
    }
}