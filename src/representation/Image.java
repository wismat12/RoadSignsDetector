package representation;

import basic.Circle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

public class Image {

    private int oldX;
    private int oldY;

    /** Store the Image reference */
    private BufferedImage image;

    /** Store the image width and height */
    private int width, height;

    /** Pixels value - ARGB */
    private int pixels[];

    /** Total number of pixel in an image*/
    private int totalPixels;

    private enum ImageType{
        JPG, PNG
    }

    private ImageType imgType;

    /** Default constructor */
    public Image(){}

    public Image(Image img){
        this.oldX = img.getOldX();
        this.oldY = img.getOldY();
        this.width = img.getImageWidth();
        this.height = img.getImageHeight();
        this.totalPixels = this.width * this.height;
        this.pixels = new int[this.totalPixels];

        this.imgType = img.imgType;
        if(this.imgType == ImageType.PNG){
            this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }else{
            this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }
        //copy original image pixels value to new image and pixels array
        for(int y = 0; y < this.height; y++){
            for(int x = 0; x < this.width; x++){
                this.image.setRGB(x, y, img.getPixel(x, y));
                this.pixels[x+y*this.width] = img.getPixel(x, y);
            }
        }
    }

    public void modifyImageObject(int width, int height, BufferedImage bi){
        this.width = width;
        this.height = height;
        this.totalPixels = this.width * this.height;
        this.pixels = new int[this.totalPixels];
        if(this.imgType == ImageType.PNG){
            this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }else{
            this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D g2d = this.image.createGraphics();
        g2d.drawImage(bi, 0, 0, null);
        g2d.dispose();
        initPixelArray();
    }

    public void resizeImageObject(int width, int height){

        this.width = width;
        this.height = height;
        this.totalPixels = this.width * this.height;
        this.pixels = new int[this.totalPixels];

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = newImage.createGraphics();
        g.drawImage(this.image, 0, 0, width, height, null);
        this.image = newImage;
        g.dispose();
        initPixelArray();
    }

    public void readImage(String filePath){
        try{
            File f = new File(filePath);
            image = ImageIO.read(f);

            String fileType = filePath.substring(filePath.lastIndexOf('.')+1);

            if("jpg".equals(fileType)){
                imgType = ImageType.JPG;
            }else{
                imgType = ImageType.PNG;
            }

            this.width = image.getWidth();
            this.height = image.getHeight();

            this.totalPixels = this.width * this.height;

            this.pixels = new int[this.totalPixels];

            initPixelArray();
        }catch(IOException e){
            System.out.println("Error Occurred!\n"+e);
        }
    }

    public void writeImage(String filePath){
        try{
            File f = new File(filePath);
            String fileType = filePath.substring(filePath.lastIndexOf('.')+1);
            ImageIO.write(image, fileType, f);
        }catch(IOException e){
            System.out.println("Error Occurred!\n"+e);
        }
    }

    public boolean isEqual(Image img, double inequalityPercentage){

        //check dimension
        if(this.width != img.getImageWidth() || this.height != img.getImageHeight()){
            return false;
        }
        double inequalityAmount = this.getImageTotalPixels() * (inequalityPercentage/100.0);
        int inequalityIndexer = 0;
        for(int y = 0; y < this.height; y++){
            for(int x = 0; x < this.width; x++){

                if(this.getRed(x,y) != img.getRed(x, y)){
                    inequalityIndexer++;
                    if(inequalityIndexer > inequalityAmount)
                        return false;
                }
            }
        }
        return true;
    }
    /**
     *
     * Storing the value of each pixel of a 2D image in a 1D array.
     */
    private void initPixelArray(){
        PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
        try{
            pg.grabPixels();
        }catch(InterruptedException e){
            System.out.println("Error Occurred: "+e);
        }
    }

    public int getImageWidth(){
        return width;
    }

    public int getImageHeight(){
        return height;
    }

    public int getImageTotalPixels(){
        return totalPixels;
    }

    /**
     * This methods return the amount of particular value between 0-255 at the pixel (x,y)
     *
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     * @return the amount of particular px
     */
    public int getAlpha(int x, int y){
        return (pixels[x+(y*width)] >> 24) & 0xFF;
    }

    public int getRed(int x, int y){
        return (pixels[x+(y*width)] >> 16) & 0xFF;
    }

    public int getGreen(int x, int y){
        return (pixels[x+(y*width)] >> 8) & 0xFF;
    }

    public int getBlue(int x, int y){
        return pixels[x+(y*width)] & 0xFF;
    }

    public int getPixel(int x, int y){
        return pixels[x+(y*width)];
    }

    /**
     * This methods modify the amount of particular value between 0-255 at the pixel (x,y)
     *
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     */
    public void setAlpha(int x, int y, int alpha){
        pixels[x+(y*width)] = (alpha<<24) | (pixels[x+(y*width)] & 0x00FFFFFF);
        updateImagePixelAt(x,y);
    }

    public void setRed(int x, int y, int red){
        pixels[x+(y*width)] = (red<<16) | (pixels[x+(y*width)] & 0xFF00FFFF);
        updateImagePixelAt(x,y);
    }

    public void setGreen(int x, int y, int green){
        pixels[x+(y*width)] = (green<<8) | (pixels[x+(y*width)] & 0xFFFF00FF);
        updateImagePixelAt(x,y);
    }

    public void setBlue(int x, int y, int blue){
        pixels[x+(y*width)] = blue | (pixels[x+(y*width)] & 0xFFFFFF00);
        updateImagePixelAt(x,y);
    }

    public void setPixel(int x, int y, int alpha, int red, int green, int blue){
        pixels[x+(y*width)] = (alpha<<24) | (red<<16) | (green<<8) | blue;
        updateImagePixelAt(x,y);
    }

    public void setPixelToValue(int x, int y, int pixelValue){
        pixels[x+(y*width)] = pixelValue;
        updateImagePixelAt(x,y);
    }

    /**
     * This method will mark the entire circle circumference within square -  with a given color.
     *
     * @param img from img method gets absolute coords of source img
     * @param circle from circle method gets distance between inner and outer circle(edges)
     * @param rC color R
     * @param gC color G
     * @param bC color B
     * @param markSize frame size surrounding inner circle
     */
    @SuppressWarnings("Duplicates")
    public void markSignArea(Image img, Circle circle, int rC, int gC, int bC, int markSize){

        int xLU = img.getOldX() + circle.getS().getX() - circle.getR();
        int yLU = img.getOldY() + circle.getS().getY() - circle.getR();
        int r = circle.getR();
        int rangeX =xLU+ r*2;
        int rangeY =yLU+ r*2;

        for(int i = 0; i < markSize; i++){
            for(int x = xLU ; x < rangeX; x++){
                this.setPixel(x, yLU + i,0, rC,gC,bC);
            }
            for(int x = xLU ; x < rangeX; x++){
                this.setPixel(x, rangeY - i, 0, rC,gC,bC);
            }
        }
        for(int i = 0; i < markSize; i++){
            for(int y = yLU ; y < rangeY; y++){
                this.setPixel(xLU + i, y, 0, rC,gC,bC);
            }
            for(int y = yLU ; y < rangeY; y++){
                this.setPixel(rangeX - i, y, 0, rC,gC,bC);
            }
        }
    }

    /**
     * This method will update the image pixel at coordinate (x,y)
     *
     * @param x the x coordinate of the pixel that is set
     * @param y the y coordinate of the pixel that is set
     */
    private void updateImagePixelAt(int x, int y){
        image.setRGB(x, y, pixels[x+(y*width)]);
    }

    ////////////////////////////// HSV color model Methods /////////////////////

    /**
     * This method will return the hue of the pixel (x,y) as per HSV color model.
     *
     * @param x The x coordinate of the pixel.
     * @param y The y coordinate of the pixel.
     * @return H The hue value of the pixel [0-360] in degree.
     */
    public double HSV_getHue(int x, int y){
        int r = getRed(x,y);
        int g = getGreen(x,y);
        int b = getBlue(x,y);

        double H = Math.toDegrees(Math.acos((r - (0.5*g) - (0.5*b))/Math.sqrt((r*r)+(g*g)+(b*b)-(r*g)-(g*b)-(b*r))));
        H = (b>g)?360-H:H;

        return H;
    }

    /**
     * This method will return the saturation of the pixel (x,y) as per HSV color model.
     *
     * @param x The x coordinate of the pixel.
     * @param y The y coordinate of the pixel.
     * @return S The saturation of the pixel [0-100].
     */
    public double HSV_getSaturation(int x, int y){
        int r = getRed(x,y);
        int g = getGreen(x,y);
        int b = getBlue(x,y);

        int max = Math.max(Math.max(r, g), b);
        int min = Math.min(Math.min(r, g), b);

        double S = (max>0)?(1 - (double)min/max):0;

        return S * 100;
    }

    /**
     * This method will return the value of the pixel (x,y) as per HSV color model.
     *
     * @param x The x coordinate of the pixel.
     * @param y The y coordinate of the pixel.
     * @return V The value of the pixel [0-100].
     */
    public double HSV_getValue(int x, int y){
        int r = getRed(x,y);
        int g = getGreen(x,y);
        int b = getBlue(x,y);

        int max = Math.max(Math.max(r, g), b);
        double V = max/255.0;

        return V * 100;
    }

    /**
     * This method will set the hue of the pixel (x,y).
     *
     * @param x The x coordinate of the pixel.
     * @param y The y coordinate of the pixel.
     * @param hue The hue value that is set [0-360] in degree.
     */
    public void HSV_setHue(int x, int y, double hue){
        int rgb[] = HSV_getRGBFromHSV(hue, HSV_getSaturation(x,y), HSV_getValue(x,y));
        setPixel(x, y, getAlpha(x,y), rgb[0], rgb[1], rgb[2]);
    }

    /**
     * This method will set the saturation of the pixel (x,y).
     *
     * @param x The x coordinate of the pixel.
     * @param y The y coordinate of the pixel.
     * @param saturation The saturation value that is set [0-1].
     */
    public void HSV_setSaturation(int x, int y, double saturation){
        int rgb[] = HSV_getRGBFromHSV(HSV_getHue(x,y), saturation, HSV_getValue(x,y));
        setPixel(x, y, getAlpha(x,y), rgb[0], rgb[1], rgb[2]);
    }

    /**
     * This method will set the value of the pixel (x,y).
     *
     * @param x The x coordinate of the pixel.
     * @param y The y coordinate of the pixel.
     * @param value The value that is set [0-1].
     */
    public void HSV_setValue(int x, int y, double value){
        int rgb[] = HSV_getRGBFromHSV(HSV_getHue(x,y), HSV_getSaturation(x,y), value);
        setPixel(x, y, getAlpha(x,y), rgb[0], rgb[1], rgb[2]);
    }

    /**
     * This method will return the RGB value from HSV value.
     *
     * @param H Hue of the pixel.
     * @param S Saturation of the pixel.
     * @param V Value of the pixel.
     * @return The rgb value for the corresponding HSV value.
     */
    private int[] HSV_getRGBFromHSV(double H, double S, double V){
        int rgb[] = new int[3];
        int r = 0, g = 0, b = 0;

        double max = 255.0*V;
        double min = max*(1-S);
        double tmp = (max-min)*(1 - Math.abs((H/60)%2 - 1));

        H %= 360;

        if(H < 60){
            r = (int)Math.round(max);
            g = (int)Math.round(tmp+min);
            b = (int)Math.round(min);
        }else if(H < 120){
            r = (int)Math.round(tmp+min);
            g = (int)Math.round(max);
            b = (int)Math.round(min);
        }else if(H < 180){
            r = (int)Math.round(min);
            g = (int)Math.round(max);
            b = (int)Math.round(tmp+min);
        }else if(H < 240){
            r = (int)Math.round(min);
            g = (int)Math.round(tmp+min);
            b = (int)Math.round(max);
        }else if(H < 300){
            r = (int)Math.round(tmp+min);
            g = (int)Math.round(min);
            b = (int)Math.round(max);
        }else if(H < 360){
            r = (int)Math.round(max);
            g = (int)Math.round(min);
            b = (int)Math.round(tmp+min);
        }

        rgb[0]=r;
        rgb[1]=g;
        rgb[2]=b;

        return rgb;
    }

    public int getOldX() {
        return oldX;
    }

    public void setOldX(int oldX) {
        this.oldX = oldX;
    }

    public int getOldY() {
        return oldY;
    }

    public void setOldY(int oldY) {
        this.oldY = oldY;
    }

}
