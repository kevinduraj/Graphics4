package graphics4;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Graphics4 {

    int numberOfImages = 30;

    /*------------------------------------------------------------------------*/
    public static void main(String[] args) throws IOException {

        Graphics4 img = new Graphics4();
        //img.RGB_ColorSpace();
        img.YPbPr_ColorSpace();
    }

    /*------------------------------------------------------------------------*/
    public void RGB_ColorSpace() throws IOException {
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

        for (double i = 0; i <= numberOfImages; i++) {

            double currentAlpha = i / numberOfImages;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {

                    int Y = (int) (0.299 * red[x][y] + 0.587 * grn[x][y] + 0.114 * blu[x][y]);
                    
                    // Alpha Blend
                    double newRed   = ((1 - currentAlpha) * red[x][y] + currentAlpha * Y);
                    double newGreen = ((1 - currentAlpha) * grn[x][y] + currentAlpha * Y);
                    double newBlue  = ((1 - currentAlpha) * blu[x][y] + currentAlpha * Y);

                    int pixel = ((int) newRed << 16) | ((int) newGreen << 8) | ((int) newBlue);
                    bi.setRGB(x, y, pixel);

                }
            }

            String filename = String.format("/Users/ktd/Desktop/img/rose%02d.png", (int) i);
            System.out.println(filename);
            File outputfile = new File(filename);
            ImageIO.write(bi, "png", outputfile);
        }
    }
    
    /*------------------------------------------------------------------------
     * 1.) Read the image into RGB space 
     * 2.) Convert to YPbPr space
     * 3.) Fade it to gray scale within the YPbPr space
     * 4.) Convert it back to RGB space for saving to disk
     */
    public void YPbPr_ColorSpace() throws IOException {
        String s = "/Users/ktd/Desktop/rose.png"; 

        // -- read input image (1)
        File infile = new File(s);
        BufferedImage bi = ImageIO.read(infile);

        int width = bi.getWidth();
        int height = bi.getHeight();

        //Separate out image components
        int red[][] = new int[width][height];
        int grn[][] = new int[width][height];
        int blu[][] = new int[width][height];
        
        // 1.) Read the image into RGB space 
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                red[x][y] = bi.getRGB(x, y) >> 16 & 0xFF;
                grn[x][y] = bi.getRGB(x, y) >> 8 & 0xFF;
                blu[x][y] = bi.getRGB(x, y) & 0xFF;
                
                    // 2.) Convert to YPbPr space
                double Y  = (0.299 * red[x][y])  + (0.587  * grn[x][y])  + (0.114 * blu[x][y]); // & 0xFF;
                double Pb = (-0.168 * red[x][y]) + (-0.331 * grn[x][y]) + (0.500 * blu[x][y]); //& 0xFF;
                double Pr = (0.500 * red[x][y])  + (-0.418 * grn[x][y]) + (-0.081 * blu[x][y]);// & 0xFF;                   
               //int pixel = ((int) Y << 16) | ((int) Pb << 8) | ((int) Pr);
                red[x][y] = (int)Y  & 0xFF;
                grn[x][y] = (int)Pb & 0xFF;
                blu[x][y] = (int)Pr & 0xFF;
                //System.out.println( red[x][y] + "\t" + grn[x][y] + "\t" + blu[x][y]);
            }
        }
        
        


        /*-------------------- Convert to YPbPr ------------------------------*/
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (double i = 0; i <= numberOfImages; i++) {

            double Alpha = i / numberOfImages;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel;
                    //Original Image
                    //int pixel = ((int) red[x][y] << 16) | ((int) grn[x][y] << 8) | ((int) blu[x][y]);
                    double Gray = (0.299 * red[x][y]) + (0.587 * grn[x][y]) + (0.114 * blu[x][y]); // & 0xFF;

                    // 2.) Convert to YPbPr space
                    int Y = red[x][y]; // & 0xFF;
                    int Pb = grn[x][y];
                    int Pr = blu[x][y];

                    //3.) Fade it to gray scale within the YPbPr space
                    //int fadeY  = (int) ((1 - Alpha) * Y  + Alpha * Y) & 0xFF;
                    //int fadePb = (int) ((1 - Alpha) * Pb + Alpha * Y) & 0xFF;
                    //int fadePr = (int) ((1 - Alpha) * Pr + Alpha * Y) & 0xFF;

                    //3.) Fade it to gray scale within the YPbPr space
                    int fadeY = (int) ((1 - Alpha) * Y + Alpha * Gray) & 0xFF;
                    int fadePb = (int) ((1 - Alpha) * Pb + Alpha * Gray) & 0xFF;
                    int fadePr = (int) ((1 - Alpha) * Pr + Alpha * Gray) & 0xFF;

                    pixel = ((int) fadeY << 16) | ((int) fadePb << 8) | ((int) fadePr);

                    //4.) Convert it back to RGB space for saving to disk9
                    //int rgbRed   = (int) ((1.000 * fadeY) + (0.000  * fadePb) + (1.402  * fadePr)) & 0xFF;
                    //int rgbGreen = (int) ((1.000 * fadeY) + (-0.344 * fadePb) + (-0.714 * fadePr)) & 0xFF;
                    //int rgbBlue  = (int) ((1.000 * fadeY) + (1.772  * fadePb) + (0.000  * fadePr)) & 0xFF;             
                    //pixel = ((int) rgbRed << 16) | ((int) rgbGreen << 8) | ((int) rgbBlue);

                    bi.setRGB(x, y, pixel);

                }
            }

            String filename = String.format("/Users/ktd/Desktop/img/rose%02d.png", (int) i);
            System.out.println(filename);
            File outputfile = new File(filename);
            ImageIO.write(bi, "png", outputfile);
        }
    }

}
