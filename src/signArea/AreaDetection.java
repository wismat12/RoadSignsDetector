package signArea;

import basic.Area;
import basic.Circle;
import basic.Line;
import basic.Point;
import config.Config;
import javafx.scene.effect.Light;
import representation.Image;

import java.util.ArrayList;
import java.lang.Math.*;

public class AreaDetection {

    static boolean isCenterOfCircle(int row, int col, int r, Image image) {

        int pointsOnCircle = 0;
        for(int t : Config.POINTS_ON_CIRCLE_DEGREES){
            int xp = (int)(row + r * Math.cos(t * Math.PI / 180));
            int yp = (int)(col + r * Math.sin(t * Math.PI / 180));

            if(image.getRed(xp,yp)>60){
                if((Math.sqrt(Math.pow(xp-row,2) + Math.pow(yp-col,2)) >= r - 1)&&(Math.sqrt(Math.pow(xp-row,2) + Math.pow(yp-col,2)) <= r + 1)){
                    pointsOnCircle++;
                }
            }
        }
        if(pointsOnCircle >5) {
            System.out.println(pointsOnCircle);
        }

        if(pointsOnCircle >= Config.VALID_POINTS_ON_CIRCLE_AMOUNT) {
            System.out.println("Center detected! x"+row + " y" + col + " points "+ pointsOnCircle);
            return true;
        }
       return false;
    }


    public static ArrayList<Circle> detectCircle(Image img) {
        /* https://en.wikipedia.org/wiki/Circle_Hough_Transform */

        ArrayList<Circle> circles = new ArrayList<>();
        int rmax;
        int imgHeight = img.getImageHeight();
        int imgWidth = img.getImageWidth();
        if(imgHeight > imgWidth){
            rmax = (img.getImageWidth()/2);
        }else {
            rmax = (img.getImageHeight()/2);
        }

        for(int y = rmax - 1 ; y < imgHeight; y++){
            for(int x = rmax - 1 ; x < imgWidth; x++){
                for(int r = Config.MIN_SIZE_CIRCLE_RADIUS; r < rmax ; r++){

                    if((x - r < 0)||(x + r >=imgWidth))
                        break;
                    if((y - r < 0)||(y + r >=imgHeight))
                        break;

                    if(isCenterOfCircle(x,y,r,img)){
                        Circle c = new Circle(new Point(x,y),r);
                        circles.add(c);
                    }
                }
            }
        }
        return circles;
    }

    @SuppressWarnings("Duplicates")
    public static ArrayList<Area> detectAreas(Image img, int treshold, int minSize){

        int[] histogramY = new int[img.getImageHeight()];
        int[] histogramX = new int[img.getImageWidth()];

        boolean isobjX = false;
        boolean isobjY = false;

        ArrayList<Line> linesX = new ArrayList<>();
        ArrayList<Line> linesY = new ArrayList<>();

        ArrayList<Area> areas = new ArrayList<>();

        for(int y = 0; y < img.getImageHeight(); y++){
            for(int x = 0; x < img.getImageWidth(); x++){

                if(img.getRed(x,y) == 0){
                    histogramY[y]++;
                    histogramX[x]++;
                }
            }
        }
        /*
        for(int i = 0; i < histogramX.length; i++){
            System.out.println(i+ " - " + histogramX[i]);
        }*/
        Line newLine = new Line();
        int x = 0;
        while(x < img.getImageWidth()){

            if(histogramX[x]>1){
                if(!isobjX){
                    //groupsAmountX++;
                    newLine.getBegin().setX(x);
                    isobjX=true;
                }

            }else{
                if(isobjX) {
                    newLine.getEnd().setX(x);
                    linesX.add(newLine);
                    newLine = new Line();
                    isobjX = false;
                }
            }
            x++;
        }
        if(isobjX){
            newLine.getEnd().setX(x-1);
            linesX.add(newLine);
        }

        newLine = new Line();
        int y = 0;
        while( y < img.getImageHeight()){

            if(histogramY[y]>1){
                if(!isobjY){
                    //groupsAmountY++;
                    newLine.getBegin().setY(y);
                    isobjY=true;
                }

            }else{
                if(isobjY) {
                    newLine.getEnd().setY(y);
                    linesY.add(newLine);
                    newLine = new Line();
                    isobjY = false;
                }
            }
            y++;
        }
        if(isobjY){
            newLine.getEnd().setY(y-1);
            linesY.add(newLine);
        }
/*
        System.out.println("pocz");
        System.out.println(linesX.get(0).getBegin().getX());
        System.out.println(linesX.get(0).getEnd().getX());
  //      System.out.println(linesX.get(1).getBegin().getX());
 //       System.out.println(linesX.get(1).getEnd().getX());

        System.out.println("cccc");
        System.out.println(linesY.get(0).getBegin().getY());
        System.out.println(linesY.get(0).getEnd().getY());
  //      System.out.println(linesY.get(1).getBegin().getY());
  //      System.out.println(linesY.get(1).getEnd().getY());
        System.out.println("kon");
*/
        for(Line lx: linesX){
            for(Line ly: linesY){
                if((lx.getEnd().getX() - lx.getBegin().getX() >= minSize)&&(ly.getEnd().getY() - ly.getBegin().getY() >= minSize)){
                    Area area = new Area();
                    area.getLeftUpper().setX(lx.getBegin().getX()-treshold);
                    area.getLeftUpper().setY(ly.getBegin().getY()-treshold);

                    area.getLeftLower().setX(lx.getBegin().getX()+treshold);
                    area.getLeftLower().setY(ly.getEnd().getY()+treshold);

                    area.getRigthUpper().setX(lx.getEnd().getX()+treshold);
                    area.getRigthUpper().setY(ly.getBegin().getY()-treshold);

                    area.getRightLower().setX(lx.getEnd().getX()+treshold);
                    area.getRightLower().setY(ly.getEnd().getY()+treshold);

                    areas.add(area);
                }
            }
        }
        System.out.println(areas.size());
        return areas;
    }
}
