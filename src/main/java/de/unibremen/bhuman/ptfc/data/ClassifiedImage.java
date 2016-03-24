package de.unibremen.bhuman.ptfc.data;

import javafx.scene.image.Image;
import lombok.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Data
public class ClassifiedImage {

    private Image image;

    private File path;

    private Status status;

    public ClassifiedImage(File path) throws FileNotFoundException {
        this.path = path;
        image = new Image(new FileInputStream(path));
        status = Status.NOTHING;
    }

    public ClassifiedImage() {
        image = null;
        path = null;
        status = Status.NOTHING;
    }
}
