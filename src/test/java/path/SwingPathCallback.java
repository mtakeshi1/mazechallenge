package path;

import mc.challenge.maze.Maze;
import mc.renamebeforepr.AbsolutePosition;
import mc.renamebeforepr.FringeEntry;
import mc.renamebeforepr.PathFindingCallback;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.function.Function;

public class SwingPathCallback implements PathFindingCallback {

    private static volatile SwingPathCallback sharedInstance;
    private final MazePanel center;
    private final Function<AbsolutePosition, Color> colorFunction;

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
        this.colorFunction = cell -> {
            if (selectedCell.equals(cell)) {
                return Color.CYAN;
            } else if (fringe.contains(cell)) {
                return Color.YELLOW;
            } else if (visited.contains(cell)) {
                return Color.WHITE;
            } else if (from.equals(cell)) {
                return Color.RED;
            } else if (to.equals(cell)) {
                return Color.GREEN;
            } else {
                Maze.CellType type = map.getOrDefault(cell, Maze.CellType.UNK);
                return switch (type) {
                    case WLL -> Color.ORANGE;
                    case FLR, SRT, FSH -> Color.LIGHT_GRAY;
                    default -> Color.BLACK;
                };
            }
        };
        this.center = new MazePanel(this.map.keySet(), colorFunction);
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
        this.center.updateCells(this.map.keySet(), this.colorFunction);
        SwingUtilities.invokeLater(() -> mainWindow.repaint());
        try {
            if (autoAnimation) {
                Thread.sleep(300);
            } else while (!step && !autoAnimation && mainWindow.isVisible()) {
                Thread.sleep(100);
            }
            step = false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isVisible() {
        return mainWindow.isVisible();
    }
}
