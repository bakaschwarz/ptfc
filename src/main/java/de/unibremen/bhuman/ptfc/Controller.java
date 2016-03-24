package de.unibremen.bhuman.ptfc;

import de.unibremen.bhuman.ptfc.commands.DeleteCommand;
import de.unibremen.bhuman.ptfc.commands.IsBallCommand;
import de.unibremen.bhuman.ptfc.commands.NoBallCommand;
import de.unibremen.bhuman.ptfc.data.ClassifiedImage;
import de.unibremen.bhuman.ptfc.data.PNGSource;
import de.unibremen.bhuman.ptfc.data.PNGSourceCell;
import de.unibremen.bhuman.ptfc.data.Status;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
    private Button saveButton;

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
    private ListView<PNGSource> pngSourcesListView;

    @FXML
    private TextField trainNameField;

    @FXML
    private TextField trainOutPathField;

    @FXML
    private Button generateDataButton;
    //endregion

    private File inputPath, outputPath, pngOutputPath;

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



    @FXML
    void initialize() {
        revertButton.disableProperty().bind(Main.getCommandHistory().undoProperty().not());
        saveButton.disableProperty().bind(Bindings.and(outputField.textProperty().isEqualTo("").not(), inputField.textProperty().isEqualTo("").not()).not());
        imageList = new ArrayList<>();
        pngSourcesListView.setCellFactory(param -> new PNGSourceCell());
        generateDataButton.disableProperty().bind(Bindings.and(trainNameField.textProperty().isNotEmpty(), trainOutPathField.textProperty().isNotEmpty()).not());
    }

    //region Classification
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
        String input = event.getCharacter();
        if(input.toCharArray()[0] == noBindField.getText().toCharArray()[0]) {
            noBall(new ActionEvent());
        } else if(input.toCharArray()[0] == yesBindField.getText().toCharArray()[0]) {
            aBall(new ActionEvent());
        } else if(input.toCharArray()[0] == revertBindField.getText().toCharArray()[0]) {
            revert(new ActionEvent());
        }
        event.consume();
    }

    @FXML
    void loadInput(ActionEvent event) throws FileNotFoundException {
        DirectoryChooser dc = new DirectoryChooser();
        inputPath = dc.showDialog(Main.getMainWindow());
        inputField.setText(inputPath.getAbsolutePath());
        imageList.clear();
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
                File out_file = new File(outputPath.getAbsolutePath() + "/" + out_name);
                FileUtils.copyFile(image.getPath(), out_file);
                image.setStatus(Status.NOTHING);
            }
        }
        index = 0;
        positive = 0;
        negative = 0;
        delete = 0;
        updateStatus();
        updateImages();
        changes = false;
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
                "%s%s_%s_%s_%s.png",
                ball == Status.BALL ? "" : "f_",
                dt.getYear(),
                dt.getMonthOfYear(),
                dt.getDayOfMonth(),
                dt.getMillisOfDay());
        return filename;
    }
    //endregion

    //region Generate FANN
    @FXML
    void clearPNGSourcesButton(ActionEvent event) {
        pngSourcesListView.getItems().clear();
    }

    @FXML
    void generateData(ActionEvent event) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(pngOutputPath.getAbsolutePath() + "/" + trainNameField.getText());
        String lines = "";
        int testCount = 0;
        for(PNGSource source : pngSourcesListView.getItems()) {
            testCount += source.getImages().size();
            for(ClassifiedImage image : source.getImages()) {
                BufferedImage bufferedImage = ImageIO.read(image.getPath());
                byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
                ArrayList<String> extracted_r = new ArrayList<>();
                for (int i = 0; i < pixels.length; i += 2) {
                    extracted_r.add(Integer.toString(pixels[i] & 0xFF));
                }
                lines += (String.join(" ", extracted_r)) + "\n";
                lines += (image.getStatus() == Status.BALL ? "1" : "0") + "\n";
            }
        }
        IOUtils.write(String.format("%d %d %d\n", testCount, 1024, 1), outputStream);
        IOUtils.write(lines, outputStream);
        outputStream.close();
    }

    @FXML
    void loadPNGSourceButton(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(Main.getMainWindow());
        if (dir != null) {
            PNGSource pngSource = new PNGSource(dir);
            pngSourcesListView.getItems().add(pngSource);
        }
    }

    @FXML
    void setTrainOutPutPath(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(Main.getMainWindow());
        if(dir != null) {
            pngOutputPath = dir;
            trainOutPathField.setText(dir.getAbsolutePath());
        }
    }
    //endregion
}
