import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

// 1. THE MAIN APPLET CONTAINER
public class MinesweeperApplet extends Applet {
    private MinesweeperFrame gameWindow;
    private TextField textField1;
    private TextField textField2;

    // Constant strings defined directly in the code
    private static final String LINE_1_TEXT = "Constant Status Line 1: Playing Minesweeper";
    private static final String LINE_2_TEXT = "Constant Status Line 2: Close window to reset game";

    @Override
    public void init() {
        // Set up the applet layout with two rows for the text lines
        setLayout(new GridLayout(2, 1, 5, 5));

        // Initialize text fields with the constant strings
        textField1 = new TextField(LINE_1_TEXT, 40);
        textField2 = new TextField(LINE_2_TEXT, 40);

        // LOCK FIELDS: Enforce non-editable restriction so users cannot modify them
        textField1.setEditable(false);
        textField2.setEditable(false);

        // Add fields to the applet viewport
        add(textField1);
        add(textField2);
    }

    @Override
    public void start() {
        launchOrResetGame();
    }

    // Public method so the window can tell the applet to restart it when closed
    public void launchOrResetGame() {
        if (gameWindow != null) {
            gameWindow.dispose();
        }
        // Pass a reference of 'this' applet to the window so it can trigger a reset on close
        gameWindow = new MinesweeperFrame("Minesweeper Game Window", this);
    }

    @Override
    public void stop() {
        if (gameWindow != null) {
            gameWindow.setVisible(false);
        }
    }

    @Override
    public void destroy() {
        if (gameWindow != null) {
            gameWindow.dispose();
        }
    }
}

// 2. THE SEPARATE POP-UP WINDOW HOUSING THE GAME
class MinesweeperFrame extends Frame implements ActionListener {
    private final int GRID_SIZE = 10;
    private final int NUM_MINES = 10;
    
    private Button[][] buttons = new Button[GRID_SIZE][GRID_SIZE];
    private boolean[][] mines = new boolean[GRID_SIZE][GRID_SIZE];
    private boolean[][] revealed = new boolean[GRID_SIZE][GRID_SIZE];
    private boolean gameOver = false;
    private MinesweeperApplet parentApplet;

    public MinesweeperFrame(String title, MinesweeperApplet applet) {
        super(title);
        this.parentApplet = applet;
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        initializeGrid();
        placeMines();

        setSize(450, 450);
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);

        // Reset and Reopen Strategy: Whenever the window is closed, tell the parent applet to reopen it
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                // Call back to the parent applet to instantly recreate and reset the game
                parentApplet.launchOrResetGame();
            }
        });
    }

    private void initializeGrid() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                Button btn = new Button("");
                btn.setFont(new Font("Arial", Font.BOLD, 14));
                
                // Set action commands to easily read the row/column inside the ActionListener
                btn.setActionCommand(r + "," + c);
                btn.addActionListener(this);
                
                buttons[r][c] = btn;
                add(btn);
            }
        }
    }

    private void placeMines() {
        Random rand = new Random();
        int minesPlaced = 0;
        while (minesPlaced < NUM_MINES) {
            int r = rand.nextInt(GRID_SIZE);
            int c = rand.nextInt(GRID_SIZE);
            if (!mines[r][c]) {
                mines[r][c] = true;
                minesPlaced++;
            }
        }
    }

    // Reliable cross-platform execution via ActionListener
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        // Parse coordinates from action command string "row,col"
        String[] coords = e.getActionCommand().split(",");
        int r = Integer.parseInt(coords[0]);
        int c = Integer.parseInt(coords[1]);

        clickCell(r, c);
    }

    private void clickCell(int r, int c) {
        if (revealed[r][c]) return;

        revealed[r][c] = true;
        buttons[r][c].setEnabled(false);

        if (mines[r][c]) {
            endGame(false);
            return;
        }

        int count = countSurroundingMines(r, c);
        
        if (count > 0) {
            buttons[r][c].setLabel(Integer.toString(count));
            styleNumber(buttons[r][c], count);
        } else {
            buttons[r][c].setBackground(Color.LIGHT_GRAY);
            // Cascade uncover
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr;
                    int nc = c + dc;
                    if (nr >= 0 && nr < GRID_SIZE && nc >= 0 && nc < GRID_SIZE) {
                        clickCell(nr, nc);
                    }
                }
            }
        }
        
        checkWinCondition();
    }

    private int countSurroundingMines(int r, int c) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = r + dr;
                int nc = c + dc;
                if (nr >= 0 && nr < GRID_SIZE && nc >= 0 && nc < GRID_SIZE && mines[nr][nc]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void styleNumber(Button btn, int count) {
        Color[] colors = {Color.BLUE, Color.GREEN.darker(), Color.RED, Color.MAGENTA};
        if (count - 1 < colors.length) {
            btn.setForeground(colors[count - 1]);
        }
    }

    private void checkWinCondition() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (!mines[r][c] && !revealed[r][c]) return;
            }
        }
        endGame(true);
    }

    private void endGame(boolean won) {
        gameOver = true;
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (mines[r][c]) {
                    buttons[r][c].setLabel("X");
                    buttons[r][c].setBackground(won ? Color.GREEN : Color.RED);
                }
                buttons[r][c].setEnabled(false);
            }
        }
    }
}
