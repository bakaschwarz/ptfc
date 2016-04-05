package de.unibremen.bhuman.ptfc.control;

import de.unibremen.bhuman.ptfc.Main;
import de.unibremen.bhuman.ptfc.commands.DeleteCommand;
import de.unibremen.bhuman.ptfc.commands.IsBallCommand;
import de.unibremen.bhuman.ptfc.commands.NoBallCommand;
import de.unibremen.bhuman.ptfc.data.ClassifiedImage;
import de.unibremen.bhuman.ptfc.data.PNGSource;
import de.unibremen.bhuman.ptfc.data.Status;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassifyController {

    //region FXML Variables
    @FXML
    private ImageView lastView;

    @FXML
    private ImageView currentView;

    @FXML
    private ImageView nextView;

    @FXML
    private Label lastLabel;

    @FXML
    private Label currentLabel;

    @FXML
    private Label nextLabel;

    @FXML
    private Button noBallButton;

    @FXML
    private Button ballButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button revertButton;

    @FXML
    private TextField focusField;

    @FXML
    private TextField noBindField;

    @FXML
    private TextField yesBindField;

    @FXML
    private TextField revertBindField;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField inputField;

    @FXML
    private TextField outputField;

    @FXML
    private Button saveButton;
    //endregion

    private File inputPath, outputPath;

    @Getter
    private static List<ClassifiedImage> imageList;

    @Getter @Setter
    private static int index = 0;

    @Getter @Setter
    private static int positive = 0;

    @Getter @Setter
    private static int negative = 0;

    @Getter @Setter
    private static int delete = 0;

    @Getter
    private static boolean changes = false;


    private int outputcounter = 0;

    @Getter
    private static ClassifyController controller;

    @FXML
    void initialize() {
        revertButton.disableProperty().bind(Main.getCommandHistory().undoProperty().not());
        saveButton.disableProperty().bind(Bindings.and(outputField.textProperty().isEqualTo("").not(), inputField.textProperty().isEqualTo("").not()).not());
        imageList = new ArrayList<>();
        controller = this;
    }

    @FXML
    void aBall(ActionEvent event) {
        if(imageList.size() > 0) {
            IsBallCommand isBallCommand = new IsBallCommand();
            Main.getCommandHistory().execute(isBallCommand);
            updateImages();
            updateStatus();
            changes = true;
        }
    }

    @FXML
    void delete(ActionEvent event) {
        if(imageList.size() > 0) {
            DeleteCommand deleteCommand = new DeleteCommand();
            Main.getCommandHistory().execute(deleteCommand);
            updateImages();
            updateStatus();
            changes = true;
        }
    }

    @FXML
    void keybindInvoke(KeyEvent event) {
        if(index < imageList.size()) {
            String input = event.getCharacter();
            if (input.toCharArray()[0] == noBindField.getText().toCharArray()[0]) {
                noBall(new ActionEvent());
            } else if (input.toCharArray()[0] == yesBindField.getText().toCharArray()[0]) {
                aBall(new ActionEvent());
            } else if (input.toCharArray()[0] == revertBindField.getText().toCharArray()[0]) {
                revert(new ActionEvent());
            }
        }
        event.consume();
    }

    @FXML
    void loadInput(ActionEvent event) throws FileNotFoundException {
        DirectoryChooser dc = new DirectoryChooser();
        inputPath = dc.showDialog(Main.getMainWindow());
        if(inputPath != null) {
            inputField.setText(inputPath.getAbsolutePath());
            imageList.clear();
            for (File file : inputPath.listFiles()) {
                if (FilenameUtils.getExtension(file.getName()).equals("png")) {
                    ClassifiedImage image = new ClassifiedImage(file);
                    imageList.add(image);
                }
            }
            updateImages();
            updateStatus();
            Main.resetCommandHistory();
        }
    }

    @FXML
    void loadOutput(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        outputPath = dc.showDialog(Main.getMainWindow());
        if(outputPath != null) {
            outputField.setText(outputPath.getAbsolutePath());
        }
    }

    @FXML
    void noBall(ActionEvent event) {
        if(imageList.size() > 0) {
            NoBallCommand noBallCommand = new NoBallCommand();
            Main.getCommandHistory().execute(noBallCommand);
            updateImages();
            updateStatus();
            changes = true;
        }
    }

    ClassifiedImage getImageAtIndex(final int index) {
        if(index >= 0 && index < imageList.size()) {
            return imageList.get(index);
        } else {
            return new ClassifiedImage();
        }
    }

    @FXML
    void revert(ActionEvent event) {
        if(Main.getCommandHistory().undoProperty().getValue()) {
            Main.getCommandHistory().undo();
            updateImages();
            updateStatus();
        }
    }

    @FXML
    void save(ActionEvent event) throws IOException {
        for(ClassifiedImage image : imageList) {
            if(image.getStatus() != Status.DELETE && image.getStatus() != Status.NOTHING) {
                String out_name = getAFilename(image.getStatus());
                File out_file = new File(outputPath.getAbsolutePath() + File.separator + out_name);
                FileUtils.copyFile(image.getPath(), out_file);
                image.setStatus(Status.NOTHING);
            }
            outputcounter++;
        }
        index = 0;
        positive = 0;
        negative = 0;
        delete = 0;
        updateStatus();
        updateImages();
        outputcounter = 0;
        changes = false;
        PNGSource source = new PNGSource(outputPath);
        TrainingGenerateController.getController().getSourcesListView().getItems().add(source);
        Main.resetCommandHistory();
    }

    void updateImages() {
        ClassifiedImage last = getImageAtIndex(index - 1);
        ClassifiedImage current = getImageAtIndex(index);
        ClassifiedImage next = getImageAtIndex(index + 1);

        lastView.setImage(last.getImage());
        lastLabel.setText(last.getPath() != null ? String.format("Status: %s", last.getStatus()) : "---");

        currentView.setImage(current.getImage());
        currentLabel.setText(current.getPath() != null ? String.format("Status: %s", current.getStatus()) : "---");

        nextView.setImage(next.getImage());
        nextLabel.setText(next.getPath() != null ? String.format("Status: %s", next.getStatus()) : "---");
    }

    void updateStatus() {
        statusLabel.setText(String.format("Status: %d/%d classified - positive: %d, negative: %d, %d are marked for deletion.",
                index,
                imageList.size(),
                positive,
                negative,
                delete
        ));
    }

    String getAFilename(Status ball) {
        DateTime dt = new DateTime();
        String filename = String.format(
                "%s%s_%s_%s_%s_%04d.png",
                ball == Status.BALL ? "" : "f_",
                dt.getYear(),
                dt.getMonthOfYear(),
                dt.getDayOfMonth(),
                dt.getMillisOfDay(),
                outputcounter
        );
        return filename;
    }

}