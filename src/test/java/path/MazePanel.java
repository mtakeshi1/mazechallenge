package path;

import mc.renamebeforepr.AbsolutePosition;
import mc.renamebeforepr.ChallengeImpl;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.function.Function;

public class MazePanel extends JPanel {

    private volatile Function<AbsolutePosition, Color> colors;
    private volatile Collection<? extends AbsolutePosition> positions;

    public MazePanel(Collection<? extends AbsolutePosition> positions, Function<AbsolutePosition, Color> color) {
        this.positions = positions;
        this.colors = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        AbsolutePosition[] minMax = ChallengeImpl.discoverMinMax(this.positions);
        if (minMax[0] == null) {
            return;
        }
        var min = minMax[0];
        var max = minMax[1];
        int rows = max.row() - min.row() + 1;
        int cols = max.col() - min.col() + 1;
        int celHeight = this.getHeight() / (rows+1);
        int cellWidth = this.getWidth() / (cols+1);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int rrow = row + min.row();
                AbsolutePosition cell = new AbsolutePosition(max.row() - (row + min.row()) - 1, col + min.col());
                Color color = colors.apply(cell);
                if (color == null) {
                    color = Color.BLACK;
                }
                g.setColor(color);
                g.fillRect(col * cellWidth, row * celHeight, cellWidth, celHeight);
            }
        }
    }

    public void updateCells(Collection<? extends AbsolutePosition> positions, Function<AbsolutePosition, Color> color) {
        this.positions = positions;
        this.colors = color;
        SwingUtilities.invokeLater(this::repaint);
    }

}
