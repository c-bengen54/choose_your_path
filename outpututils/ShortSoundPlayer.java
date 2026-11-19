package outpututils;
import javax.sound.sampled.*;
import java.io.*;
import java.util.Set;

public class ShortSoundPlayer {
    private String filePath;
    private static final Set<String> fileEndings = Set.of("wav", "aiff", "au");

    public ShortSoundPlayer(String soundFilePath) throws IOException{

        if (soundFilePath == null || !soundFilePath.contains(".")){
            throw new IOException("File name cannot be null, or must contain a .");
        }

        int dotIndex = soundFilePath.indexOf(".");

        if (!fileEndings.contains(soundFilePath.substring(dotIndex+1).toLowerCase())){
            throw new IOException("Incorrect file type, must be .wav, .aiff, or .au");
        }

        this.filePath = soundFilePath;
    }

    public void soundOutput(){
        try{
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            clip.start();

            Thread.sleep(clip.getMicrosecondLength()/1000);

            clip.close();
            audioStream.close();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public void soundOutput(int milliseconds) {
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();

            clip.open(audioStream);
            clip.start();

            long clipMilliseconds = clip.getMicrosecondLength() / 1000;
            Thread.sleep(Math.min(milliseconds, clipMilliseconds));

            clip.stop();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
