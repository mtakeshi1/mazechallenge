package mc.participants.deboder1;

import mc.Configuration;
import mc.challenge.Challenge;
import mc.challenge.maze.ArrayUtil;
import mc.challenge.maze.Direction;
import mc.challenge.maze.HeadlessMain;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.MazeFactory;
import mc.challenge.maze.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static mc.challenge.maze.Direction.EAST;
import static mc.challenge.maze.Direction.NORTH;
import static mc.challenge.maze.Direction.SOUTH;
import static mc.challenge.maze.Direction.WEST;
import static mc.challenge.maze.Maze.CellType.FSH;
import static mc.challenge.maze.Maze.CellType.UNK;
import static mc.challenge.maze.Maze.CellType.WLL;

public class DeboChallenge1 implements Challenge {


    private Position playerPos = new Position(1050, 1050);
    private final int poffset = 6;

    boolean finishFound = false;

    private CellType[][] mx = new CellType[3000][3000];

    {
        for (var arr : mx) Arrays.fill(arr, UNK);
    }


    @Override
    public void handleLineOfSightUpdate(CellType[][] los) {
        for (int r = 0; r < los.length; r++) {
            for (int c = 0; c < los[0].length; c++) {
                var tmp = los[r][c];
                if (tmp == FSH) {
                    finishFound = true;
                }
                int cellR = playerPos.row() + r - poffset;
                int cellC = playerPos.col() + c - poffset;
                if (mx[cellR][cellC] == UNK) {
                    mx[cellR][cellC] = tmp;


                }
            }
        }
    }

    @Override
    public Direction getMove() {


        var firstFound = findFirst();
        var list = new ArrayList<Position>();
        while (firstFound != null) {
            list.add(firstFound.position);
            firstFound = firstFound.parent;
        }
        Collections.reverse(list);

        var first = list.get(0);
        var second = list.get(1);
        if (first.row() < second.row()) {
            playerPos = playerPos.plus(NORTH.getTP());
            return NORTH;
        }
        if (first.row() > second.row()) {
            playerPos = playerPos.plus(SOUTH.getTP());
            return SOUTH;
        }
        if (first.col() > second.col()) {
            playerPos = playerPos.plus(WEST.getTP());
            return WEST;
        }
        if (first.col() < second.col()) {
            playerPos = playerPos.plus(EAST.getTP());
            return EAST;
        }

        throw new RuntimeException("can not");
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