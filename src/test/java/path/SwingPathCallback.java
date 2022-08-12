package path;

import mc.challenge.maze.Maze;
import mc.renamebeforepr.AbsolutePosition;
import mc.renamebeforepr.ChallengeImpl;
import mc.renamebeforepr.FringeEntry;
import mc.renamebeforepr.PathFindingCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

public class SwingPathCallback implements PathFindingCallback {

    private static volatile SwingPathCallback sharedInstance;

    private JFrame mainWindow;

    private volatile Map<AbsolutePosition, Maze.CellType> map = new HashMap<>();

    private volatile boolean autoAnimation, step;
    private volatile AbsolutePosition selectedCell = new AbsolutePosition(0, 0);
    private volatile Set<AbsolutePosition> fringe = new HashSet<>();
    private volatile Collection<? extends AbsolutePosition> visited = new HashSet<>();
    private AbsolutePosition from = new AbsolutePosition(0, 0);
    private AbsolutePosition to = new AbsolutePosition(0, 0);

    public static synchronized SwingPathCallback getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new SwingPathCallback();
        }
        return sharedInstance;
    }

    private static synchronized void windowClosed(SwingPathCallback cb) {
        if (cb == sharedInstance) {
            sharedInstance = null;
        }
    }

    public SwingPathCallback() {
        mainWindow = new JFrame();
        mainWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainWindow.setSize(800, 600);
        mainWindow.getContentPane().setLayout(new BorderLayout());
        mainWindow.setVisible(true);
        mainWindow.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                SwingPathCallback.windowClosed(SwingPathCallback.this);
                mainWindow.dispose();
            }
        });
        JPanel center = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                AbsolutePosition[] minMax = ChallengeImpl.discoverMinMax(map);
                if (minMax[0] == null) {
                    return;
                }
                var min = minMax[0];
                var max = minMax[1];
                int rows = max.row() - min.row() + 1;
                int cols = max.col() - min.col() + 1;
                int celHeight = this.getHeight() / rows;
                int cellWidth = this.getWidth() / cols;
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        AbsolutePosition cell = new AbsolutePosition(row + min.row(), col + min.col());
                        Color color;
                        if (selectedCell.equals(cell)) {
                            color = Color.CYAN;
                        } else if (fringe.contains(cell)) {
                            color = Color.YELLOW;
                        } else if (visited.contains(cell)) {
                            color = Color.WHITE;
                        } else if(from.equals(cell)) {
                            color = Color.RED;
                        } else if(to.equals(cell)) {
                            color = Color.GREEN;
                        } else {
                            Maze.CellType type = map.getOrDefault(cell, Maze.CellType.UNK);
                            color = switch (type) {
//                                case SRT -> Color.RED;
                                case WLL -> Color.ORANGE;
//                                case FSH -> Color.GREEN;
                                case FLR, SRT, FSH -> Color.LIGHT_GRAY;
                                default -> Color.BLACK;
                            };
                        }
                        g.setColor(color);
                        g.fillRect(col * cellWidth, row * celHeight, cellWidth, celHeight);
                    }
                }
            }

        };
        mainWindow.getContentPane().add(BorderLayout.CENTER, center);
        JPanel south = new JPanel();
        mainWindow.getContentPane().add(BorderLayout.SOUTH, south);
        JCheckBox comp = new JCheckBox();
        comp.setText("Auto");
        comp.addItemListener(e -> autoAnimation = comp.isSelected());
        south.add(comp);
        JButton button = new JButton();
        south.add(button);
        button.setText("GO");
        button.addActionListener(e -> step = true);
    }

    public static void main(String[] args) {
        getInstance();
    }

    @Override
    public void newState(AbsolutePosition nextCell, Collection<? extends FringeEntry> fringe, Collection<? extends AbsolutePosition> visited, Map<AbsolutePosition, Maze.CellType> maze, AbsolutePosition from, AbsolutePosition to) {
        this.selectedCell = nextCell;
        this.fringe = new HashSet<>();
        fringe.stream().map(FringeEntry::destination).forEach(this.fringe::add);
        this.visited = visited;
        this.map = maze;
        this.from = from;
        this.to = to;
        SwingUtilities.invokeLater(() -> mainWindow.repaint());
        try {
            if (autoAnimation) {
                Thread.sleep(300);
            } else while (!step && !autoAnimation) {
                Thread.sleep(100);
            }
            step = false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
