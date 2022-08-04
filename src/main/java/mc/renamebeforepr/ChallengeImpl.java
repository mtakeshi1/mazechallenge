package mc.renamebeforepr;

import mc.Configuration;
import mc.challenge.Challenge;
import mc.challenge.maze.Direction;
import mc.challenge.maze.HeadlessMain;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.MazeFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

import static java.util.Collections.max;

/**
 * The whole challenge can be completed by just adding to this file.
 * You can create other java files in the same package though.
 * <p>
 * Run this:
 * Graphically -> {@link mc.GraphicalLauncher} ( for debug purposes )
 * Non graphically ->  {@link mc.HeadlessLauncher}
 * <p>
 * Configure and choose what type of mazes are ran : {@link Configuration}
 */
public class ChallengeImpl implements Challenge {

    public static final LosPosition CENTER = new LosPosition(6, 6);
    private AbsolutePosition targetPosition = null;
//    private AbsolutePosition currentPosition = new AbsolutePosition(CENTER.row(), CENTER.col());

    private PositionOffset offset = new PositionOffset(0, 0);
    private Direction selectedDirection = Direction.NORTH;
    private Map<AbsolutePosition, CellType> knownCells = new HashMap<>();
    private Queue<AbsolutePosition> selectedPath = new ArrayDeque<>();

    private AbsolutePosition topLeft = new AbsolutePosition(0, 0);
    private AbsolutePosition bottomRight = new AbsolutePosition(12, 12);

    private static class Path {
        private final LosPosition destination;
        private final Path previousPath;
        private final int steps;

        public Path(LosPosition destination, Path previousPath, int steps) {
            this.destination = destination;
            this.previousPath = previousPath;
            this.steps = steps;
        }
    }

    /**
     * This method will be called on init and after each move.
     *
     * @param los [13,13] array where 'you' always are at [6,6] ( further explained in printLOSUpdate
     */
    @Override
    public void handleLineOfSightUpdate(CellType[][] los) {
        newGridSpotted(los);
        if (selectedPath.isEmpty()) {
            traceNextPath();
        }
        AbsolutePosition currentPosition = offset.plus(CENTER);
        AbsolutePosition remove = selectedPath.remove();
        this.selectedDirection = currentPosition.directionTo(remove);
        this.offset = offset.walk(this.selectedDirection);
    }

    private void traceNextPath() {
        //TODO
    }

    /**
     * This method will be called before each move.
     * Here you must supply the program with a 'Direction', an enum that can be: NORTH, SOUTH, EAST or WEST
     * <p>
     * 'You' will walk in that direction unless there is a wall there.
     */
    @Override
    public Direction getMove() {
        return selectedDirection;
    }

    private void newGridSpotted(CellType[][] los) {
        //optimize according to last moved direction, don't need to probe the entire array every time
        for (int row = 0; row < los.length; row++) {
            for (int col = 0; col < los[0].length; col++) {
                LosPosition relative = new LosPosition(row, col);
                CellType cellType = relative.cellAt(los);
                AbsolutePosition absolute = offset.plus(relative);
                if (cellType != CellType.UNK && !knownCells.containsKey(absolute)) {
                    knownCells.put(absolute, cellType);
                    if (cellType == CellType.FSH && targetPosition == null) {
                        // we just found the finish line, b-line straight to it
                        targetPosition = absolute;
                        this.selectedPath.clear();
                    }
                }
            }
        }
    }

    private void adjustPositions(Direction selectedDirection) {
        //TODO
    }

    public static Direction findPath(LosPosition from, LosPosition to, CellType[][] los) {
        SortedSet<FringeEntry> fringe = new TreeSet<>(Comparator.comparingInt(FringeEntry::cost).thenComparingInt(System::identityHashCode));
        fringe.add(new FringeEntry(null, from, 0));
        Set<LosPosition> visited = new HashSet<>();
        while (!fringe.isEmpty()) {
            FringeEntry first = fringe.first();
            fringe.remove(first);
            visited.add(first.destination());
            if (to.equals(first.destination())) {
                return findNextPath(from, first);
            }
            explore(first.destination(), first, los, fringe, visited, 0);
        }
        throw new RuntimeException("no path from " + from + " to: " + to);
//        return Direction.NORTH;
    }

    public static Direction findNextPath(LosPosition from, FringeEntry first) {
        if (first.getSource().equals(from)) {
            return calculateDirection(from, first.destination());
        }
        return findNextPath(from, first.previousPath());
    }

    public static Direction calculateDirection(LosPosition from, LosPosition destination) {
        for (Direction direction : Direction.values()) {
            if (from.walk(direction).equals(destination)) {
                return direction;
            }
        }
        throw new RuntimeException("No direction to move from: " + from + " to " + destination);
    }

    public static void explore(LosPosition from, FringeEntry current, CellType[][] los, SortedSet<FringeEntry> fringe, Set<LosPosition> visited, int cost) {
        int newCost = cost + 1;
        for (Direction direction : Direction.values()) {
            var next = from.walk(direction);
            if (!visited.contains(next) && next.isWithin(los) && next.cellAt(los) != CellType.WLL && next.cellAt(los) != CellType.UNK) {
                FringeEntry entry = new FringeEntry(current, next, newCost);
                fringe.add(entry);
            }
        }
    }
    private record FloorCount(long count, Direction direction) {

    }

    private LosPosition findBestExplorePosition(CellType[][] los) {
        FloorCount north = new FloorCount(IntStream.range(0, los.length).mapToObj(i -> los[0][i]).filter(ct -> ct == CellType.FLR).count(), Direction.NORTH);
        FloorCount east = new FloorCount(IntStream.range(0, los.length).mapToObj(i -> los[i][los.length - 1]).filter(ct -> ct == CellType.FLR).count(), Direction.EAST);
        FloorCount south = new FloorCount(IntStream.range(0, los.length).mapToObj(i -> los[los.length - 1][i]).filter(ct -> ct == CellType.FLR).count(), Direction.SOUTH);
        FloorCount west = new FloorCount(IntStream.range(0, los.length).mapToObj(i -> los[i][0]).filter(ct -> ct == CellType.FLR).count(), Direction.WEST);
        Comparator<FloorCount> comparator = Comparator.comparingLong(FloorCount::count).thenComparing(FloorCount::direction);
        FloorCount betterDirection = max(Arrays.asList(north, east, south, west), comparator);
//        Position target = CENTER.move(betterDirection.direction());
//        while (target.cellAt(los) == CellType.FLR) {
//            Position next = target.move(betterDirection.direction());
//            if (next.cellAt(los) != CellType.FLR) {
//                return target;
//            }
//            target = next;
//        }
        return CENTER.walk(betterDirection.direction());
    }

    private LosPosition findExit(CellType[][] los) {
        for (int row = 0; row < los.length; row++) {
            for (int col = 0; col < los[row].length; col++) {
                if (los[row][col] == CellType.FSH) {
                    return new LosPosition(row, col);
                }
            }
        }
        return null;
    }

    //I put a convenience launcher here in case you want to run a single maze headless.
    public static void main(String[] args) {
        new HeadlessMain(new ChallengeImpl(),
//                MazeFactory.getFlowingCave(
//                MazeFactory.get1WMap(
                MazeFactory.getDungeon(
                        Configuration.SMALL
//                        Configuration.MEDIUM
//                        Configuration.LARGE
//                        Configuration.HUGE
                        , 100
                )
        ).doAllMoves();
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
        for (var v : los) {
            System.out.println(Arrays.toString(v));
        }
        System.out.println();
    }
}
