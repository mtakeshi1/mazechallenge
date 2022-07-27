package mc.challenge.maze;

import com.badlogic.gdx.math.Vector2;
import mc.challenge.Challenge;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

import static mc.challenge.maze.ArrayUtil.invertrows;
import static mc.challenge.maze.ArrayUtil.isInsideMatrix;
import static mc.challenge.maze.ArrayUtil.rotate;
import static mc.challenge.maze.Maze.CellType.FLR;
import static mc.challenge.maze.Maze.CellType.FSH;
import static mc.challenge.maze.Maze.CellType.SRT;
import static mc.challenge.maze.Maze.CellType.UNK;
import static mc.challenge.maze.Maze.CellType.WLL;

/**
 * This class can create a maze but is also responsible for updating it.
 * ( needs to be refactored tbh, but you should not need or touch this class )
 */
public class Maze {

    /**
     * Mazes are made up of an array[][] of these types:
     */
    public enum CellType {
        WLL, // WALL
        FLR, // FLOOR
        SRT, // START
        FSH, // FINISH
        UNK // UNKNOWN
    }

    private static final Random rnd = new Random();

    public int getStepsTaken() {
        return stepsTaken;
    }

    private final Player player;
    private final CellType[][] matrix;
    private final boolean[][] explored;
    private Position finish;
    private int stepsTaken = 0;
    private boolean endReached = false;
    private final ExploredBounds exploredBounds = new ExploredBounds();

