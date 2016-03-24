package de.unibremen.bhuman.ptfc;

import de.yabue.bakacore.Command.CommandHistory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;

/**
 * Main class starting the JavaFX Thread
 */
public class Main extends Application{

    @Getter
    private static Window mainWindow;

    @Getter
    private static CommandHistory commandHistory;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        commandHistory = new CommandHistory(1024);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/main.fxml"));
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        mainWindow = primaryStage;
        primaryStage.show();
    }


}
