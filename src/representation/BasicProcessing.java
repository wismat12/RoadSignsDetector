package representation;

import config.Config;

import java.awt.image.BufferedImage;

public class BasicProcessing {

    public static void HSV_createHSVImage(Image img){
        for(int y = 0; y < img.getImageHeight(); y++){
            for(int x = 0; x < img.getImageWidth(); x++){

                int a = img.getAlpha(x, y);
                int h = (int)(img.HSV_getHue(x, y));
                int s = (int)((img.HSV_getSaturation(x, y)));
                int v = (int)((img.HSV_getValue(x, y)));

                img.setPixel(x, y, a, h, s, v);
            }
        }
    }
    /**
     * HSV red Binarization with default configuration
     *
     * @param img The image to proceed binarization.
     */
    public static void HSV_redBinarization(Image img){
        for(int y = 0; y < img.getImageHeight(); y++){
            for(int x = 0; x < img.getImageWidth(); x++){

                int a = img.getAlpha(x, y);
                int h = (int)(img.HSV_getHue(x, y));
                int s = (int)((img.HSV_getSaturation(x, y)));
                int v = (int)((img.HSV_getValue(x, y)));

                boolean isRed = (((h >= 0) && (h <= 24)) || ((h >= 330) && (h <= 360))) &&
                        ((s >= 45) && (s <= 100)) &&
                        ((v >= 45) && (v <= 100));
                if (isRed) {
                    img.setPixel(x, y, a, 0, 0, 0);
                }else {
                    img.setPixel(x, y, a, 255, 255, 255);
                }
            }
        }
    }
    /**
     * HSV black Binarization with default threshold
     *
     * @param img The image to proceed binarization.
     */
    public static void HSV_blackBinarization(Image img){
        HSV_blackBinarization(img, Config.HSV_blackBinarization_TRESHOLD);
    }
    /**
     * HSV black Binarization with fixed threshold
     *
     * @param img The image to proceed binarization.
     * @param threshold The image to proceed binarization.
     */
    public static void HSV_blackBinarization(Image img, int threshold){
        for(int y = 0; y < img.getImageHeight(); y++){
            for(int x = 0; x < img.getImageWidth(); x++){

                int a = img.getAlpha(x, y);
                int v = (int)((img.HSV_getValue(x, y)));

                boolean isblack = ((v >= 0) && (v <= threshold));

                if (isblack) {
                    img.setPixel(x, y, a, 0, 0, 0);
                }else {
                    img.setPixel(x, y, a, 255, 255, 255);
                }
            }
        }
    }
    /**
     * Cropping img
     *
     * @param img The image to crop.
     * @param x The x coordinate from where cropping will start.
     * @param y The y coordinate from where cropping will start.
     * @param width The width of the new cropped image.
     * @param height The height of the new cropped image.
     */
    public static void crop(Image img, int x, int y, int width, int height){
        img.setOldX(x);
        img.setOldY(y);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int sy = y, j = 0; sy < y+height; sy++, j++){
            for(int sx = x, i = 0; sx < x+width; sx++, i++){
                bi.setRGB(i, j, img.getPixel(sx, sy));
            }
        }
        img.modifyImageObject(width, height, bi);
    }

    ////////////////////////////// Grayscale Methods ///////////////////////////

    /**
     * This method will turn color image to gray scale image.
     * It will set the RGB value of the pixel to (R+G+B)/3
     *
     * @param img The image pixels to change.
     */
    public static void grayScale_Average(Image img){
        for(int y = 0; y < img.getImageHeight(); y++){
            for(int x = 0; x < img.getImageWidth(); x++){
                int a = img.getAlpha(x, y);
                int r = img.getRed(x, y);
                int g = img.getGreen(x, y);
                int b = img.getBlue(x, y);
                int grayscale = (r+g+b)/3;
                img.setPixel(x, y, a, grayscale, grayscale, grayscale);
            }
        }
    }
    /**
     * This method will turn color image to gray scale image.
     * In this method GrayScale = (max(R, G, B) + min(R, G, B)) / 2.
     *
     * @param img The image pixels to change.
     */
    public static void grayScale_Lightness(Image img){
        for(int y = 0; y < img.getImageHeight(); y++){
            for(int x = 0; x < img.getImageWidth(); x++){
                int a = img.getAlpha(x, y);
                int r = img.getRed(x, y);
                int g = img.getGreen(x, y);
                int b = img.getBlue(x, y);
                int max = Math.max(Math.max(r, g), b);
                int min = Math.min(Math.min(r, g), b);
                int grayscale = (max+min)/2;
                img.setPixel(x, y, a, grayscale, grayscale, grayscale);
            }
        }
    }
    /**
     * This method will turn color image to gray scale image.
     * This method uses the formula GrayScale = 0.2126*R + 0.7152*G + 0.0722*B
     *
     * @param img The image pixels to change.
     */
    public static void grayScale_Luminosity(Image img){
        for(int y = 0; y < img.getImageHeight(); y++){
            for(int x = 0; x < img.getImageWidth(); x++){
                int a = img.getAlpha(x, y);
                int r = img.getRed(x, y);
                int g = img.getGreen(x, y);
                int b = img.getBlue(x, y);
                int grayscale = (int)(0.2126*r + 0.7152*g + 0.0722*b);
                img.setPixel(x, y, a, grayscale, grayscale, grayscale);
            }
        }
    }
}
