package outpututils;

import java.util.Base64;
import java.util.Set;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

//This class is meant to create the image sprites that will be printed to the terminal
public class Sprite {
    private String filePath;
    private static final Set<String> imageFileEndings = Set.of("png", "jpg", "jpeg");  
    
    public Sprite(String imageFilePath) throws IOException{

        if (imageFilePath == null || !imageFilePath.contains(".")){
            throw new IOException("File name must not be null, or must contain a .");
        }

        int dotIndex = imageFilePath.indexOf(".");

        if (!imageFileEndings.contains(imageFilePath.substring(dotIndex+1).toLowerCase())){
            System.out.println(imageFilePath.substring(dotIndex+1).toLowerCase());

            throw new IOException("Incorrect file type, must be .png, .jpeg, or .jpg");
        }

        this.filePath = imageFilePath;
    }

    public void terminalOutput(){
        try{
            byte[] imageBytes = Files.readAllBytes(Path.of(filePath));

            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            System.out.printf("\033]1337;File=inline=1:%s\007\n", base64);
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
