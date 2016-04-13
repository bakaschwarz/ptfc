package de.unibremen.bhuman.ptfc.data;

import lombok.Data;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class PNGSource {

    private File source;

    private List<ClassifiedImage> images;

    private int positive, negative;

    public PNGSource(File source) {
        this.source = source;
        positive = 0;
        negative = 0;
        images = new ArrayList<>();
        for(File file : source.listFiles()) {
            if(FilenameUtils.getExtension(file.getName()).equals("png")) {
                ClassifiedImage image = new ClassifiedImage();
                image.setPath(file);
                String fake = file.getName().substring(0, 2);
                if(fake.equals("f_")) {
                    image.setStatus(Status.NEGATIVE);
                    negative++;
                } else {
                    image.setStatus(Status.POSITIVE);
                    positive++;
                }
                images.add(image);
            }
        }
    }
}
