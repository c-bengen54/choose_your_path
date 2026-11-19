package src;
//import java.util.Scanner;

import outpututils.*;


public class Main {
    public static void main(String[] args) throws java.io.IOException{
        Sprite image = new Sprite("sprites/snoopy.JPG");
        ShortSoundPlayer soundPlayer = new ShortSoundPlayer("soundeffects/bear-5-scream.wav");

        try{
            Thread.sleep(500);
            image.terminalOutput();
            Thread.sleep(1000);
            soundPlayer.soundOutput(500);

        } catch (InterruptedException e){
            e.printStackTrace();
        }
    } 
}
