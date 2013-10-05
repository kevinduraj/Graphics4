package graphics4;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Graphics4 {

    public static void main(String[] args) throws IOException {



        String s = "/Users/ktd/Desktop/rose.png"; // -- read image from project directory

        // -- read input image (1)
        File infile = new File(s);
        BufferedImage bi = ImageIO.read(infile);

        int width = bi.getWidth();
        int height = bi.getHeight();

        // -- separate out image components (2)
        int red[][] = new int[width][height];
        int grn[][] = new int[width][height];
        int blu[][] = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                red[x][y] = bi.getRGB(x, y) >> 16 & 0xFF;
                grn[x][y] = bi.getRGB(x, y) >> 8 & 0xFF;
                blu[x][y] = bi.getRGB(x, y) & 0xFF;
                //System.out.printf("red=%d green=%d blue=%d \n"
                // , red[x][y], grn[x][y], blu[x][y]);
            }
        }

        /*----------------------- Part 1----------------------------------*/
        // -- move image into BufferedImage object
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        
        int numberOfImages = 100;
        for (double i = 0; i <= numberOfImages; i++) {
            
            double currentAlpha = i / numberOfImages;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {

                    int Y = (int) (0.299 * red[x][y] + 0.587 * grn[x][y] + 0.114 * blu[x][y]);
                    // Alpha Blend
                    double newRed   =  ((1 - currentAlpha) * red[x][y] + currentAlpha * Y);
                    double newGreen =  ((1 - currentAlpha) * grn[x][y] + currentAlpha * Y);
                    double newBlue  =  ((1 - currentAlpha) * blu[x][y] + currentAlpha * Y);

                    int pixel = ((int)newRed << 16) | ((int)newGreen << 8) | ((int)newBlue);
                    bi.setRGB(x, y, pixel);
                    
                }
            }

            String filename = String.format("/Users/ktd/Desktop/img/rose%02d.png", (int)i);
            System.out.println(filename);
            File outputfile = new File(filename);
            ImageIO.write(bi, "png", outputfile);
        }
    }
}



