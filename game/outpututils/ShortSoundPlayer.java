package outpututils;
import javax.sound.sampled.*;
import java.io.*;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Plays short sound effects from {@code .wav}, {@code .aiff}, or {@code .au} files.
 *
 * <p>Playback uses {@link javax.sound.sampled.Clip}, which loads the entire file
 * into memory, so this class is intended for short sounds rather than long music
 * tracks. MP3 is not supported. Audio goes directly to the operating system's
 * audio output, so it works regardless of which terminal the program runs in
 * (over SSH, the sound plays on the remote machine).
 *
 * <p>All playback methods <b>block</b> the calling thread until playback finishes.
 * Run them on a separate thread if you need other work to continue (for example,
 * in a GUI application). playAsync is one of the <b>non-blocking</b> methods which uses
 * a background thread to play sound while the program executes.
 *
 * <p>Example:
 * <pre>{@code
 * ShortSoundPlayer click = new ShortSoundPlayer("sounds/click.wav");
 * click.soundOutput();     // play the full clip
 * click.soundOutput(500);  // play at most the first 500 ms
 * }</pre>
 */
public class ShortSoundPlayer {
    private String filePath;
    private static final Set<String> SOUND_FILE_ENDINGS = Set.of("wav", "aiff", "au");

    /**
     * Creates a player for the given sound file.
     *
     * <p>The path is validated by file extension only. The file is not opened and
     * its existence is not checked until one of the {@code soundOutput} methods
     * is called.
     *
     * @param soundFilePath the path to the sound file; must end in {@code .wav},
     *                      {@code .aiff}, or {@code .au} (case-insensitive)              
     * @throws IllegalArgumentException if {@code soundFilePath} is {@code null}
     *                                  or contains no {@code .}, or
     *                                  if the {@code .} is the last character of
     *                                  the path, or the extension is not one of
     *                                  the supported types
     */
    public ShortSoundPlayer(String soundFilePath) throws IllegalArgumentException{

        if (soundFilePath == null || !soundFilePath.contains(".")){
            throw new IllegalArgumentException("File name cannot be null, or must contain a .");
        }

        int dotIndex = soundFilePath.lastIndexOf(".");

        if (dotIndex == soundFilePath.length()-1){
            throw new IllegalArgumentException("Invalid file name, . must not be last character");
        }

        if (!SOUND_FILE_ENDINGS.contains(soundFilePath.substring(dotIndex+1).toLowerCase())){
            throw new IllegalArgumentException("Incorrect file type, must be .wav, .aiff, or .au");
        }

        this.filePath = soundFilePath;
    }


    /**
     * Plays the entire sound clip, then returns.
     *
     * <p>This method blocks the calling thread for the full length of the clip.
     * Playback errors (missing file, unsupported audio format, no available audio
     * device) are not thrown; the stack trace is printed to standard error and
     * the method returns normally. If the thread is interrupted while waiting,
     * the stack trace is printed and playback is cut off when the clip is closed.
     */
    public void soundOutput(){
        soundOutput(Integer.MAX_VALUE);
    }

    /**
     * Plays the sound clip for at most the given number of milliseconds, then
     * stops it and returns.
     *
     * <p>If {@code milliseconds} is longer than the clip, the whole clip is played
     * and the method returns when it ends. This method blocks the calling thread
     * while the sound plays.
     *
     * <p>Playback errors (missing file, unsupported audio format, no available
     * audio device) are not thrown; the stack trace is printed to standard error
     * and the method returns normally. If the calling thread is interrupted while
     * waiting, its interrupt flag is restored and the clip is not explicitly
     * stopped or closed by this method.
     *
     * @param milliseconds the maximum playback time in milliseconds; must be
     *                     zero or greater
     * @throws IllegalArgumentException if {@code milliseconds} is negative
     */
    public void soundOutput(int milliseconds) throws IllegalArgumentException{
        if (milliseconds < 0) {
            throw new IllegalArgumentException("Milliseconds should be greater than 0");
        }

        CountDownLatch clipDone = new CountDownLatch(1);
        
        try(
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
        ){

            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP){clipDone.countDown();}
            });
            
            clip.open(audioStream);
            clip.start();


            clipDone.await(milliseconds, TimeUnit.MILLISECONDS);
            clip.stop();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Plays the entire sound clip without blocking the calling thread.
     *
     * <p>This method returns as soon as playback has started. The audio is played
     * by Java Sound's own background thread, and the clip closes itself
     * automatically when playback stops, so the caller does not need to wait or
     * clean up.
     *
     * <p>Each call opens a new {@link javax.sound.sampled.Clip} and rereads the file
     * from disk, so calling this method repeatedly plays overlapping copies of the
     * sound at the same time.
     *
     * <p>Errors that occur while setting up playback (missing file, unsupported
     * audio format, no available audio device) are not thrown; the stack trace is
     * printed to standard error and the method returns without playing anything.
     * Errors that occur after playback has started are not reported.
     *
     * <p>Playback may be cut off if the program exits before the sound finishes.
     *
     * @see #soundOutput()
     */
    public void playAsync() {
        try (AudioInputStream in = AudioSystem.getAudioInputStream(new File(filePath))) {
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) clip.close();
            });
            clip.open(in);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
