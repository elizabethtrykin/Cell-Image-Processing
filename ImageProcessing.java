//Used http://www.jhlabs.com/ip/index.html for guidance on different filters

import java.awt.image.*;
import java.util.ArrayList;
import java.awt.Point;
import java.util.*;

public class ImageProcessing {

    public static BufferedImage contrast(BufferedImage input, double maxVal) {

        // Working out alow and ahigh
        int[] histogram = new int[256];
        int[] histogramValue = new int[256];
        
        int num = 0;

        int width = input.getWidth();
        int height = input.getHeight();

        //Low + High pixel value
        double tLow = width * height * maxVal;
        double tHigh = width * height * (1 - maxVal);

        int a, b;
        int low = 0;
        int high = 0;
        int newPixel;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                
                a = input.getRGB(i, j);
                b = a & 0xff; //RGB value

                histogramValue[b]++; //Creates an array with pixel values as the indexes
            }
        }

        // Creating an actual histogram for the image
        for (int i = 0; i < histogramValue.length; i++) {
            num += histogramValue[i];
            histogram[i] = num;
        }

        // Lowest value from the new histogram
        for (int i = 0; i < histogram.length; i++) {
            if (histogram[i] >= tLow) {
                low = i;
                    break; // Exits the loop
                }
            }

            // Highest value from the new histogram
            for (int i = histogram.length - 1; i >= 0; i--) { //loop goes backwards this time
                if (histogram[i] > 0 && histogram[i] <= tHigh) {
                    high = i;
                    break;// Exiting the loop
                }
            }

            //Mapping values to increase pixel value ranges the image
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    
                    a = input.getRGB(i, j);
                    b = a & 0xff;

                    if (b <= low) {
                        newPixel = 0;
                    } else if (b > low && b < high) {
                        newPixel = (b - low) * (255 / (high - low)); //sets contrasting
                    } else {
                        newPixel = 255;
                    }
                    // Replacing pixel value
                    b = newPixel;

                    // replace RGB value with avg
                    a = (b << 24) | (b << 16) | (b << 8) | b;

                    input.setRGB(i, j, a);
                }
            }
            return input;
        }


    //Gaussian blurs the image - Req: image has to be greyscale 
    public static BufferedImage gaussianBlur(BufferedImage input) {

        float[] kernel = { 0.25f, 0.5f, 0.25f };

        //ConvolveOp is a class that allows for convolution: multiplying an input pixel by a kernel (another word for filter) and computes an ouput pixel. 
        //--> https://docs.oracle.com/javase/7/docs/api/java/awt/image/ConvolveOp.html

        //filters horizantally
        BufferedImageOp opH = new ConvolveOp(new Kernel(3, 1, kernel), ConvolveOp.EDGE_NO_OP, null); //EDGE_NO_OP ensures that the edge pixels are copied without modification. 
        BufferedImage output = opH.filter(input, null);

        //filters vertically
        BufferedImageOp opV = new ConvolveOp(new Kernel(1, 3, kernel), ConvolveOp.EDGE_NO_OP, null);
        return opV.filter(output, null); //Returns the output image
    }


    //Gamma corrests the image
    public static BufferedImage gamma(BufferedImage input, double gamma) {

        //Creating a variable in order to reduces computational power required
        int width = input.getWidth();
        int height = input.getHeight();
        

        int a;
        double b;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                a = input.getRGB(i, j);
                b = a & 0xff;

                //Preforming Gamma Correction for pixel
                b = Math.pow((b / 255), gamma) * 255;

                // replace RGB value with new pixel value
                a = ((int) b << 24) | ((int) b << 16) | ((int) b << 8) | (int) b;

                input.setRGB(i, j, a); //Sets according RGB value to each pixel
            }
        }
        return input;
    }


    //Thresholds the image to be completely black & white
    public static BufferedImage thresholding(BufferedImage input, int thresholdVal) {

        int width = input.getWidth();
        int height = input.getHeight();
        int a,b;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                a = input.getRGB(i, j);
                b = a & 0xff;

                //If the pixel is above thresholding value, set to 255 (white), otherwise, set to 0 (black) 
                if (b >= thresholdVal) {
                    b = 255;
                } else {
                    b = 0;
                }

                //replace the RGB values with new values
                a = (b << 24) | (b << 16) | (b << 8) | b;

                input.setRGB(i, j, a);
            }
        }
        return input;
    }

    // Image opening to remove small objects in the foreground --> Erodes image, then dilates. Num is how many times the erosion/dilationn is to happen. 
    public static BufferedImage opening(BufferedImage input, int num) {

        // Preforming Erosion
        for (int i = 0; i < num; i++) {
            input = erode(input);
        }

        // preforming Dilation
        for (int i = 0; i < num; i++) {
            input = dilate(input);
        }

        return input;
    }

    //Image closing to remove small holes --> Dilates image, then erodes. Num is how many times the erosion/dilationn is to happen. 
    public static BufferedImage closing(BufferedImage input, int num) {

        // Preforming Erosion
        for (int i = 0; i < num; i++) {
            input = dilate(input);
        }

        // preforming Dilation
        for (int i = 0; i < num; i++) {
            input = erode(input);
        }

        return input;
    }


    //Inverts the image to prepare for dilation
    public static BufferedImage inverting(BufferedImage input) {
        
        int width = input.getWidth();
        int height = input.getHeight();
        
        int a,b;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                a = input.getRGB(i, j);
                b = a & 0xff;
                
                //Inverts pixel. This image is black and white, which means this could've been done with an if statement, but that would be a bit more computationally intensive. 
                b = 255 - b;
                
                // replace RGB value with the new value
                a = (b << 24) | (b << 16) | (b << 8) | b;

                input.setRGB(i, j, a);
            }
        }
        return input;
    }

    // Erodes an image
    public static BufferedImage erode(BufferedImage input) {
        int height = input.getHeight();
        int width = input.getWidth();
        
        int filterPosition;
        int a, b, newPixel;
        int num = 0;

        //clones the input image
        BufferedImage newImage = deepCopy(input);

        //kernel or filter that helps erode black pixels that aren't cells
        int[] kernel = {0,1,0,1,1,1,0,1,0};

        for (int i = 1; i <= width - 3; i++) {
            for (int j = 1; j <= height - 3; j++) {

                //RGB value of the pixel
                a = input.getRGB(i, j);
                b = a & 0xff;

                // If the pixel is black
                if (b == 0) {

                    for (int iw = -1; iw <= 1; iw++) {
                        for (int jw = -1; jw <= 1; jw++) {
                            
                            //RGB value of the pixels in a 3x3 square around the original pixel
                            a = input.getRGB(i + iw, j + jw);
                            b = a & 0xff;

                            // If the pixel is white and the kernel[num] (position of the kernel) is one
                            if (b == 255 && kernel[num] == 1) {
                                newPixel = (0 << 24) | (0 << 16) | (0 << 8); //changes pixel to black
                                newImage.setRGB(i + iw, j + jw, newPixel);
                            }
                            num++;
                        }
                    }
                    num = 0; //resets num
                }
            }
        }
        return newImage;
    }

    //Dilates black parts of the image, which is just eroding the inverted verison of the image
    public static BufferedImage dilate(BufferedImage input) {
        //First the image is inverted, then eroded, and then inverted back to the way it was originally. 
        return inverting(erode(inverting(input)));
    }

    //Counts regions and labels them - must use a binary image - Used to count cells in the image
    public static int cellCount(BufferedImage input) {

        int count = 0;
        int a, b;

        int width = input.getWidth();
        int height = input.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                a = input.getRGB(i, j);
                b = a & 0xff;

                //If pixel is white
                if (b == 255) {
                    //calls the floodfill method
                    fillRed(input, i, j);
                    count++;
                }
            }
        }
        return count;
    }

    //Preforms foodfill DFS version - on a given starting pixel u,v
    private static BufferedImage fillRed(BufferedImage input, int i, int j) {

        //Double ended que 
        //Deque<Point> stack = new LinkedList<>();

        //Could use the Stack class, but Deque is more efficient for single-threaded code. 
        Deque<Point> arr = new ArrayDeque<Point>();
        
        int width = input.getWidth();
        int height = input.getHeight();
        
        int a,r,g,b,p,finalValue;

        a = 255;
        r = 255;
        g = 0;
        b = 0;

        //Setting replacment pixel value
        finalValue = (a << 24) | (r << 16) | (g << 8) | b;


        arr.push(new Point(i, j));

        while (!arr.isEmpty()) {
            Point point = arr.pop(); //removes value from array + retturns that value
            int x = (int)point.getX();
            int y = (int)point.getY();

            if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {

                p = input.getRGB(x,y);
                b = p & 0xff;

                if(b == 255){
                    input.setRGB(x, y, finalValue);
                    //Pushes new points to the array
                    arr.push(new Point(x + 1, y));
                    arr.push(new Point(x, y + 1));
                    arr.push(new Point(x, y - 1));
                    arr.push(new Point(x - 1, y));
                } 
            }
        }
        return input;
    }

    // Produces a full copy of a Buffered Image 
    // --> code copied from https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage/3514297#3514297
    public static BufferedImage deepCopy(BufferedImage img) {
        ColorModel cm = img.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = img.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
