package detection;

import representation.Image;

public class Detect {

    public static void edge(Image img){

        int mask[] = new int[]{  -1, -1, -1,
                -1,  8, -1,
                -1, -1, -1};

        int maskSize = 3;   //The width of the mask.

        /**
         * Buffered array of pixels holds the intermediate value of pixels that
         * is multiplied with the mask to get the final value for the center
         * pixel under the mask.
         */
        int buff[];

        /**
         * This array will store the output of the edge detect operation which will
         * be later written back to the original image pixels.
         */
        int outputPixels[] = new int[img.getImageTotalPixels()];

        int width = img.getImageWidth();
        int height = img.getImageHeight();

        /** edge detect operation */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                /** Fill buff array */
                int i = 0;
                buff = new int[9];
                for(int r = y - (maskSize / 2); r <= y + (maskSize / 2); r++){
                    for(int c = x - (maskSize / 2); c <= x + (maskSize / 2); c++){
                        if(r < 0 || r >= height || c < 0 || c >= width){
                            /** Some portion of the mask is outside the image. */
                            int tr = r, tc = c;
                            if(r < 0){
                                tr = r+1;
                            }else if(r == height){
                                tr = r-1;
                            }
                            if(c < 0){
                                tc = c+1;
                            }else if(c == width){
                                tc = c-1;
                            }
                            buff[i] = img.getPixel(tc, tr);
                        }else{
                            buff[i] = img.getPixel(c, r);
                        }
                        i++;
                    }
                }

                /** Multiply mask with buff array to get the final value. */
                int sa=0, sr=0, sg=0, sb=0;
                for(i = 0; i < 9; i++){
                    sa += (mask[i]* getAlpha(buff[i]))/16;
                    sr += (mask[i]* getRed(buff[i]))/16;
                    sg += (mask[i]* getGreen(buff[i]))/16;
                    sb += (mask[i]* getBlue(buff[i]))/16;
                }

                /** Save result in outputPixels array. */
                int p = getPixel(sa, sr, sg, sb);
                outputPixels[x+y*width] = p;
            }
        }
        /** Write the output pixels to the image pixels */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                img.setPixelToValue(x, y, outputPixels[x+y*width]);
            }
        }
    }
    /////////////////////////// ARGB METHODS ///////////////////////////////////

    /**
     * This method will return alpha value from the pixel value.
     *
     * @param pixelValue The pixel value from which alpha value is calculated.
     * @return Alpha value [0-255].
     */
    public static int getAlpha(int pixelValue){
        return (pixelValue>>24) & 0xFF;
    }

    /**
     * This method will return red value from the pixel value.
     *
     * @param pixelValue The pixel value from which red value is calculated.
     * @return Red value [0-255].
     */
    public static int getRed(int pixelValue){
        return (pixelValue>>16) & 0xFF;
    }

    /**
     * This method will return green value from the pixel value.
     *
     * @param pixelValue The pixel value from which green value is calculated.
     * @return Green value [0-255].
     */
    public static int getGreen(int pixelValue){
        return (pixelValue>>8) & 0xFF;
    }

    /**
     * This method will return blue value from the pixel value.
     *
     * @param pixelValue The pixel value from which blue value is calculated.
     * @return Blue value [0-255].
     */
    public static int getBlue(int pixelValue){
        return pixelValue & 0xFF;
    }

    /**
     * This method will return pixel value from the ARGB value.
     *
     * @param alpha Alpha value [0-255].
     * @param red Red value [0-255].
     * @param green Green value [0-255].
     * @param blue Blue value [0-255].
     * @return Pixel value.
     */
    public static int getPixel(int alpha, int red, int green, int blue){
        return (alpha<<24) | (red<<16) | (green<<8) | blue;
    }
}
