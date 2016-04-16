package de.unibremen.bhuman.ptfc.control;

import de.unibremen.bhuman.ptfc.InfoWindow;
import de.unibremen.bhuman.ptfc.Main;
import de.unibremen.bhuman.ptfc.ProgressDialog;
import de.unibremen.bhuman.ptfc.data.ClassifiedImage;
import de.unibremen.bhuman.ptfc.data.PNGSource;
import de.unibremen.bhuman.ptfc.data.PNGSourceCell;
import de.unibremen.bhuman.ptfc.data.Status;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import lombok.Getter;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class TrainingGenerateController {

    @FXML @Getter
    private ListView<PNGSource> sourcesListView;

    @FXML
    private TextField nameField;

    @FXML
    private TextField outputPathField;

    @FXML
    private Button generateDataButton;

    @FXML
    private CheckBox mirrorCheck;

    private File outputPath;

    private static String lines, mirrorLines;

    private static int testCount;

    @Getter
    private static TrainingGenerateController controller;

    @FXML
    void initialize() {
        sourcesListView.setCellFactory(param -> new PNGSourceCell());
        generateDataButton.disableProperty().bind(Bindings.and(nameField.textProperty().isNotEmpty(), outputPathField.textProperty().isNotEmpty()).not());
        controller = this;
    }

    @FXML
    void clearPNGSourcesButton(ActionEvent event) {
        sourcesListView.getItems().clear();
    }

    @FXML
    void generateData(ActionEvent event) throws IOException {
        lines = "";
        mirrorLines = "";
        testCount = 0;
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        int count = 1;
                        for(PNGSource source : sourcesListView.getItems()) {
                            testCount += source.getImages().size();
                            for(ClassifiedImage image : source.getImages()) {
                                updateMessage(String.format(
                                        "Converting image %s...",
                                        count
                                        ));
                                count++;
                                BufferedImage bufferedImage = ImageIO.read(image.getPath());
                                ArrayList<String> extracted_r = new ArrayList<>();
                                for(int y = 0; y < 32; y++) {
                                    for(int x = 0; x < 32; x++) {
                                        Color color = new Color(bufferedImage.getRGB(x, y));
                                        extracted_r.add(Integer.toString(color.getRed()));
                                    }
                                }
                                lines += (String.join(" ", extracted_r)) + "\n";
                                lines += (image.getStatus() == Status.POSITIVE ? "1" : "0") + "\n";
                                if(mirrorCheck.isSelected()) {
                                    ArrayList<String> reversedList = (ArrayList<String>) extracted_r.clone();
                                    reversedList.stream().forEach(e -> e = new StringBuilder(e).reverse().toString());
                                    mirrorLines += (String.join(" ", reversedList)) + "\n";
                                    mirrorLines += (image.getStatus() == Status.POSITIVE ? "1" : "0") + "\n";
                                }
                            }
                        }
                        return null;
                    }
                };
            }
        };
        service.setOnSucceeded(event1 -> {
            try {
                if(mirrorCheck.isSelected())
                    testCount *= 2;
                FileOutputStream outputStream = new FileOutputStream(outputPath.getAbsolutePath() + File.separator + nameField.getText());
                IOUtils.write("# Dataset generated with ptfc.\n", outputStream);
                IOUtils.write(String.format("%d %d %d\n", testCount, 1024, 1), outputStream);
                IOUtils.write(lines, outputStream);
                if(mirrorCheck.isSelected()) {
                    IOUtils.write(mirrorLines, outputStream);
                }
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        service.setOnFailed(event1 -> {
            InfoWindow.showInfo("Write error!", "Failed to write training data to file!", Main.getMainWindow());
        });
        ProgressDialog progressDialog = new ProgressDialog(service);
        progressDialog.initOwner(Main.getMainWindow());
        progressDialog.initModality(Modality.APPLICATION_MODAL);
        progressDialog.setTitle("Saving dataset...");
        service.start();
    }

    @FXML
    void loadPNGSourceButton(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(Main.getMainWindow());
        if (dir != null) {
            PNGSource pngSource = new PNGSource(dir);
            sourcesListView.getItems().add(pngSource);
        }
    }

    @FXML
    void setTrainOutPutPath(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(Main.getMainWindow());
        if(dir != null) {
            outputPath = dir;
            outputPathField.setText(dir.getAbsolutePath());
        }
    }
}
