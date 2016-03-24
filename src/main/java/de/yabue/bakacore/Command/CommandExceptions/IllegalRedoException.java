package de.yabue.bakacore.Command.CommandExceptions;

/**
 * Wird in der Regel immer dann geworfen, wenn versucht wird, ein Kommando zu wiederholen,
 * für welches eine solche Aktion nicht vorgesehen ist.
 *
 * @author Yannick Bülter
 */
public class IllegalRedoException extends Exception {
    public IllegalRedoException(String message) {
        super(message);
    }
}
