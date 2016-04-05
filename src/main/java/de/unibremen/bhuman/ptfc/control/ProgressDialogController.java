package de.unibremen.bhuman.ptfc.control;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is only existing to provide access to the bar and the text.
 *
 * @author Yannick Bülter
 * @version 1.0
 */
@Getter
@Setter
public class ProgressDialogController {

    @FXML
    private ProgressBar bar;

    @FXML
    private Label text;

    @FXML
    void initialize() {
    }
}

