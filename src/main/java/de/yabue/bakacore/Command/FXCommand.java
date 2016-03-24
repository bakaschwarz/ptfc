package de.yabue.bakacore.Command;

import de.yabue.bakacore.Command.CommandExceptions.IllegalRedoException;
import de.yabue.bakacore.Command.CommandExceptions.IllegalUndoException;
import lombok.Getter;

import java.sql.SQLDataException;

/**
 * Ein Kommando ist die Kapselung für die Ausführung einer bestimmten Aktion. Eine Aktion kann jede Art von Berechnung sein, die in irgendeiner
 * Weise beobachtet, verfolgt, rückgängig und/oder wiederholt werden soll.
 *
 * @author Yannick Bülter
 */
public abstract class FXCommand {

    @Getter
    private final boolean undoable;

    @Getter
    private final boolean redoable;

    @Getter
    private ExecutionState commandState;

    /**
     * Konstruiert ein neues FXCommand.
     *
     * @param undoable Entscheidet, ob dieses Kommando rückgängig gemacht werden kann, oder nicht.
     * @param redoable Entscheidet, ob dieses Kommando wiederholt ausgeführt werden kann, oder nicht.
     */
    public FXCommand(final boolean undoable, final boolean redoable) {
        this.undoable = undoable;
        this.redoable = redoable;
        commandState = ExecutionState.NOT;
    }

    /**
     * Konstruiert ein neues FXCommand, welches sowohl rückgängig gemacht, als auch wiederholt werden kann.
     */
    public FXCommand() {
        this.undoable = true;
        this.redoable = true;
        commandState = ExecutionState.NOT;
    }

    /**
     * Führt ein Kommando aus.
     */
    public final void execute() {
        executeAction();
        commandState = ExecutionState.UNDO;
    }

    /**
     * Macht ein Kommando rückgängig, wenn es möglich ist.
     *
     * @throws IllegalUndoException
     */
    public final void undo() throws IllegalUndoException {
        if (undoable) {
            if (commandState == ExecutionState.UNDO) {
                undoAction();
                commandState = ExecutionState.REDO;
            } else {
                if (commandState == ExecutionState.NOT) {
                    throw new IllegalUndoException("Das Kommando wurde noch nicht ausgeführt! Es muss nichts rückgängig gemacht werden!");
                } else if (commandState == ExecutionState.REDO) {
                    throw new IllegalUndoException("Das Kommando wurde bereits rückgängig gemacht!");
                }
            }
        } else {
            throw new IllegalUndoException("Das Kommando darf nicht rückgängig gemacht werden!");
        }
    }

    /**
     * Wiederholt eine Aktion, wenn es möglich ist.
     *
     * @throws IllegalRedoException
     */
    public final void redo() throws IllegalRedoException {
        if (redoable) {
            if (commandState == ExecutionState.REDO) {
                redoAction();
                commandState = ExecutionState.UNDO;
            } else {
                if (commandState == ExecutionState.NOT) {
                    throw new IllegalRedoException("Das Kommando wurde noch nicht ausgeführt! Es kann nichts wiederholt werden!");
                } else if (commandState == ExecutionState.UNDO) {
                    throw new IllegalRedoException("Das Kommando wurde noch nicht rückgängig gemacht und kann daher nicht wiederholt werden!");
                }
            }
        } else {
            throw new IllegalRedoException("Kommando darf nicht wiederholt werden!");
        }
    }

    /**
     * Führt die Aktion dieses Kommandos aus. Der Programmierer sollte diese Methode nur implementieren, aber nicht aufrufen.
     * Nutze stattdessen {@link #execute()}.
     */
    protected abstract void executeAction();

    /**
     * Macht die Aktion von {@link #executeAction()} wieder ungeschehen. Der Programmierer sollte diese Methode nur implementieren, aber nicht aufrufen.
     * Nutze stattdessen {@link #undo()}.
     */
    protected abstract void undoAction();

    /**
     * Wiederholt die Aktion von {@link #executeAction()}. In den meisten Fällen ist es ausreichend, wenn hier einfach {@link #executeAction()} aufgerufen wird.
     * Der Programmierer sollte diese Methode nur implementieren, aber nicht aufrufen. Nutze stattdessen {@link #redo()}.
     */
    protected abstract void redoAction();

}