package de.unibremen.bhuman.ptfc;

import de.unibremen.bhuman.ptfc.control.ProgressDialogController;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import lombok.SneakyThrows;

/**
 * A convenient progress dialog that can be bound to a <code>Worker<?></code>.
 *
 * @author Yannick Bülter
 * @version 1.0
 */
public class ProgressDialog extends Stage {

    private Label message;

    private ProgressBar progressBar;

    /**
     * Constructs a new progress dialog.
     *
     * @param worker The task, that should be observed from the dialog.
     */
    @SneakyThrows
    public ProgressDialog(final Worker<?> worker) {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/loading.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        setScene(scene);
        ProgressDialogController c = fxmlLoader.getController();
        c.getText().textProperty().bind(worker.messageProperty());
        c.getBar().progressProperty().bind(worker.progressProperty());
        message = c.getText();
        progressBar = c.getBar();
        worker.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.RUNNING) {
                show();
            }
            if (newValue == Worker.State.SUCCEEDED || newValue == Worker.State.FAILED
                    || newValue == Worker.State.CANCELLED) {
                close();
            }
        });
        setOnCloseRequest(event -> {
            if (worker.getState() != Worker.State.SUCCEEDED) {
                event.consume();
            }
        });
    }

    public void setIndeterminate(final boolean indeterminate) {
        if (indeterminate) {
            progressBar.setProgress(-1);
        } else {
            progressBar.setProgress(0);
        }
    }
}
