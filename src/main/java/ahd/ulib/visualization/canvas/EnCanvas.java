package ahd.ulib.visualization.canvas;

import java.awt.Canvas;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.Serial;

@Deprecated
public class EnCanvas extends Canvas implements Runnable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Thread thread;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static boolean running = false;

    public EnCanvas() {
        Dimension size = new Dimension(WIDTH, HEIGHT);
        this.setPreferredSize(size);

    }

    public synchronized void start() {
        running = true;
        this.thread = new Thread(this, "Display");
        this.thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1000000000.0 / 60;
        double delta = 0;
        int frames = 0;


        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            boolean flag = delta >= 1;
            while (delta >= 1) {
                delta--;
            }

            if (flag) {
                render();
                frames++;
            }

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(frames);
                frames = 0;
            }
        }

        stop();
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH * 2, HEIGHT * 2);


        g.dispose();
        bs.show();
    }

}
