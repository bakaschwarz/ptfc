package de.unibremen.bhuman.ptfc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import lombok.Getter;
import lombok.Setter;

/**
 * Kontrolliert die Informationsfenster.
 *
 * @author Yannick Bülter
 */
public class InfoWindowController {

    @FXML private Button ok_button;


    @FXML
    @Getter
    private VBox vboxtext;

    @FXML
    @Getter
    private Button cancel_button;
    @Getter
    private boolean fun = false;
    @Setter
    private Window parent = null;



    /**
     * Schließt das Informationsfenster.
     */
    @FXML
    private void clickedClose() {
        ok_button.setDisable(true);
        if (parent != null) {
            parent.hide();
            fun = true;
        }
    }

    /**
     * Schließt das Informationsfenster und vermerkt, dass etwas abgebrochen werden soll.
     */
    @FXML
    private void cancelPressed(ActionEvent actionEvent) {
        if (parent != null) {
            parent.hide();
            fun = false;
        }
    }
}
