import java.awt.*;

/**
 * Class for simple mandelbrot generator. Boundaries on real and imaginary axises are adjustable
 * as is the size of the generated image
 */
public class Mandelbrot implements Runnable{
    private double xmin = -2;
    private double xmax = 1;
    private double ymin = -1;
    private double ymax = 1;
    private double dx, dy;
    private int width, height;
    private int[] palette;
    private int maxIter = 4096;
    private int[] pixels;
    private Thread t;
    protected boolean running = false;
    private boolean calculating = false;

    /**
     * Constructs a setup for the mandelbrot generator. A palette of colours is generated using the hue as variable
     * (in the hsb-color model, hue is an angle rotating full circle giving a nice circular palette)
     * @param w int determining the width of the image
     * @param h int determining the height of the image
     */
    public Mandelbrot(int w, int h) {
        this.width = w;
        this.height = h;
        pixels = new int[w*h];
        dx = (xmax-xmin)/(width-1);
        dy = (ymax-ymin)/(height-1);
        palette = new int[256];
        for (int i = 0; i < 256; i++)
            palette[i] = Color.getHSBColor(i/255F, 1, 1).getRGB();
    }

    /**
     * Method to adjust the region in the complex plane to be used for calculations, region is scaled to
     * fit image dimension
     * @param xmin minimum real value
     * @param xmax maximum real value
     * @param ymin minimum imaginary value
     * @param ymax maximum imagonary value
     */
    public void setBound(double xmin,double xmax,double ymin,double ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        dx = (xmax-xmin)/(width-1);
        dy = (ymax-ymin)/(height-1);
    }

    /**
     * Method to generate the mandelbrot image. Loops through all points (pixels) and calculates the color, based
     * on the number of iterations needed to decide weather the iterated function converges or not.
     */
    public void generateMandelbrot() {
        double x, y;
        for (int row = 0; row < height; row++) {
            y = ymax - dy*row;
            for (int col = 0; col < width; col++) {
                x = xmin + dx*col;
                pixels[row*width+col]=mandelbrot(new Komplex(x,y));
            }
        }
    }

    /**
     * Getter for the image data
     * @return an array of ints representing the mandelbrot image
     */
    public int[] getMandelbrotImage() {
        return pixels;
    }

    /**
     * Method to calculate the number of iterations needed to decide weather the function converges at x+iy
     * @param z the complex number
     * @return the number of iterations needed to analyse the point x+iy
     */
    private int mandelbrot(Komplex z) {
        int count = 0;
        Komplex zn = new Komplex(0,0);
        while (count < maxIter && (Math.sqrt(zn.r*zn.r+zn.im*zn.im)) < 2) {
            // Do calculations here!
            double spara = zn.im;

            //Byt ut spara * spara mot Komplex multiply
            zn.im = 2*zn.r*zn.im + z.im;
            zn.r = zn.r * zn.r - spara * spara + z.r;

            /*
            zn.im = 2*zn.r*zn.im + z.im;
            zn.r *= 2;
            zn.r -= zn.im *= 2;
            zn.r += z.r;
            */

            /*
            zn.r *= 2;
            zn.im += z.im;
             */
            count++;
        }

        if (count == maxIter)
            return 0;
        else
            return palette[count%palette.length];
    }

    public synchronized void start() {
        running = true;
        t = new Thread(this);
        t.start();

    }
    @Override
    public void run() {
        calculating = true;
        while (calculating) {
            generateMandelbrot();
/*            double xminTarget = -1.6744096740931858;
            double xmaxTarget = -1.674409674093473;
            double yminTarget = 4.716540768697223E-5;
            double ymaxTarget = 4.716540790246652E-5;
            setBound(xmin-(xmin-xminTarget)*0.1,xmax-(xmax-xmaxTarget)*0.1,ymin-(ymin-yminTarget)*0.1,
                    ymax-(ymax-ymaxTarget)*0.1);*/
        }
    }
}