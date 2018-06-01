package config;

import representation.Image;

import java.util.ArrayList;

public class Config {

    public static final int MAX_WIDTH_IMG = 1920;
    public static final int MAX_HEIGHT_IMG = 1080;

    /* For Circle detection */
    public static final int MIN_SIZE_CIRCLE_RADIUS = 15;

    public static final int POINTS_ON_CIRCLE_AMOUNT = 13;
    public static final int VALID_POINTS_ON_CIRCLE_AMOUNT = 10;
    public static final int[] POINTS_ON_CIRCLE_DEGREES = new int[POINTS_ON_CIRCLE_AMOUNT];

    public static final int HSV_blackBinarization_TRESHOLD = 44;  //0-100 Value from HSV


    /* For Pattern checking 0 - 100[%] 0 - img is the same, 100 - is completely different*/
    public static final int INEQUALITY_PERCENTAGE = 16;

    public static final int MIN_SIZE_AREA = 40;

    public static final int MAX_SIZE_AREA = 200;

    public static final Image[] PATTERN_BASE = new Image[10];

    static{
        for(int i = 0; i < 10; i++){
            Image myImg = new Image();
            myImg.readImage("resources\\digits\\PatternBase\\digit"+i+".png");
            PATTERN_BASE[i] = myImg;
        }

        double degreeRange = 360/POINTS_ON_CIRCLE_AMOUNT;
        for(int i = 0; i < POINTS_ON_CIRCLE_AMOUNT; i++){
            POINTS_ON_CIRCLE_DEGREES[i] =(int)( i * degreeRange);
        }

    }

}
