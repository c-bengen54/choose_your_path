package src;
import java.util.Scanner;
import outpututils.*;

public class Main {
    public static void main(String[] args) throws java.io.IOException{
        Sprite image = new Sprite("sprites/snoopy.JPG");
        image.terminalOutput();
        TextUtils.typewriterText(TextUtils.colorizeString("Test string", 255, 0, 0), 100);

    } 
}
