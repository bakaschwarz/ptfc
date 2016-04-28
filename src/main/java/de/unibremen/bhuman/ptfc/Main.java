package de.unibremen.bhuman.ptfc;

import de.unibremen.bhuman.ptfc.control.ClassifyController;
import de.yabue.bakacore.Command.CommandHistory;
import de.yabue.bakacore.Configurations.ObservableConfiguration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Main class starting the JavaFX Thread
 */
public class Main extends Application{

    @Getter
    private static Window mainWindow;

    @Getter
    private static ObservableConfiguration observableConfiguration;

    @Getter
    private static CommandHistory commandHistory;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        commandHistory = new CommandHistory(1024);
        prepareConfiguration();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/main.fxml"));
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        mainWindow = primaryStage;
        primaryStage.setTitle("PTFC");
        primaryStage.setOnCloseRequest(event -> {
            if(ClassifyController.isChanges()) {
                if(!InfoWindow.showConfirm("Discard changes?", "There are unsaved changes. Closing means losing all of them. Proceed?", mainWindow)) {
                    event.consume();
                }
            }
        });

        primaryStage.show();
    }

    public static void resetCommandHistory() {
        commandHistory = new CommandHistory(1024);
    }

    private static void prepareConfiguration() throws IOException, ConfigurationException {
        File configFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath() + ".properties");
        if(!configFile.exists()) {
            FileUtils.touch(configFile);
            observableConfiguration = new ObservableConfiguration(configFile, true);
            observableConfiguration.setStringProperty("networkPath", "");
            observableConfiguration.setStringProperty("trainingPath", "");
            observableConfiguration.setStringProperty("testPath", "");
            observableConfiguration.setStringProperty("fcnnPath", "");
            observableConfiguration.setStringProperty("mse", "0.01");
            observableConfiguration.setStringProperty("epoches", "5000");
            observableConfiguration.setStringProperty("frequency", "10");
            observableConfiguration.setStringProperty("learnRate", "0.7");
            observableConfiguration.setStringProperty("layers", "1024 24 1");
        } else {
            observableConfiguration = new ObservableConfiguration(configFile, true);
        }
    }
}
