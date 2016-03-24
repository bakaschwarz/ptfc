package de.unibremen.bhuman.ptfc.commands;

import de.unibremen.bhuman.ptfc.Controller;
import de.unibremen.bhuman.ptfc.data.ClassifiedImage;
import de.unibremen.bhuman.ptfc.data.Status;
import de.yabue.bakacore.Command.FXCommand;

public class IsBallCommand extends FXCommand {

    @Override
    public void executeAction() {
        ClassifiedImage image = Controller.getImageList().get(Controller.getIndex());
        image.setStatus(Status.BALL);
        Controller.setIndex(Controller.getIndex() + 1);
        Controller.setPositive(Controller.getPositive() + 1);
    }

    @Override
    public void undoAction() {
        Controller.setIndex(Controller.getIndex() - 1);
        Controller.setPositive(Controller.getPositive() - 1);
        ClassifiedImage image = Controller.getImageList().get(Controller.getIndex());
        image.setStatus(Status.NOTHING);
    }

    @Override
    public void redoAction() {
        executeAction();
    }
}
