package mc.participants.deboder2;

import mc.Configuration;
import mc.challenge.Challenge;
import mc.challenge.maze.ArrayUtil;
import mc.challenge.maze.Direction;
import mc.challenge.maze.HeadlessMain;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.MazeFactory;
import mc.challenge.maze.Position;
import mc.participants.deboder1.DeboChallenge1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static mc.challenge.maze.Direction.EAST;
import static mc.challenge.maze.Direction.NORTH;
import static mc.challenge.maze.Direction.SOUTH;
import static mc.challenge.maze.Direction.WEST;
import static mc.challenge.maze.Maze.CellType.FLR;
import static mc.challenge.maze.Maze.CellType.FSH;
import static mc.challenge.maze.Maze.CellType.SRT;
import static mc.challenge.maze.Maze.CellType.UNK;
import static mc.challenge.maze.Maze.CellType.WLL;

public class DeboChallenge2 implements Challenge {


    private static int SIZE = 500;

    private Position playerPos = new Position(SIZE / 2, SIZE / 2);
    private final int poffset = 6;

    boolean finishFound = false;

    private CellType[][] mx;

    private char[][] w1Mx;


    enum MapType {
        EMPTY, SCATTER, W1, DUNGEON, FLOWINGCAVE
    }


    Consumer<CellType[][]> handleLOSStrategy = (mx) -> {
        setSelectStrategy(mx);
    };

    Supplier<Direction> directionStrategy = () -> {
        throw new RuntimeException("Did not select direction Strategy");
    };

    private Position lastPos = playerPos;
    Supplier<Direction> w1DirStrategy = () -> {
        var list = getOpenSurrounds(playerPos.row(), playerPos.col(), w1Mx);

        System.out.println("--w1DirStrategy");
        var ppostemp = playerPos;
        Direction dir = null;

        if (list.size() == 1) {
            setArrPos(playerPos, w1Mx, '#');
            dir = fromTo(playerPos, list.get(0));
            playerPos = list.get(0);
        } else if (list.size() == 2) {
            var p1 = list.get(0);
            var p2 = list.get(1);
            if (p2.equals(lastPos)) {
                dir = fromTo(playerPos, p1);
                playerPos = p1;

            } else {
                dir = fromTo(playerPos, p2);
                playerPos = p2;

            }
        } else if (list.size() == 3) {
            var p1 = list.get(0);
            var p2 = list.get(1);
            var p3 = list.get(2);
            setArrPos(lastPos, w1Mx, 'V');
            if (p1.equals(lastPos)) {

                if (visited(p2, w1Mx)) {
                    dir = fromTo(playerPos, p3);
                    playerPos = p3;
                } else {
                    dir = fromTo(playerPos, p2);
                    playerPos = p2;
                }
            } else if (p2.equals(lastPos)) {
                if (visited(p1, w1Mx)) {
                    dir = fromTo(playerPos, p3);
                    playerPos = p3;
                } else {
                    dir = fromTo(playerPos, p1);
                    playerPos = p1;
                }
            } else {
                if (visited(p2, w1Mx)) {
                    dir = fromTo(playerPos, p1);
                    playerPos = p1;
                } else {
                    dir = fromTo(playerPos, p2);
                    playerPos = p2;
                }

            }
            setArrPos(playerPos, w1Mx, 'V');
        } else if (list.size() == 4) {
            var getBest = getOpenSurroundsWithoutV(playerPos.row(), playerPos.col(), w1Mx);

            if (getBest.isEmpty()) {
                if(list.get(0).equals(lastPos)){
                    list.remove(0);
                }
                dir = fromTo(playerPos, list.get(0));
                playerPos = list.get(0);
                setArrPos(playerPos, w1Mx, 'V');
            } else {
                if(getBest.get(0).equals(lastPos)){
                    getBest.remove(0);
                }
                dir = fromTo(playerPos, getBest.get(0));
                playerPos = getBest.get(0);
                setArrPos(playerPos, w1Mx, 'V');
            }

        }
        lastPos = ppostemp;
        if (dir != null) {
            System.out.println(dir + " " + playerPos);
            return dir;
        }

        throw new RuntimeException("not implemented");
    };

