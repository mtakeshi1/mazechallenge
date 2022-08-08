package mc.renamebeforepr;

import mc.Configuration;
import mc.challenge.Challenge;
import mc.challenge.maze.Direction;
import mc.challenge.maze.HeadlessMain;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.MazeFactory;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    private AbsolutePosition finishLine = null;
//    private AbsolutePosition currentPosition = new AbsolutePosition(CENTER.row(), CENTER.col());

    private PositionOffset offset = new PositionOffset(0, 0);
    private Direction selectedDirection = Direction.NORTH;
    private Map<AbsolutePosition, CellType> knownCells = new HashMap<>();
    private Queue<AbsolutePosition> selectedPath = new ArrayDeque<>();

    private AbsolutePosition min = new AbsolutePosition(0, 0);
    private AbsolutePosition max = new AbsolutePosition(12, 12);

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
        AbsolutePosition currentPosition = offset.plus(CENTER);
        if (selectedPath.isEmpty()) {
            traceNextPath(currentPosition);
        }
        AbsolutePosition remove = selectedPath.remove();
        this.selectedDirection = currentPosition.directionTo(remove);
        this.offset = offset.walk(this.selectedDirection);
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

    public static Direction reverse(Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.SOUTH;
            case EAST -> Direction.WEST;
            case SOUTH -> Direction.NORTH;
            case WEST -> Direction.EAST;
        };
    }

    private void traceNextPath(AbsolutePosition currentPosition) {
        AbsolutePosition target = finishLine != null ? finishLine : findBestExplorePosition();
        selectedPath.addAll(findPath(currentPosition, target, knownCells));
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
                    if (cellType == CellType.FSH && finishLine == null) {
                        // we just found the finish line, b-line straight to it
                        finishLine = absolute;
                        this.selectedPath.clear();
                    }
                }
                min = new AbsolutePosition(Math.min(min.row(), absolute.row()), Math.min(min.col(), absolute.col()));
                max = new AbsolutePosition(Math.max(max.row(), absolute.row()), Math.max(max.col(), absolute.col()));
            }
        }
    }

    private void adjustPositions(Direction selectedDirection) {
        this.offset = this.offset.walk(reverse(selectedDirection));
    }

    public static Collection<AbsolutePosition> findPath(AbsolutePosition from, AbsolutePosition to, Map<AbsolutePosition, CellType> knownCells) {
        SortedSet<FringeEntry> fringe = new TreeSet<>(Comparator.comparingInt(FringeEntry::cost).thenComparingInt(System::identityHashCode));
        fringe.add(new FringeEntry(null, from, 0));
        Set<AbsolutePosition> visited = new HashSet<>();
        while (!fringe.isEmpty()) {
            FringeEntry first = fringe.first();
            fringe.remove(first);
            visited.add(first.destination());
            if (to.equals(first.destination())) {
                return findNextPath(from, first, new LinkedList<>());
            }
            explore(first.destination(), first, fringe, visited, 0, knownCells);
        }
        throw new RuntimeException("no path from " + from + " to: " + to);
//        return Direction.NORTH;
    }

    public static Collection<AbsolutePosition> findNextPath(AbsolutePosition from, FringeEntry first, LinkedList<AbsolutePosition> container) {
        Objects.requireNonNull(first, "should not reach end of chain");
        if (first.getSource().equals(from)) {
            return container;
        }
        container.addFirst(first.getSource());
        return findNextPath(from, first.previousPath(), container);
    }

    public static Direction calculateDirection(Position from, Position destination) {
        for (Direction direction : Direction.values()) {
            if (from.walk(direction).equals(destination)) {
                return direction;
            }
        }
        throw new RuntimeException("No direction to move from: " + from + " to " + destination);
    }

    public static void explore(AbsolutePosition from, FringeEntry current, SortedSet<FringeEntry> fringe, Set<AbsolutePosition> visited, int cost, Map<AbsolutePosition, CellType> knownCells) {
        int newCost = cost + 1;
        for (Direction direction : Direction.values()) {
            var next = from.walk(direction);
            CellType type = knownCells.get(next);
            //next.isWithin(los) && next.cellAt(los) != CellType.WLL && next.cellAt(los) != CellType.UNK
            if (!visited.contains(next) && type != null && type != CellType.WLL && type != CellType.UNK) {
                FringeEntry entry = new FringeEntry(current, next, newCost);
                fringe.add(entry);
            }
        }
    }

    private record FloorCount(int count, AbsolutePosition cell) {
    }

    private FloorCount countUnknownNeighboors(AbsolutePosition position) {
        int count = (int) Arrays.stream(Direction.values()).map(position::walk).map(knownCells::get).filter(Objects::nonNull).count();
        return new FloorCount(count, position);
    }

    private AbsolutePosition findBestExplorePosition() {
        Stream<FloorCount> colStream = Stream.concat(
                IntStream.rangeClosed(min.col(), max.col()).mapToObj(c -> new AbsolutePosition(min.row(), c)).filter(knownCells::containsKey).map(this::countUnknownNeighboors),
                IntStream.rangeClosed(min.col(), max.col()).mapToObj(c -> new AbsolutePosition(max.row(), c)).filter(knownCells::containsKey).map(this::countUnknownNeighboors)
        );
        Stream<FloorCount> rowStream = Stream.concat(IntStream.rangeClosed(min.row(), max.row()).mapToObj(r -> new AbsolutePosition(r, min.col())).filter(knownCells::containsKey).map(this::countUnknownNeighboors), IntStream.rangeClosed(min.row(), max.row()).mapToObj(r -> new AbsolutePosition(r, max.col())).filter(knownCells::containsKey).map(this::countUnknownNeighboors));
        Optional<FloorCount> max = Stream.concat(colStream, rowStream).max(Comparator.comparingInt(FloorCount::count));
        return max.map(FloorCount::cell).orElseThrow(() -> new RuntimeException("could not find next position to explore"));
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
