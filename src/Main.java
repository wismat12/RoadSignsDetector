import activity.ProceedImg;
import signArea.PatternChecker;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ArrayList<String> detected = ProceedImg.detectSigns(("resources\\RealSigns\\znak70Real.jpg"));

        System.out.println("Detecting Signs: I found: ");
        System.out.println(detected);
    }
}