    private boolean visited(Position p, char[][] arr) {
        return arr[p.row()][p.col()] == 'V';
    }

    private void setArrPos(Position p, char[][] mx, char target) {
        mx[p.row()][p.col()] = target;
    }

    public Direction fromTo(Position from, Position to) {
        if (from.row() < to.row()) return NORTH;
        if (from.row() > to.row()) return SOUTH;
        if (from.col() > to.col()) return WEST;
        if (from.col() < to.col()) return EAST;
        throw new RuntimeException("can not");
    }


    private void setSelectStrategy(CellType[][] mx) {
        if (isWidth1(mx)) {
            handleLOSStrategy = width1Strategy;

            w1Mx = new char[SIZE][SIZE];

            for (var arr : w1Mx) Arrays.fill(arr, '?');

            handleLOSStrategy.accept(mx);

            directionStrategy = w1DirStrategy;

        } else throw new RuntimeException("implement other strategies.");

    }

    Consumer<CellType[][]> width1Strategy = los -> {
        for (int r = 0; r < los.length; r++) {
            for (int c = 0; c < los[0].length; c++) {
                var losval = lookup[los[r][c].ordinal()];
                if (losval == 'F') {
                    finishFound = true;
                }
                int cellR = playerPos.row() + r - poffset;
                int cellC = playerPos.col() + c - poffset;
                if (w1Mx[cellR][cellC] == '?') {
                    w1Mx[cellR][cellC] = losval;
                }
                has3walls(cellR, cellC, w1Mx);
            }
        }

        System.out.println();

        int low = Integer.MAX_VALUE;

        for (int x = w1Mx.length - 1; x >= 0; x--) {
            if (contains(w1Mx[x], '#')) {

                for (int c = playerPos.col() - 20; c < playerPos.col() + 60; c++) {
                    System.out.print(w1Mx[x][c]);
                }
                System.out.println();
            }

        }
        System.out.println();
    };

    private boolean contains(char[] arr, char c) {
        for (var ch : arr) {
            if (ch != '?') return true;
        }
        return false;
    }

    private void has3walls(int cellR, int cellC, char[][] mx) {
        var tmp = mx[cellR][cellC];
        if (!(tmp == '.' || tmp == 'S')) return;
        if (playerPos.row() == cellR && playerPos.col() == cellC) return;

        var list = getOpenSurrounds(cellR, cellC, mx);

        if (list.size() == 1) {
            mx[cellR][cellC] = '#';
            var p = list.get(0);
            has3walls(p.row(), p.col(), mx);
        }

    }

    private List<Position> getOpenSurrounds(int r, int c, char[][] mx) {
        List<Position> list = new ArrayList<>(4);
        for (var dir : Direction.values()) {
            var tmp = dir.getTP().plus(r, c);
            if (mx[tmp.row()][tmp.col()] != '#') {
                list.add(tmp);
            }
        }
        return list;
    }

    private List<Position> getOpenSurroundsWithoutV(int r, int c, char[][] mx) {
        List<Position> list = new ArrayList<>(4);
        for (var dir : Direction.values()) {
            var tmp = dir.getTP().plus(r, c);
            if (mx[tmp.row()][tmp.col()] != '#' && mx[tmp.row()][tmp.col()] != 'V') {
                list.add(tmp);
            }
        }
        return list;
    }

    private static char[] lookup = new char[10];

    static {
        lookup[WLL.ordinal()] = '#';
        lookup[FLR.ordinal()] = '.';
        lookup[SRT.ordinal()] = 'S';
        lookup[FSH.ordinal()] = 'F';
        lookup[UNK.ordinal()] = '?';
    }


