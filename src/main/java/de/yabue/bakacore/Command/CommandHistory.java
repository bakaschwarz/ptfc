package de.yabue.bakacore.Command;

import de.yabue.bakacore.Command.CommandExceptions.IllegalRedoException;
import de.yabue.bakacore.Command.CommandExceptions.IllegalUndoException;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;

import java.sql.SQLDataException;

/**
 * Implementiert eine Kommando Historie, die eine Vielzahl von Kommandos verwalten kann.
 * Sie dient dazu, dem Programmierer die Aufgabe abzunehmen, die Kommandos selbst verwalten zu müssen.
 *
 * @author Yannick Bülter
 */
public class CommandHistory {

    @Getter
    private final int size;

    private FXCommand[] commands;

    private int position;

    private final SimpleBooleanProperty canUndo;

    private final SimpleBooleanProperty canRedo;

    private FXCommand lastCommand;


    public CommandHistory(final int size) {
        this.size = size;
        commands = new FXCommand[size];
        position = -1;
        canUndo = new SimpleBooleanProperty(false);
        canRedo = new SimpleBooleanProperty(false);
    }

    /**
     * Führt ein übergebenes Kommando aus und fügt es der Kommando Historie hinzu, wenn es
     * rückgängig gemacht werden kann.
     *
     * @param command Das Kommando, das ausgeführt werden soll.
     */
    public void execute(final FXCommand command) {
        command.execute();
        if (command.isUndoable()) {
            if (position + 1 == size) {
                FXCommand[] temp = new FXCommand[size];
                for (int i = 0; i < size - 1; i++) {
                    temp[i] = commands[i + 1];
                }
                commands = temp;
            }
            position = position + 1 == size ? position : position + 1;
            commands[position] = command;
            canUndo.setValue(true);
        }
    }

    /**
     * Macht das letzte Kommando, welches ausgeführt wurde, rückgängig.
     */
    public void undo() {
        int old_pos = position;
        try {
            commands[position].undo();
            lastCommand = commands[position];
            position = position - 1 > -1 ? position - 1 : 0;
        } catch (IllegalUndoException e) {
            e.printStackTrace();
        }
        canUndo.setValue(commands[position].getCommandState() == ExecutionState.UNDO);
        canRedo.setValue(commands[old_pos].getCommandState() == ExecutionState.REDO);
    }

    /**
     * Führt das zuletzt rückgängig gemachte Kommando wieder aus.
     */
    public void redo() {
        if (canRedo.getValue()) {
            try {
                lastCommand.redo();
                for (int i = 0; i < size; i++) {
                    if (lastCommand == commands[i]) {
                        position = i;
                        canUndo.setValue(lastCommand.getCommandState() == ExecutionState.UNDO);
                    }
                }
            } catch (IllegalRedoException e) {
                e.printStackTrace();
            }
            canRedo.setValue(false);
        }
    }

    /**
     * Erlaubt es anderen Objekten zu wissen, ob Aktionen rückgängig gemacht werden können, oder nicht.
     *
     * @return {@code true}, wenn etwas rückgängig gemacht werden kann.
     */
    public ReadOnlyBooleanProperty undoProperty() {
        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(canUndo);
    }

    /**
     * Erlaubt es anderen Objekten zu wissen, ob Aktionen wiederholt werden können, oder nicht.
     *
     * @return {@code true}, wenn etwas wiederholt werden kann.
     */
    public ReadOnlyBooleanProperty redoProperty() {
        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(canRedo);
    }

}