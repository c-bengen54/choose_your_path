package outpututils;
import java.util.Optional;
import java.util.Set;

/**
 * Detects which inline-image protocol, if any, the current terminal is likely to
 * support.
 *
 * <p>Detection is a best-effort guess based on environment variables
 * ({@code TERM_PROGRAM}, {@code TERM}, {@code KITTY_WINDOW_ID},
 * {@code ITERM2_SESSION_ID}, {@code LC_TERMINAL}). A program cannot query the
 * terminal directly, so a result other than {@link Protocol#NONE} means the
 * terminal usually supports that protocol, not that it is guaranteed to. For
 * example, VS Code sets {@code TERM_PROGRAM=vscode} whether or not
 * {@code terminal.integrated.enableImages} is turned on.
 *
 * @see Protocol
 */
public class ImageProtocol {
    private ImageProtocol(){
        //Prevent instatiation.
    }

    /**
     * The inline image protocols this class can detect.
     */
    public enum Protocol{
        /** The iTerm2 inline image protocol ({@code OSC 1337}); also used by WezTerm and others. */
        ITERM2,
        /** The Kitty graphics protocol. */
        KITTY,
        /**The Sixel graphics protocol. */
        SIXEL,
        /**No supported image protocol was detected. */
        NONE
    }

    /** {@code TERM_PROGRAM} values of terminals that generally accept the iTerm2 protocol. */
    private static final Set<String> ITERM2_COMPATIBLE = Set.of(
        "iTerm.app",
        "WezTerm",
        "mintty",
        "vscode",
        "Hyper",
        "Tabby"
    );

    /** {@code TERM_PROGRAM} values of terminals that generally accept the Kitty protocol. */
    private static final Set<String> KITTY_COMPATIBLE = Set.of(
        "kitty",
        "ghostty",
        "WarpTerminal" //Supports kitty graphics in newer versions
    );

    /**
     * Guesses which image protocol the current terminal supports by inspecting
     * environment variables.
     *
     * <p>Checks are made in this order, and the first match wins:
     * <ol>
     *   <li><b>Kitty</b>: {@code KITTY_WINDOW_ID} is set, {@code TERM_PROGRAM} is a
     *       known Kitty-compatible terminal, or {@code TERM} contains {@code "kitty"}.</li>
     *   <li><b>iTerm2</b>: {@code ITERM2_SESSION_ID} is set, {@code TERM_PROGRAM} is a
     *       known iTerm2-compatible terminal, or {@code LC_TERMINAL} is {@code "iTerm2"}.</li>
     *   <li><b>Sixel</b>: {@code TERM} contains {@code "foot"}, {@code "mlterm"}, or
     *       {@code "yaft"}, and the terminal is not Apple's Terminal.app.</li>
     *   <li>Otherwise, {@link Protocol#NONE}.</li>
     * </ol>
     *
     * @return the detected {@link Protocol}, or {@link Protocol#NONE} if none of the
     *         checks match; never {@code null}
     */
    public static Protocol detectProtocol(){
        String termProgram = Optional.ofNullable(System.getenv("TERM_PROGRAM")).orElse("");
        String term = Optional.ofNullable(System.getenv("TERM")).orElse("");

        if (System.getenv("KITTY_WINDOW_ID")!= null || KITTY_COMPATIBLE.contains(termProgram) || term.contains("kitty")){
            return Protocol.KITTY;
        } 

        if (System.getenv("ITERM2_SESSION_ID") != null || ITERM2_COMPATIBLE.contains(termProgram)|| "iTerm2".equals(System.getenv("LC_TERMINAL"))){
            return Protocol.ITERM2;
        } 

        if (termProgram.equals("Apple_Terminal") == false && (term.contains("foot") || term.contains("mlterm") || term.contains("yaft"))) {
            return Protocol.SIXEL; 
        }

        return Protocol.NONE;
    }

    /** Prints to the terminal which image protocol the terminal is currently using the {@link #detectProtocol()} method.*/
    public static void printProtocol(){
        System.out.println("Image Protocol: " + detectProtocol());
    }
}
