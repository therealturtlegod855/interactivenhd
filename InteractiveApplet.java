import java.applet.Applet;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InteractiveApplet extends Applet {
    private Frame popUpWindow;

    @Override
    public void init() {
        // Set up the layout for the embedded applet area
        setLayout(new FlowLayout());
        
        // Create an interactive button inside the web page bounds
        Button launchButton = new Button("Click to Launch Window");
        add(launchButton);

        // Define what happens when the button is clicked
        launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if the window is already open; if not, create it
                if (popUpWindow == null) {
                    launchIndependentWindow();
                } else {
                    popUpWindow.setVisible(true);
                    popUpWindow.toFront();
                }
            }
        });
    }

    private void launchIndependentWindow() {
        popUpWindow = new Frame("Interactive Launched Window");
        popUpWindow.setSize(350, 150);
        popUpWindow.setLayout(new FlowLayout());
        popUpWindow.add(new Label("This window was opened interactively via the JAR Applet!"));
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
    public void destroy() {
        // Ensure child windows are closed when the applet terminates
        if (popUpWindow != null) {
            popUpWindow.dispose();
        }
    }
}