    public Maze(char[][] arr) {
        arr = rotate(arr);
        arr = rotate(arr);
        arr = rotate(arr);
        invertrows(arr);
        player = new Player(new Position(1, 1));
        matrix = new CellType[arr.length][arr[0].length];
        explored = new boolean[arr.length][arr[0].length];
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[0].length; c++) {
                switch (arr[r][c]) {
                    case '.' -> matrix[r][c] = FLR;
                    case '#' -> matrix[r][c] = WLL;
                    case '<' -> {
                        matrix[r][c] = SRT;
                        player.setPosition(new Position(r, c));
                    }
                    case '>' -> {
                        finish = new Position(r, c);
                        matrix[r][c] = FSH;
                    }
                    default -> throw new IllegalArgumentException(arr[r][c] + " is not supported input.");
                }
            }
        }
        if (player.getPosition() == null) throw new IllegalArgumentException("Must provide START '<'");
        if (finish == null) throw new IllegalArgumentException("Must provide FINISH '>'");
        System.out.println("Startlocation : " + player.getPosition());
        System.out.println("finishlocation : " + finish);
        getLineOfSight();
    }

    /**
     * @deprecated working on builder/factory
     */
    @Deprecated(forRemoval = true)
    public Maze(int rows, int cols) {
        this(
                rows,
                cols,
                new Position(rnd.nextInt((rows / 2) - 2) + 1, rnd.nextInt((cols / 2) - 2) + 1),
                new Position(rnd.nextInt((rows / 2) - 2) + rows / 2, rnd.nextInt((cols / 2) - 2) + cols / 2)
        );
    }

    /**
     * @deprecated Clean this up and delegate to mazebuilder/factory
     */
    @Deprecated(forRemoval = true)
    public Maze(int rows, int cols, Position start, Position finish) {
        if (rows < 5) throw new IllegalArgumentException("rows must be >= 5");
        if (cols < 5) throw new IllegalArgumentException("cols must be >= 5");
        if (start == null) throw new IllegalArgumentException("start may not be null");
        if (start.row() < 1) throw new IllegalArgumentException("start row must be > 0");
        if (start.row() >= rows) throw new IllegalArgumentException("start row must be < rows");
        if (start.col() < 1) throw new IllegalArgumentException("start col must be > 0");
        if (start.col() >= rows) throw new IllegalArgumentException("start col must be < rows");

        if (finish == null) throw new IllegalArgumentException("finish may not be null");
        if (finish.row() < 1) throw new IllegalArgumentException("finish row must be > 0");
        if (finish.row() >= rows) throw new IllegalArgumentException("finish row must be < rows");
        if (finish.col() < 1) throw new IllegalArgumentException("finish col must be > 0");
        if (finish.col() >= rows) throw new IllegalArgumentException("finish col must be < rows");
        if (start.equals(finish)) throw new IllegalArgumentException("start must not be finish");

        this.finish = finish;

        matrix = new CellType[rows][cols];
        explored = new boolean[rows][cols];

        fillMatrixWithFloor();
        wallEdges();
        matrix[start.row()][start.col()] = CellType.SRT;
        matrix[finish.row()][finish.col()] = CellType.FSH;


        player = new Player(start);
        getLineOfSight();
    }

    /**
     * Driving method for the challenge. When you implement {@link Challenge#getMove()} The returned value ends up here.
     */
    public void doMove(Direction direction) {
        if (direction == null) throw new IllegalArgumentException("The direction may not be null");
        if (endReached) {
            return;
        }
        Position newPosition = player.getPosition().plus(direction.getTP().row(), direction.getTP().col());
        var type = matrix[newPosition.row()][newPosition.col()];

        if (type == WLL) {
            return;
        }

        stepsTaken++;
        if (newPosition.equals(finish)) {
            System.out.println("FINISH ! Steps taken : " + stepsTaken);
            endReached = true;
        }

        player.setPosition(newPosition);
    }

    /**
     * Should be extended to support: angle selections, distances
     */
    public CellType[][] getLineOfSight() {

        var view = new CellType[13][13];

        for (var arr : view) {
            Arrays.fill(arr, CellType.UNK);
        }

        var start = player.getPosition();

        Vector2 v2 = new Vector2(0.5f, 0);
        for (int j = 0; j < 360; j++) {
            boolean blocked = false;
            for (int i = 0; i < 25; i++) {
                v2.setLength(v2.len() + 0.25f);
                int r = (int) v2.y;
                int c = (int) v2.x;
                int mr = r + start.row();
                int mc = c + start.col();
                if (!isInsideMatrix(mr, mc, matrix)) {
                    continue;
                }

                r += 6;
                c += 6;

                CellType tile = matrix[mr][mc];
                if (!blocked) {
                    view[r][c] = tile;
                    if (tile == WLL) {
                        blocked = true;
                    } else {
                        view[r][c] = matrix[mr][mc];

                    }
                    explore(new Position(mr, mc));
                } else if (view[r][c] == UNK) {
                    view[r][c] = WLL;
                }
            }
            v2.rotateDeg(1);
            v2.setLength(0.5f);

        }
        invertrows(view);
        return view;
    }


    /**
     * @deprecated Does not belong here.
     */
    @Deprecated(forRemoval = true)
    private void fillMatrixWithFloor() {
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                matrix[r][c] = FLR;
            }
        }
    }

    /**
     * @deprecated Does not belong here.
     */
    @Deprecated(forRemoval = true)
    private void wallEdges() {
        for (int r = 0; r < matrix.length; r++) {
            matrix[r][0] = WLL;
            matrix[r][matrix[0].length - 1] = WLL;
        }
        for (int c = 0; c < matrix[0].length; c++) {
            matrix[0][c] = WLL;
            matrix[matrix.length - 1][c] = WLL;
        }
    }

    static class ExploredBounds {
        private int north = -1;
        private int south = -1;
        private int east = -1;
        private int west = -1;


        void process(Position pos) {
            if (north == -1) {
                north = pos.row();
                south = pos.row();
                east = pos.col();
                west = pos.col();
            } else {
                north = Math.max(north, pos.row());
                south = Math.min(south, pos.row());
                east = Math.max(east, pos.col());
                west = Math.min(west, pos.col());
            }
        }
    }

    private void explore(Position pos) {
        exploredBounds.process(pos);
        explored[pos.row()][pos.col()] = true;
    }

    public CellType[][] visitedMatrix() {

        if (exploredBounds.north == -1) {
            return new CellType[0][0];
        }

        int rows = 1 + exploredBounds.north - exploredBounds.south;
        int cols = 1 + exploredBounds.east - exploredBounds.west;
        CellType[][] mx = new CellType[rows][cols];


        int mr = 0;
        int mc;
        for (int r = exploredBounds.south; r <= exploredBounds.north; r++) {
            mc = 0;
            for (int c = exploredBounds.west; c <= exploredBounds.east; c++) {
                if (explored[r][c]) {
                    mx[mr][mc] = matrix[r][c];
                } else {
                    mx[mr][mc] = CellType.UNK;
                }
                mc++;
            }
            mr++;
        }

        return mx;
    }


    /**
     * gets player position relative to the explored map.
     */
    public Position getPlayerPosition() {
        return new Position(
                player.getPosition().row() - exploredBounds.south,
                player.getPosition().col() - exploredBounds.west
        );
    }


    public boolean isEndReached() {
        return endReached;
    }


    public void drawMaze(Consumer<CellType[][]> drawMaze) {
        drawMaze.accept(visitedMatrix());
    }

    public void drawPlayer(Consumer<Position> drawPlayer) {
        drawPlayer.accept(getPlayerPosition());
    }

    boolean setTile(int r, int c, CellType type) {
        return setTile(new Position(r, c), type);
    }

    boolean setTile(Position position, CellType type) {
        if (matrix[position.row()][position.col()] == FLR) {
            matrix[position.row()][position.col()] = type;
            return true;
        }
        return false;
    }

    CellType getTile(int r, int c) {
        return matrix[r][c];
    }

}
