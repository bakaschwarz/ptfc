package de.unibremen.bhuman.ptfc;

import de.unibremen.bhuman.ptfc.commands.DeleteCommand;
import de.unibremen.bhuman.ptfc.commands.IsBallCommand;
import de.unibremen.bhuman.ptfc.commands.NoBallCommand;
import de.unibremen.bhuman.ptfc.data.ClassifiedImage;
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
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class Controller {

    //region FX variables
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
    private Label statusLabel;

    @FXML
    private TextField inputField;

    @FXML
    private TextField outputField;
    //endregion

    private File inputPath, outputPath;

    @Getter
    private static List<ClassifiedImage> imageList;

    @Getter @Setter
    private static int index = 0;

    @Getter
    private static boolean changes = false;

    @FXML
    void initialize() {
        revertButton.disableProperty().bind(Main.getCommandHistory().undoProperty().not());
        imageList = new ArrayList<>();
    }

    @FXML
    void aBall(ActionEvent event) {
        if(imageList.size() > 0) {
            IsBallCommand isBallCommand = new IsBallCommand();
            Main.getCommandHistory().execute(isBallCommand);
            updateImages();
            changes = true;
        }
    }

    @FXML
    void delete(ActionEvent event) {
        if(imageList.size() > 0) {
            DeleteCommand deleteCommand = new DeleteCommand();
            Main.getCommandHistory().execute(deleteCommand);
            updateImages();
            changes = true;
        }
    }

    @FXML
    void keybindInvoke(KeyEvent event) {

    }

    @FXML
    void loadInput(ActionEvent event) throws FileNotFoundException {
        DirectoryChooser dc = new DirectoryChooser();
        inputPath = dc.showDialog(Main.getMainWindow());
        inputField.setText(inputPath.getAbsolutePath());

        for(File file : inputPath.listFiles()) {
            if(FilenameUtils.getExtension(file.getName()).equals("png")) {
                ClassifiedImage image = new ClassifiedImage(file);
                imageList.add(image);
            }
        }
        lastView.setImage(null);
        lastLabel.setText("---");
        ClassifiedImage current = getImageAtIndex(0);
        ClassifiedImage next = getImageAtIndex(1);
        currentView.setImage(current.getImage());
        nextView.setImage(next.getImage());

        currentLabel.setText(current.getPath() != null ? current.getPath().getName() : "---");
        nextLabel.setText(next.getPath() != null ? next.getPath().getName() : "---");
    }

    @FXML
    void loadOutput(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        outputPath = dc.showDialog(Main.getMainWindow());
        outputField.setText(outputPath.getAbsolutePath());
    }

    @FXML
    void noBall(ActionEvent event) {
        if(imageList.size() > 0) {
            NoBallCommand noBallCommand = new NoBallCommand();
            Main.getCommandHistory().execute(noBallCommand);
            updateImages();
            changes = true;
        }
    }

    ClassifiedImage getImageAtIndex(final int index) {
        if(index >= 0 || index < imageList.size()) {
            return imageList.get(index);
        } else {
            return new ClassifiedImage();
        }
    }

    @FXML
    void revert(ActionEvent event) {
        Main.getCommandHistory().undo();
        updateImages();
    }

    @FXML
    void save(ActionEvent event) {

        changes = false;
    }

    void updateImages() {
        ClassifiedImage last = getImageAtIndex(index - 1);
        ClassifiedImage current = getImageAtIndex(index);
        ClassifiedImage next = getImageAtIndex(index + 1);

        lastView.setImage(last.getImage());
        lastLabel.setText(last.getPath() != null ? last.getPath().getName() : "---");

        currentView.setImage(current.getImage());
        currentLabel.setText(current.getPath() != null ? current.getPath().getName() : "---");

        nextView.setImage(next.getImage());
        nextLabel.setText(next.getPath() != null ? next.getPath().getName() : "---");
    }

}
