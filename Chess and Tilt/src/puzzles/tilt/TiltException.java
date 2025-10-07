package puzzles.tilt;

/**
 * Java Exception subclass for an exception related to Tilt.
 * Takes a message in String form as a parameter and passes it
 * to the superclass constructor.
 *
 * @author Ryan O'Malley
 * @github cro5058
 */
public class TiltException extends RuntimeException {

    /** Constructor */
    public TiltException(String message) {
        // Pass the message to the superclass constructor.
        super(message);
    }
}
