package afk;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class App {

    private final ScheduledExecutorService ses;
    private ScheduledFuture<?> scheduledFuture;
    
    public static void main(String[] args) {
        JFrame frame = new Window();
        frame.setVisible(true);
    }

    public App() {
        ses = Executors.newScheduledThreadPool(1);
    }

    public void start(Integer time) {
        
        Runnable move = () -> {
            Point mLocation = MouseInfo.getPointerInfo().getLocation();
            int x = (int) mLocation.getX();
            int y = (int) mLocation.getY();

            Robot robot;
            try {
                robot = new Robot();
                robot.mouseMove(x, y + 100);
                Thread.sleep(1000);
                robot.mouseMove(x, y);
            } catch (AWTException | InterruptedException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        };

        scheduledFuture = ses.scheduleAtFixedRate(move, 0, time, TimeUnit.MINUTES);
    }

    public void stop() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
    }
}
