import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * The container for the graphics, based on a Swing Canvas and using an int-array to control pixels.
 */
public class MandelbrotGenerator extends Canvas implements Runnable{
    public static int WIDTH = 1800;
    public static int HEIGTH = WIDTH*9/16;
    int pixelSize = 8;
    private String title;
    private JFrame frame;
    private BufferedImage image = new BufferedImage(WIDTH/ pixelSize, HEIGTH/ pixelSize, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    private Mandelbrot m;
    private Thread thread;
    private boolean running = false;

    /**
     * Constructor to create a JFrame and the mandelbrotset
     * @param title is the title in the windowbar
     */
    public MandelbrotGenerator(String title) {
        m = new Mandelbrot(WIDTH/ pixelSize, HEIGTH/ pixelSize);
        setPreferredSize(new Dimension(WIDTH, HEIGTH));
        frame = new JFrame();
        frame.setResizable(false);
        this.title = title;
        frame.setTitle(title);
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Implementing the start method in the Runnable interface. Creates a thread of the canvas and starts it, then
     * starts the mandelbrotcalculation
     */
    public synchronized void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
        m.start();
    }

    /**
     * Implementation of the stop() method in the Runnable interface. Brings everything to a controlled stop.
     */
    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * The run method from the Runnable interface implemented. A series of updates ande renders are executed based on
     * a timer. The animation speed is set to 60 ups but the fps is running as fast as possible. To make sure nothing
     * is dropped multiple updates can be done if neede but there is no way of "frame skipping" if updates
     * are too time consuming.
     */
    @Override
    public void run() {
        double ns = 1000000000.0 / 60.0;
        double delta = 0;
        int frames = 0;
        int updates = 0;
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while(delta >= 1) {
                update();
                updates++;
                delta--;
            }

            render();
            frames++;

            if(System.currentTimeMillis() - timer >= 1000) {
                timer += 1000;
                frame.setTitle(this.title + "  |  " + updates + " ups, " + frames + " fps");
                frames = 0;
                updates = 0;
            }
        }
        stop();
    }

    /**
     * Update all screen data. No need to do this sonce mandelbrot is on a separate thread
     */
    private void update() {

    }

    /**
     * Renders the graphics to the screen. Using a buffer strategy and getting the image from the mandelbrotgenerator.
     * then all data is copied to the image which is finally drawn on the screen
     */
    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        int[] screenPixels = m.getMandelbrotImage();

        for (int i = 0 ; i < pixels.length ; i++) {
            pixels[i] = screenPixels[i];
        }
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH, HEIGTH, null);
        g.dispose();
        bs.show();
    }

    /**
     * A main to enable testing of the graphics
     * @param args not used at this stage
     */
    public static void main(String[] args) {
        MandelbrotGenerator mg = new MandelbrotGenerator("Mandelbrot set");
        mg.start();
    }
}