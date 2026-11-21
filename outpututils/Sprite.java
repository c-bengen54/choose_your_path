package outpututils;
import java.util.Base64;
import java.util.Set;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

/**
 * Displays a PNG or JPEG image inline in a terminal that supports image output.
 *
 * <p>The image is sent to the terminal as an escape sequence using the iTerm2
 * inline image protocol. Terminals that do not support inline images would
 * otherwise print the raw Base64 data, so {@link #terminalOutput()} consults
 * {@link ImageProtocol#detectProtocol()} first and prints nothing when no
 * supported protocol is detected.
 *
 * <p>Example:
 * <pre>{@code
 * Sprite logo = new Sprite("assets/logo.png");
 * logo.terminalOutput();
 * }</pre>
 *
 * @see ImageProtocol
 */
public class Sprite {
    private String filePath;
    private static final Set<String> IMAGE_FILE_ENDINGS = Set.of("png", "jpg", "jpeg");  
    
    /**
     * Creates a sprite for the given image file.
     *
     * <p>The path is validated by file extension only. The file is not opened and
     * its existence is not checked until {@link #terminalOutput()} is called.
     *
     * @param imageFilePath the path to the image; must end in {@code .png},
     *                      {@code .jpg}, or {@code .jpeg} (case-insensitive)
     * @throws IllegalArgumentException if {@code imageFilePath} is {@code null}
     *                                  or contains no {@code .}, or
     *                                  if the {@code .} is the last character of
     *                                  the path, or the extension is not one of
     *                                  the supported types
     */
    public Sprite(String imageFilePath) throws IllegalArgumentException{

        if (imageFilePath == null || !imageFilePath.contains(".")){
            throw new IllegalArgumentException("File name must not be null, or must contain a .");
        }

        int dotIndex = imageFilePath.lastIndexOf(".");

        if (dotIndex == imageFilePath.length()-1){
            throw new IllegalArgumentException("Invalid file name, . must not be last character");
        }

        if (!IMAGE_FILE_ENDINGS.contains(imageFilePath.substring(dotIndex+1).toLowerCase())){ 
            throw new IllegalArgumentException("Incorrect file type, must be .png, .jpeg, or .jpg");
        }

        this.filePath = imageFilePath;
    }

    /**
     * Prints the image to standard output as an inline terminal image, followed
     * by a newline.
     *
     * <p>Nothing is printed if {@link ImageProtocol#detectProtocol()} does not
     * report a supported protocol. The image is displayed at its full size, so
     * large images may fill the terminal. If the file cannot be read, the
     * {@link IOException} is caught and its stack trace is printed to standard
     * error; no exception is thrown to the caller.
     *
     * <p>Whether the image actually appears depends on the terminal. Terminal
     * support cannot be verified from inside the program.
     * 
     * <p>Currently, only terminals with iTerm2 support are able to view images.
     */
    public void terminalOutput(){
        if (ImageProtocol.detectProtocol() != ImageProtocol.Protocol.ITERM2) {
            return;
        }

        try{
            byte[] imageBytes = Files.readAllBytes(Path.of(filePath));

            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            System.out.printf("\033]1337;File=inline=1:%s\007\n", base64);
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}