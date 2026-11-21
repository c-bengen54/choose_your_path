package outpututils;

/**
 * Helpers for terminal text output: a typewriter effect, short pauses, and
 * 24-bit ANSI color formatting.
 *
 * <p>The color methods return strings containing ANSI escape codes and do not
 * print anything themselves. They require a terminal that supports 24-bit
 * ("truecolor") ANSI colors; other terminals may show approximate colors or
 * stray escape characters.
 *
 * <p>Example:
 * <pre>{@code
 * TextUtils.typewriterText(TextUtils.colorizeString("Hello!", "#00FFAA") + "\n", 30);
 * TextUtils.pause(500);
 * }</pre>
 */
public class TextUtils {
    private static final int MILLISECOND_DELAY = 50;
    private static final String RESET = "\u001B[0m";

    /**
     * Prints a message one character at a time with a default delay of 50
     * milliseconds between characters.
     *
     * <p>Every {@code char} is delayed, including the invisible characters of ANSI
     * escape sequences, so a string produced by one of the {@code colorizeString}
     * methods will pause unevenly. If the thread is interrupted, the stack trace
     * is printed and the remaining characters are not printed.
     *
     * @param message the text to print
     */
    public static final void typewriterText(String message){
        try{
            for (int i = 0; i < message.length(); i++){
                char character = message.charAt(i);
                System.out.print(character);
                Thread.sleep(MILLISECOND_DELAY);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * Prints a message one character at a time with a custom delay between
     * characters.
     *
     * <p>See {@link #typewriterText(String)} for notes on escape sequences and
     * interruption.
     *
     * @param message the text to print
     * @param delay   the delay after each character, in milliseconds; must not be
     *                negative, or {@link Thread#sleep(long)} throws an
     *                {@link IllegalArgumentException}
     */
    public static final void typewriterText(String message, int delay){
        try{
            for (int i = 0; i < message.length(); i++){
                char character = message.charAt(i);
                System.out.print(character);
                Thread.sleep(delay);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * Pauses the current thread for 100 milliseconds.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     */
    public static final void pause() throws InterruptedException{
        try{Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
        
    }

    /**
     * Pauses the current thread for the given number of milliseconds.
     *
     * @param delay the pause length in milliseconds; must not be negative
     * @throws InterruptedException     if the thread is interrupted while sleeping
     * @throws IllegalArgumentException if {@code delay} is negative
     */
    public static final void pause(int delay) throws InterruptedException, IllegalArgumentException{
        if (delay < 0){
            throw new IllegalArgumentException("Delay cannot be negative integer.");
        }

        Thread.sleep(delay);
    }

    /**
     * Checks that each RGB component is within 0 to 255 inclusive.
     *
     * @param red   the red component
     * @param green the green component
     * @param blue  the blue component
     * @throws IllegalArgumentException if any component is outside 0 to 255
     */
    private static final void validateRGB(int red, int green, int blue){
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255){
            throw new IllegalArgumentException("RGB values must be between 0 and 255");
        }
    }

    /**
     * Wraps a string in a 24-bit foreground color escape code followed by a
     * reset code.
     *
     * @param messageString the text to color
     * @param red           the red component, 0 to 255
     * @param green         the green component, 0 to 255
     * @param blue          the blue component, 0 to 255
     * @return the colorized string, ready to print
     * @throws IllegalArgumentException if any color component is outside 0 to 255
     */
    public static final String colorizeString(String messageString, int red, int green, int blue){
        validateRGB(red, green, blue);
        return String.format("\u001B[38;2;%d;%d;%dm%s%s", red, green, blue, messageString, RESET);
    }

    /**
     * Wraps a string in 24-bit foreground and background color escape codes
     * followed by a reset code.
     *
     * @param messageString the text to color
     * @param fgRed         the foreground red component, 0 to 255
     * @param fgGreen       the foreground green component, 0 to 255
     * @param fgBlue        the foreground blue component, 0 to 255
     * @param bgRed         the background red component, 0 to 255
     * @param bgGreen       the background green component, 0 to 255
     * @param bgBlue        the background blue component, 0 to 255
     * @return the colorized string, ready to print
     * @throws IllegalArgumentException if any color component is outside 0 to 255
     */
    public static final String colorizeString(String messageString, int fgRed, int fgGreen, int fgBlue, int bgRed, int bgGreen, int bgBlue){
        validateRGB(fgRed, fgGreen, fgBlue);
        validateRGB(bgRed, bgGreen, bgBlue);

        return String.format("\u001B[38;2;%d;%d;%dm\u001B[48;2;%d;%d;%dm%s%s", fgRed, fgGreen, fgBlue, bgRed, bgGreen, bgBlue, messageString, RESET);
    }

    /**
     * Wraps a string in a 24-bit foreground color, given as a hex code, followed
     * by a reset code.
     *
     * <p>The hex code must contain exactly six hex digits (for example
     * {@code "#FF8800"} or {@code "ff8800"}). Three-digit shorthand such as
     * {@code "#F80"} is not supported.
     *
     * @param messageString the text to color
     * @param hexCode       the color as {@code RRGGBB}, with or without a leading
     *                      {@code #}; must not be {@code null}
     * @return the colorized string, ready to print
     * @throws IllegalArgumentException if the hex code (excluding any {@code #})
     *                                  is not six characters long, or contains
     *                                  characters that are not hex digits
     *                                  ({@link NumberFormatException} is a
     *                                  subclass of this exception)
     * @throws NullPointerException     if {@code hexCode} is {@code null}
     */
    public static final String colorizeString(String messageString, String hexCode) throws IllegalArgumentException{
        String cleanHex = hexCode.startsWith("#") ? hexCode.substring(1) : hexCode;
        if (cleanHex.length() != 6){
            throw new IllegalArgumentException("Invalid hex code length.");
        }

        int red = Integer.parseInt(cleanHex.substring(0, 2), 16);

        int green = Integer.parseInt(cleanHex.substring(2, 4), 16);

        int blue = Integer.parseInt(cleanHex.substring(4, 6), 16);

        validateRGB(red, green, blue);

        return colorizeString(messageString, red, green, blue);
    }
}
