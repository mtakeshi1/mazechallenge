package mc.challenge.maze;

import com.badlogic.gdx.math.Vector2;
import mc.challenge.Challenge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static mc.challenge.maze.ArrayUtil.invertrows;
import static mc.challenge.maze.ArrayUtil.isInsideOuterWallsMatrix;
import static mc.challenge.maze.ArrayUtil.rotate;
import static mc.challenge.maze.Maze.CellType.FLR;
import static mc.challenge.maze.Maze.CellType.FSH;
import static mc.challenge.maze.Maze.CellType.SRT;
import static mc.challenge.maze.Maze.CellType.WLL;


public abstract class Maze {

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

    public abstract String getMazeType();

    public int getStepsTaken() {
        return stepsTaken;
    }

    final int totalRows;
    final int totalCols;

    private final Player player;
    private final CellType[][] matrix;
    private final boolean[][] explored;
    private final boolean[][] visited;
    private Position finish;
    private Position start;
    private int stepsTaken = 0;
    private boolean endReached = false;
    private final ExploredBounds exploredBounds = new ExploredBounds();

    private String entrant = null;

    private final long startTimeMS;

    private RunInfo finishedInfo;

    protected Maze(char[][] arr) {
        totalRows = arr.length;
        totalCols = arr[0].length;
        //        if (rows < 5) throw new IllegalArgumentException("rows must be >= 5");
//        if (cols < 5) throw new IllegalArgumentException("cols must be >= 5");
//        if (start == null) throw new IllegalArgumentException("start may not be null");
//        if (start.row() < 1) throw new IllegalArgumentException("start row must be > 0");
//        if (start.row() >= rows) throw new IllegalArgumentException("start row must be < rows");
//        if (start.col() < 1) throw new IllegalArgumentException("start col must be > 0");
//        if (start.col() >= rows) throw new IllegalArgumentException("start col must be < rows");
//
//        if (finish == null) throw new IllegalArgumentException("finish may not be null");
//        if (finish.row() < 1) throw new IllegalArgumentException("finish row must be > 0");
//        if (finish.row() >= rows) throw new IllegalArgumentException("finish row must be < rows");
//        if (finish.col() < 1) throw new IllegalArgumentException("finish col must be > 0");
//        if (finish.col() >= rows) throw new IllegalArgumentException("finish col must be < rows");
//        if (start.equals(finish)) throw new IllegalArgumentException("start must not be finish");


        arr = rotate(arr);
        arr = rotate(arr);
        arr = rotate(arr);
        invertrows(arr);
        player = new Player(new Position(1, 1));
        matrix = new CellType[arr.length][arr[0].length];
        explored = new boolean[arr.length][arr[0].length];
        visited = new boolean[arr.length][arr[0].length];
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[0].length; c++) {
                switch (arr[r][c]) {
                    case '.' -> matrix[r][c] = FLR;
                    case '#' -> matrix[r][c] = WLL;
                    case '<' -> {
                        matrix[r][c] = SRT;
                        player.setPosition(new Position(r, c));
                        start = player.getPosition();
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
        getLineOfSight();
        startTimeMS = System.currentTimeMillis();
    }

    /**
     * Driving method for the challenge. When you implement {@link Challenge#getMove()} The returned value ends up here.
     */
    public Optional<RunInfo> doMove(Direction direction) {
        if (direction == null) throw new IllegalArgumentException("The direction may not be null");
        if (finishedInfo != null) {
            return of(finishedInfo);
        }
        Position newPosition = player.getPosition().plus(direction.getTP().row(), direction.getTP().col());
        var type = matrix[newPosition.row()][newPosition.col()];

        if (type == WLL) {
            System.out.println("Warning: WALL HIT");
            return empty();
        }

        stepsTaken++;
        if (newPosition.equals(finish)) {
            endReached = true;
            if (entrant == null) throw new RuntimeException("entrant may not be null");
            finishedInfo = new RunInfo(LocalDateTime.now(), getMazeType(), totalRows, totalCols, stepsTaken, getNrExploredTiles(explored), getNrWalkableTiles(matrix), start, finish, System.currentTimeMillis() - startTimeMS, entrant);
            try {
                Files.writeString(Path.of("./data/runsv1"), finishedInfo + "\n", APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return of(finishedInfo);
        }

        player.setPosition(newPosition);
        visited[player.getPosition().row()][player.getPosition().col()] = true;
        return empty();
    }


    public RunInfo getFinishedInfo() {
        return finishedInfo;
    }

    public void setEntrant(String entrant) {
        this.entrant = entrant;
    }

    /**
     * Should be extended to support: angle selections, distances
     */
    public CellType[][] getLineOfSight() {
        var start = player.getPosition();

        var view = new CellType[13][13];

        for (var arr : view) {
            Arrays.fill(arr, CellType.UNK);
        }


        Vector2 v2 = new Vector2(0.5f, 0);
        for (int j = 0; j < 360; j++) {
            boolean blocked = false;
            for (int i = 0; i < 25; i++) {
                v2.setLength(v2.len() + 0.25f);
                int r = (int) v2.y;
                int c = (int) v2.x;
                int mr = r + start.row();
                int mc = c + start.col();
                if (!isInsideOuterWallsMatrix(mr, mc, matrix)) {
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
                }
            }
            v2.rotateDeg(1);
            v2.setLength(0.5f);

        }
        return view;
    }

    static int getNrWalkableTiles(CellType[][] mx) {
        int count = 0;
        for (var arr : mx) {
            for (var c : arr) {
                if (c != FLR) count++;
            }
        }
        return count;
    }

    static int getNrExploredTiles(boolean[][] mx) {
        int count = 0;
        for (var arr : mx) {
            for (var c : arr) {
                if (c) count++;
            }
        }
        return count;
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

    public boolean[][] exploredvisitedMx() {
        int en = exploredBounds.north;
        int es = exploredBounds.south;
        int ee = exploredBounds.east;
        int ew = exploredBounds.west;

        if (en == -1) {
            return new boolean[0][0];
        }


        int rws = 1 + en - es;
        int cls = 1 + ee - ew;
        boolean[][] mx = new boolean[rws][cls];


        int mr = 0;
        int mc;

        for (int r = es; r <= en; r++) {
            mc = 0;
            for (int c = ew; c <= ee; c++) {

                if (visited[r][c]) {
                    mx[mr][mc] = true;
                }
                mc++;
            }
            mr++;
        }

        return mx;
    }

    public CellType[][] visitedMatrix() {

        int en = exploredBounds.north;
        int es = exploredBounds.south;
        int ee = exploredBounds.east;
        int ew = exploredBounds.west;

        if (en == -1) {
            return new CellType[0][0];
        }


        int rws = 1 + en - es;
        int cls = 1 + ee - ew;
        CellType[][] mx = new CellType[rws][cls];


        int mr = 0;
        int mc;

        for (int r = es; r <= en; r++) {
            mc = 0;
            for (int c = ew; c <= ee; c++) {

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
    public Position getPlayerRelativePosition() {
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
        drawPlayer.accept(getPlayerRelativePosition());
    }

    public void drawVisited(Consumer<boolean[][]> drawVis){
        drawVis.accept(exploredvisitedMx());
    }


    public boolean setTile(int r, int c, CellType type) {
        return setTile(new Position(r, c), type);
    }

    public boolean setTile(Position position, CellType type) {
        if (position.row() < 0 || position.row() >= matrix.length) return false;
        if (position.col() < 0 || position.col() >= matrix[0].length) return false;
        if (matrix[position.row()][position.col()] == FLR) {
            matrix[position.row()][position.col()] = type;
            return true;
        }
        return false;
    }

    public CellType getTile(int r, int c) {
        return matrix[r][c];
    }

}
