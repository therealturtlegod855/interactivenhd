import java.applet.Applet;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;

public class InteractiveApplet extends Applet {
    private Frame popUpWindow;

    @Override
    public void init() {
        // Set up a simple message in the embedded web page area
        setLayout(new FlowLayout());
        add(new Label("Applet initialized. Launching automatic popup window..."));
    }

    @Override
    public void start() {
        // start() executes automatically immediately after init() finishes
        if (popUpWindow == null) {
            launchIndependentWindow();
        }
    }

    private void launchIndependentWindow() {
        popUpWindow = new Frame("Automatic Launched Window");
        popUpWindow.setSize(350, 150);
        popUpWindow.setLayout(new FlowLayout());
        popUpWindow.add(new Label("This window opened automatically when the applet loaded!"));
        popUpWindow.setLocationRelativeTo(null); // Center on screen
        
        // Basic window close handling for legacy AWT windows
        popUpWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                popUpWindow.dispose();
                popUpWindow = null;
            }
        });

        popUpWindow.setVisible(true);
    }

    @Override
    public void stop() {
        // Minimizes or hides the window if the user minimizes the applet viewer
        if (popUpWindow != null) {
            popUpWindow.setVisible(false);
        }
    }

    @Override
    public void destroy() {
        // Ensure child windows are fully closed when the applet terminates
        if (popUpWindow != null) {
            popUpWindow.dispose();
        }
    }
}
