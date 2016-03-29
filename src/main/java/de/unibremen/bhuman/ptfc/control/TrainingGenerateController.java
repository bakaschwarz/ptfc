package de.unibremen.bhuman.ptfc.control;

import de.unibremen.bhuman.ptfc.Main;
import de.unibremen.bhuman.ptfc.data.ClassifiedImage;
import de.unibremen.bhuman.ptfc.data.PNGSource;
import de.unibremen.bhuman.ptfc.data.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TrainingGenerateController {

    @FXML
    private ListView<PNGSource> pngSourcesListView;

    @FXML
    private TextField trainNameField;

    @FXML
    private TextField trainOutPathField;

    @FXML
    private Button generateDataButton;

    private File pngOutputPath;

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
}
