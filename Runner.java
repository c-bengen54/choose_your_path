import java.util.Scanner;
import outpututils.*;

public class Runner {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws java.io.IOException, java.lang.InterruptedException{
        Sprite image = new Sprite("sprites/bear5_sprite.png");
        ShortSoundPlayer bear5Jumpscare = new ShortSoundPlayer("soundeffects/bear-5-scream.wav");
        image.terminalOutput();
        bear5Jumpscare.soundOutput();

        TextUtils.typewriterText(TextUtils.colorizeString("Hor\n", 0, 0, 0), 50);

        System.out.println(TextUtils.colorizeString("testing string", 25, 60, 150, 200, 60, 80, "5;"));
        Thread.sleep(2000);
    } 
}
