package afk;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class App {

    private final ScheduledExecutorService ses;
    private ScheduledFuture<?> scheduledFuture;

    public static void main(String[] args) throws AWTException {
        JFrame frame = new Window();
        frame.setVisible(true);

        URL iconUrl = frame.getClass().getResource("/afk.png");
        Image icon = new ImageIcon(iconUrl).getImage();
        frame.setIconImage(icon);

        if (SystemTray.isSupported()) {
            System.out.println("suported");
            frame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        }

        URL trayIconUrl = frame.getClass().getResource("/afk16.png");
        Image trayIcon = new ImageIcon(trayIconUrl).getImage();
        
        SystemTray systemTray = SystemTray.getSystemTray();
        TrayIcon traylcon = new TrayIcon(trayIcon);

        MenuItem show = new MenuItem("Show");
        show.addActionListener((ActionEvent e) -> {
            frame.setVisible(true);
        });

        MenuItem close = new MenuItem("Exit");
        close.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        PopupMenu trayMenu = new PopupMenu();
        trayMenu.add(show);
        trayMenu.add(close);

        traylcon.setPopupMenu(trayMenu);
        systemTray.add(traylcon);
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
                robot.mouseMove(x, y);
            } catch (AWTException ex) {
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
