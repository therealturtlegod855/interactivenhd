import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

// 1. THE MAIN APPLET CONTAINER
public class MinesweeperApplet extends Applet {
    private MinesweeperFrame gameWindow;
    private TextField textField1;
    private TextField textField2;

    @Override
    public void init() {
        // Set up the applet layout with two rows for the editable text lines
        setLayout(new GridLayout(2, 1, 5, 5));

        // Initialize two editable text fields
        textField1 = new TextField("Editable Line 1: Type something here...", 30);
        textField2 = new TextField("Editable Line 2: Customize this text...", 30);

        // Add the fields to the embedded web applet area
        add(textField1);
        add(textField2);
    }

    @Override
    public void start() {
        // Automatically launch the independent game window when the applet starts
        if (gameWindow == null) {
            gameWindow = new MinesweeperFrame("Minesweeper Game Window");
        } else {
            gameWindow.setVisible(true);
            gameWindow.toFront();
        }
    }

    @Override
    public void stop() {
        // Hide the game window if the user leaves the web page
        if (gameWindow != null) {
            gameWindow.setVisible(false);
        }
    }

    @Override
    public void destroy() {
        // Clean up and close the game window when the applet cycle ends
        if (gameWindow != null) {
            gameWindow.dispose();
        }
    }
}

// 2. THE SEPARATE POP-UP WINDOW HOUSING THE GAME
class MinesweeperFrame extends Frame {
    private final int GRID_SIZE = 10;
    private final int NUM_MINES = 10;
    
    private Button[][] buttons = new Button[GRID_SIZE][GRID_SIZE];
    private boolean[][] mines = new boolean[GRID_SIZE][GRID_SIZE];
    private boolean[][] revealed = new boolean[GRID_SIZE][GRID_SIZE];
    private boolean[][] flagged = new boolean[GRID_SIZE][GRID_SIZE];
    private boolean gameOver = false;

    public MinesweeperFrame(String title) {
        super(title);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        initializeGrid();
        placeMines();

        // Window behavior configurations
        setSize(450, 450);
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);

        // Allow users to manually close the window via the title bar 'X'
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void initializeGrid() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                Button btn = new Button();
                btn.setFont(new Font("Arial", Font.BOLD, 14));
                
                final int row = r;
                final int col = c;
                
                // Track both normal clicks and right-clicks for flagging
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return;
                        
                        // Right click to flag, Left click to reveal
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            toggleFlag(row, col);
                        } else if (e.getButton() == MouseEvent.BUTTON1) {
                            clickCell(row, col);
                        }
                    }
                });
                
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

    private void toggleFlag(int r, int c) {
        if (revealed[r][c]) return;
        
        flagged[r][c] = !flagged[r][c];
        if (flagged[r][c]) {
            buttons[r][c].setLabel("F");
            buttons[r][c].setForeground(Color.RED);
        } else {
            buttons[r][c].setLabel("");
        }
    }

    private void clickCell(int r, int c) {
        if (revealed[r][c] || flagged[r][c]) return;

        revealed[r][c] = true;

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
            // Auto-reveal surrounding clean spaces
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
        
        buttons[r][c].setEnabled(false);
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