    private static boolean isWidth1(CellType[][] mx) {
        for (int r = 0; r < mx.length - 1; r++) {
            for (int c = 0; c < mx[0].length - 1; c++) {
                var tmp = isWalkable(mx[r][c]);
                if (mx[r][c] == UNK) continue;
                if (mx[r + 1][c + 1] == UNK) continue;
                if (mx[r + 1][c] == UNK) continue;
                if (mx[r][c + 1] == UNK) continue;
                int count = 0;
                if (tmp == isWalkable(mx[r + 1][c])) count++;
                if (tmp == isWalkable(mx[r][c + 1])) count++;
                if (tmp == isWalkable(mx[r + 1][c + 1])) count++;
                if (count == 3) {
                    return false;
                }
            }
        }
        System.out.println("Width1 maze");
        return true;
    }

    private static boolean isWalkable(CellType type) {
        return type != WLL;
    }

    @Override
    public void handleLineOfSightUpdate(CellType[][] los) {
        handleLOSStrategy.accept(los);
    }

    @Override
    public Direction getMove() {

        return directionStrategy.get();
//
//        var firstFound = findFirst();
//        var list = new ArrayList<Position>();
//        while (firstFound != null) {
//            list.add(firstFound.position);
//            firstFound = firstFound.parent;
//        }
//        Collections.reverse(list);
//
//        var first = list.get(0);
//        var second = list.get(1);
//        if (first.row() < second.row()) {
//            playerPos = playerPos.plus(NORTH.getTP());
//            return NORTH;
//        }
//        if (first.row() > second.row()) {
//            playerPos = playerPos.plus(SOUTH.getTP());
//            return SOUTH;
//        }
//        if (first.col() > second.col()) {
//            playerPos = playerPos.plus(WEST.getTP());
//            return WEST;
//        }
//        if (first.col() < second.col()) {
//            playerPos = playerPos.plus(EAST.getTP());
//            return EAST;
//        }
//
//        throw new RuntimeException("can not");
    }

    Node findFirst() {
        Node root = new Node(null, playerPos);
        Set<Position> searched = new HashSet<>();
        Set<Node> level = new HashSet<>();
        level.add(root);
        searched.add(playerPos);

        boolean found = false;
        while (!found) {
            Set<Node> newLevel = new HashSet<>();

            for (var n : level) {
                for (int[] dirs : ArrayUtil.FOUR_DIRECTIONS) {
                    var newpos = n.position.plus(dirs);
                    var newtype = get(newpos);
                    if (newtype != WLL && !searched.contains(newpos)) {
                        searched.add(newpos);

                        if (newtype == FSH || newtype == UNK) {
                            return new Node(n, newpos);
                        }
                        newLevel.add(new Node(n, newpos));

                    }
                }
            }
            level = newLevel;
        }
        throw new RuntimeException("can not find. ");
    }

    CellType get(Position p) {
        return mx[p.row()][p.col()];
    }

    static boolean hasUnkNeighbor(CellType[][] mx, Position position) {
        for (int[] dirs : ArrayUtil.FOUR_DIRECTIONS) {
            if (mx[position.row() + dirs[0]][position.col() + dirs[1]] == UNK) {
                return true;
            }
        }
        return false;
    }

    static class Node {
        Node(Node p, Position position) {
            parent = p;
            this.position = position;
        }

        Position position;
        Set<Node> children = new HashSet<>();

        Node parent;

    }


    //Just here to show explain how the Line Of Sight array is build.
    private static void printLOSUpdate(CellType[][] los) {
        // [13,13] Line of sight array printed with 'you' in the middle at: [6,6]
        // UNK = unknown
        // WLL = wall
        // FLR = floor
        // SRT = start
        // FSH = finish
        System.out.println();
        for (int x = los.length - 1; x >= 0; x--) {
            System.out.println(Arrays.toString(los[x]));
        }

        System.out.println();
    }


    public static void main(String[] args) {
        new HeadlessMain(new DeboChallenge1(),
//                MazeFactory.getFlowingCave(
//                MazeFactory.get1WMap(
                MazeFactory.getDungeon(
//                        Configuration.SMALL, Configuration.SMALL
//                        Configuration.MEDIUM, Configuration.MEDIUM
//                        Configuration.LARGE, Configuration.LARGE
                        Configuration.HUGE
                )
        ).doAllMoves();
    }

}