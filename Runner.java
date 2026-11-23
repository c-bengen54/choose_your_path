import java.util.Scanner;
import characters.*;

public class Runner {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws java.io.IOException, java.lang.InterruptedException{
        bear5 bear5 = new bear5(100, 100, "House");
        System.out.println(bear5.getHealth());
    } 
}
