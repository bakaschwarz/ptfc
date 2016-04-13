package de.unibremen.bhuman.ptfc.commands;

import de.unibremen.bhuman.ptfc.control.ClassifyController;
import de.unibremen.bhuman.ptfc.data.ClassifiedImage;
import de.unibremen.bhuman.ptfc.data.Status;
import de.yabue.bakacore.Command.FXCommand;

public class NoBallCommand extends FXCommand {

    @Override
    public void executeAction() {
        ClassifiedImage image = ClassifyController.getImageList().get(ClassifyController.getIndex());
        image.setStatus(Status.NEGATIVE);
        ClassifyController.setIndex(ClassifyController.getIndex() + 1);
        ClassifyController.setNegative(ClassifyController.getNegative() + 1);
    }

    @Override
    public void undoAction() {
        ClassifyController.setIndex(ClassifyController.getIndex() - 1);
        ClassifyController.setNegative(ClassifyController.getNegative() - 1);
        ClassifiedImage image = ClassifyController.getImageList().get(ClassifyController.getIndex());
        image.setStatus(Status.NOTHING);
    }

    @Override
    public void redoAction() {
        executeAction();
    }
}
