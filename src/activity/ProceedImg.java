package activity;

import basic.Area;
import basic.Circle;
import config.Config;
import detection.Detect;
import representation.BasicProcessing;
import representation.Image;
import signArea.AreaDetection;
import signArea.PatternChecker;

import java.util.ArrayList;

public class ProceedImg {

    /* Briefly algorithm description
    1 Scaling (optional)
    2 HSV_redBinarization
    3 Computing areas - red objects on the image and theirs positions
    4 croping source imgs after binariaztion and rgb to previously detected areas
    5 edges detecting on red objects - white/black edges as output
    6 looking for circles - if point belong to circle
    7 marking circles on output img/frame
    8 croping every circle to inner one - we need only interior - preparing areas with black numbers
    9 croping rgb imgs to the same coordinates
    10 HSV_blackBinarization on rgb interior circles
    11 cleaning black pixels on the edges - cleaning edges
    12 detecting areas with only one number - we need only one digit per area
    13 comparing found digit with PatternBase
    14 if passed returned sign constraint in String
     */
    public static ArrayList<String> detectSigns(String path){

        int indexDigit = 0;
        ArrayList<String> detected = new ArrayList<>();

        Image baseImg = new Image();
        baseImg.readImage(path);

        //Scaling picture if needed
        if((baseImg.getImageWidth() > Config.MAX_WIDTH_IMG)||(baseImg.getImageHeight() > Config.MAX_HEIGHT_IMG)){
            baseImg.resizeImageObject(baseImg.getImageWidth()/2,baseImg.getImageHeight()/2);
        }

        Image rgbImgOut = new Image(baseImg);  //needed to create output

        BasicProcessing.HSV_redBinarization(baseImg);

        baseImg.writeImage("resources\\afterBinarization1.png");

        ArrayList<Area> areas = AreaDetection.detectAreas(baseImg, 1, Config.MIN_SIZE_AREA);

        System.out.println("areas size: "+ areas.size());
        int index = 1;
        int indexR = 0;
        for(Area area : areas) {
            System.out.println(area.getLeftUpper().getX());
            System.out.println(area.getRightLower().getX());
            Image newArea = new Image(baseImg);    // it's after red binarization - 0,255 value picture, needed to detecting edges end circles
            Image newAreaRGB = new Image(rgbImgOut);  //needed to hsv black binarization

            BasicProcessing.crop(newArea, area.getLeftUpper().getX(), area.getLeftUpper().getY(),
                    area.getRigthUpper().getX() - area.getLeftUpper().getX(),
                    area.getRightLower().getY() - area.getRigthUpper().getY());
            BasicProcessing.crop(newAreaRGB, area.getLeftUpper().getX(), area.getLeftUpper().getY(),
                    area.getRigthUpper().getX() - area.getLeftUpper().getX(),
                    area.getRightLower().getY() - area.getRigthUpper().getY());

            Detect.edge(newArea);
            newArea.writeImage("resources\\area" + (index++) + ".png");

            ArrayList<Circle> circles = AreaDetection.detectCircle(newArea);

            System.out.println("Centers size: " + circles.size());

            for (int i = 0; i < circles.size(); i++) {
                indexR++;
                Image newArea2 = new Image(newAreaRGB); //needed to extract black numbers

                rgbImgOut.markSignArea(newArea2, circles.get(i), 51, 255, 51, 3); //placing mark around the sign on output image

                System.out.println("x: " + circles.get(i).getS().getX() + " y:" + circles.get(i).getS().getY() + " r:" + circles.get(i).getR());

                BasicProcessing.crop(newArea2, circles.get(i).getS().getX() - circles.get(i).getR(),
                        circles.get(i).getS().getY() - circles.get(i).getR(),
                        circles.get(i).getR() * 2,
                        circles.get(i).getR() * 2);

                newArea2.writeImage("resources\\outColor" + (indexR) + ".png");

                BasicProcessing.HSV_blackBinarization(newArea2);

                //TODO cleaning edges - operacja czyszczenia brzegow - to przyciecie ponizej pozwala na chwilowe obejscie tego czyli usuniecie czarnych pxow z rogow img
                BasicProcessing.crop(newArea2,0, 4, newArea2.getImageWidth(), newArea2.getImageHeight() - 8);

                ArrayList<Area> digitsAreas = AreaDetection.detectAreas(newArea2, 0, 8);   //return areas with one particular number

                newArea2.writeImage("resources\\out" + (indexR) + ".png");

                String tmp = "";
                //System.out.println(digitsAreas.size());
                for (Area areaDigit : digitsAreas) {
                    indexDigit++;
                    tmp += String.valueOf(checkDigit(newArea2,areaDigit,indexDigit));
                }
                detected.add(tmp);
            }
        }
        rgbImgOut.writeImage("resources\\result.png");
        return detected;
    }

    private static String checkDigit(Image ImgToClone, Area area, int indexDigit){

        Image tmp = new Image(ImgToClone);

        BasicProcessing.crop(tmp, area.getLeftUpper().getX(), area.getLeftUpper().getY(),
                area.getRigthUpper().getX() - area.getLeftUpper().getX(),
                area.getRightLower().getY() - area.getRigthUpper().getY());

        tmp.resizeImageObject(20, 40);
        tmp.writeImage("resources\\digits\\digit" + indexDigit + ".png");
        return  String.valueOf(PatternChecker.recognisePattern(tmp));
    }
}