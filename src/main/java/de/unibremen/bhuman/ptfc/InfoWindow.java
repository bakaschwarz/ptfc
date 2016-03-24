package de.unibremen.bhuman.ptfc;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.controlsfx.control.PopOver;

import java.io.IOException;

/**
 * Klasse zum schnellen Erstellen von Informationsfenstern.
 *
 * @author Yannick Bülter
 */
public class InfoWindow {

    private InfoWindow() {}

    /**
     * Zeigt einen einfachen Dialog mit einem Titel, einer Nachricht und einem OK Button an.
     *
     * @param title   Der Titel für den Dialog.
     * @param message Die Nachricht.
     * @param owner   Das Vater Fenster für den Dialog.
     */
    public static void showInfo(final String title, final String message, final Window owner) {
        try {
            FXMLLoader fxml_loader = new FXMLLoader(Main.class.getResource("fxml/InfoWindow.fxml"));
            Parent node = fxml_loader.load();
            InfoWindowController controller = fxml_loader.getController();
            PopOver pop = new PopOver(node);

            Text text = new Text(message);
            text.setFill(Color.WHITE);
            TextFlow textFlow = new TextFlow(text);
            controller.getVboxtext().getChildren().add(textFlow);

            pop.setAutoHide(true);
            pop.setDetached(true);
            pop.setDetachedTitle(title);
            controller.setParent(pop);
            pop.show(owner);
            ((Parent) pop.getSkin().getNode()).getStylesheets()
                    .add(Main.class.getResource("style/InfoWindow.css").toExternalForm());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zeigt einen einfachen Dialog mit einem Titel, einer Nachricht, einem OK Button und einem Abbrechen Button an.
     *
     * @param title   Der Titel für den Dialog.
     * @param message Die Nachricht.
     * @param owner   Das Vater Fenster für den Dialog.
     * @return <code>true</code>, falls bestätigt wurde.
     */
    public static boolean showConfirm(final String title, final String message, Window owner) {
        try {
            FXMLLoader fxml_loader = new FXMLLoader(Main.class.getResource("fxml/InfoWindow.fxml"));
            Parent node = fxml_loader.load();
            InfoWindowController controller = fxml_loader.getController();

            Text text = new Text(message);
            text.setFill(Color.WHITE);
            TextFlow textFlow = new TextFlow(text);
            controller.getVboxtext().getChildren().add(textFlow);

            controller.getCancel_button().setVisible(true);
            controller.getCancel_button().setDisable(false);
            Scene scene = new Scene(node);
            scene.getStylesheets().add(Main.class.getResource("style/main.css").toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(true);
            controller.setParent(stage);
            stage.setTitle(title);
            stage.initOwner(owner);
            stage.showAndWait();

            return controller.isFun();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
