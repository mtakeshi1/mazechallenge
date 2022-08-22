package mc.renamebeforepr;

import mc.Configuration;
import mc.challenge.Challenge;
import mc.challenge.maze.Direction;
import mc.challenge.maze.HeadlessMain;
import mc.challenge.maze.Maze;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.MazeFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
    private AbsolutePosition currentPosition = new AbsolutePosition(CENTER.row(), CENTER.col());

    private PositionOffset offset = new PositionOffset(0, 0);
    private Direction selectedDirection;
    private Map<AbsolutePosition, CellType> knownCells = new HashMap<>();
    private Set<AbsolutePosition> visitedCells = new HashSet<>();
    private List<AbsolutePosition> selectedPath = new ArrayList<>();
    private int currentIndex;

    private AbsolutePosition min = new AbsolutePosition(0, 0);
    private AbsolutePosition max = new AbsolutePosition(12, 12);

    private int searchRadius = 2;

    /**
     * This method will be called on init and after each move.
     *
     * @param los [13,13] array where 'you' always are at [6,6] ( further explained in printLOSUpdate
     */
    @Override
    public void handleLineOfSightUpdate(CellType[][] los) {
        newGridSpotted(los, selectedDirection);
        visitedCells.add(currentPosition);
        if (selectedPath.isEmpty() || currentIndex >= selectedPath.size() || currentTargetAlreadyDiscovered() || pathIsBlocked()) {
            selectedPath.clear();
            currentIndex = 0;
            traceNextPath(currentPosition);
        }
        AbsolutePosition remove = selectedPath.get(currentIndex++);
        if (!remove.equals(currentPosition)) {
            this.selectedDirection = currentPosition.directionTo(remove);
            adjustOffset(selectedDirection);
            this.currentPosition = currentPosition.walk(selectedDirection);
        } else {
//            System.out.println("Done?");
        }
    }

    private boolean pathIsBlocked() {
        for (int i = currentIndex; i < selectedPath.size(); i++) {
            if (knownCells.get(selectedPath.get(i)) == CellType.WLL) {
                return true;
            }
        }
        return false;
    }

    private boolean currentTargetAlreadyDiscovered() {
        AbsolutePosition target = selectedPath.get(selectedPath.size() - 1);
        FloorCount floorCount = countUnknownNeighboors(target);
        return floorCount.count() == 0;
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

    private void traceNextPath(AbsolutePosition currentPosition) {
        Collection<AbsolutePosition> path = finishLine != null ?
                findPath(currentPosition, finishLine, knownCells, true, PathFindingCallback.NO_OP) :
                pathToclosestUnknown(currentPosition, knownCells, PathFindingCallback.NO_OP);
        if (path == null) {
            throw new RuntimeException("Could not find path from: " + currentPosition);
        }
        selectedPath.addAll(path);
    }

    private void newGridSpotted(CellType[][] los, Direction selectedDirection) {
//        if (selectedDirection == null) {
        //optimize according to last moved direction, don't need to probe the entire array every time
        for (int row = 0; row < los.length; row++) {
            for (int col = 0; col < los[0].length; col++) {
                updateCell(los, row, col);
            }
        }
//        } else {
//            switch (selectedDirection) {
//                case NORTH -> {
//                    for (int col = 0; col < los[0].length; col++) {
//                        updateCell(los, los.length - 1, col);
//                    }
//                }
//                case EAST -> {
//                    for (int row = 0; row < los.length; row++) {
//                        updateCell(los, row, los[row].length - 1);
//                    }
//                }
//                case SOUTH -> {
//                    for (int col = 0; col < los[0].length; col++) {
//                        updateCell(los, 0, col);
//                    }
//                }
//                case WEST -> {
//                    for (int row = 0; row < los.length; row++) {
//                        updateCell(los, row, 0);
//                    }
//                }
//            }
//        }
    }

    private void updateCell(CellType[][] los, int row, int col) {
        LosPosition relative = new LosPosition(row, col);
        CellType cellType = relative.cellAt(los);
        AbsolutePosition absolute = offset.plus(relative);
        if (!knownCells.containsKey(absolute) || knownCells.get(absolute) == CellType.UNK) {
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

    private void adjustOffset(Direction selectedDirection) {
//        this.offset = this.offset.walk(reverse(selectedDirection));
        this.offset = this.offset.walk(selectedDirection);
    }

    public static Collection<AbsolutePosition> findPath(AbsolutePosition from, AbsolutePosition to, Map<AbsolutePosition, CellType> knownCells) {
        return findPath(from, to, knownCells, false, PathFindingCallback.NO_OP);
    }

    private static void writeTestCase(AbsolutePosition from, AbsolutePosition to, Map<AbsolutePosition, CellType> knownCells) {
        try (ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream("test.bin"))) {
            oout.writeObject(from);
            oout.writeObject(to);
            oout.writeObject(knownCells);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<AbsolutePosition> pathToclosestUnknown(AbsolutePosition current, Map<AbsolutePosition, Maze.CellType> maze, PathFindingCallback callback) {
        //TODO implement a DFS instead
        Queue<FringeEntry> fringe = new PriorityQueue<>(Comparator.comparingInt(FringeEntry::steps));
        Set<AbsolutePosition> visited = new HashSet<>();
        fringe.add(new FringeEntry(null, current, 0, 0));
        while (!fringe.isEmpty()) {
            var first = fringe.remove();
            callback.newState(first.destination(), fringe, visited, maze, current, current);
            if (maze.getOrDefault(first.destination(), CellType.UNK) == CellType.UNK) {
                return selectPathTiles(current, first, new LinkedList<>());
            }
            if (visited.contains(first.destination())) {
                continue;
            }
            visited.add(first.destination());
            for (var dir : Direction.values()) {
                var next = first.destination().walk(dir);
//                if (maze.get(next) == CellType.UNK) {
//                    return selectPathTiles(current, first, new LinkedList<>());
//                }
                if (!visited.contains(next) && maze.get(next) != CellType.WLL) {
                    fringe.add(new FringeEntry(first, next, first.steps() + 1, 0));
                }
            }
        }
        throw new RuntimeException("No unknown tiles from: " + current);
    }

    public static AbsolutePosition closestUnknown(AbsolutePosition current, Map<AbsolutePosition, Maze.CellType> maze, PathFindingCallback callback) {
        List<AbsolutePosition> path = pathToclosestUnknown(current, maze, callback);
        return path.get(path.size() - 1);
    }


    public static Collection<AbsolutePosition> findPath(AbsolutePosition from, AbsolutePosition to, Map<AbsolutePosition, CellType> knownCells, boolean lenient, PathFindingCallback callback) {
//        writeTestCase(from, to, knownCells);
        Queue<FringeEntry> fringe = new PriorityQueue<>(Comparator.comparingInt(FringeEntry::cost).thenComparingInt(FringeEntry::steps));
        Queue<FringeEntry> closestEntries = new PriorityQueue<>(Comparator.comparingDouble(fe -> fe.destination().distanceTo(from) + fe.destination().distanceTo(to)));
        fringe.add(new FringeEntry(null, from, 0, 0));
        Set<AbsolutePosition> visited = new HashSet<>();
        while (!fringe.isEmpty()) {
            FringeEntry first = fringe.remove();
            callback.newState(first.destination(), fringe, visited, knownCells, from, to);
            if (visited.contains(first.destination())) {
                continue;
            }
            visited.add(first.destination());
            if (lenient) {
                closestEntries.add(first);
            }
            if (to.equals(first.destination())) {
                LinkedList<AbsolutePosition> container = new LinkedList<>();
                container.addFirst(first.destination());
                return selectPathTiles(from, first, container);
            }
            explore(first.destination(), first, fringe, visited, knownCells, from, to, first.steps());
        }
        while (!closestEntries.isEmpty()) {
            var first = closestEntries.remove();
            LinkedList<AbsolutePosition> container = new LinkedList<>();
            container.addFirst(first.destination());
            var path = selectPathTiles(from, first, container);
            if (path != null) {
                return path;
            }
        }
        throw new RuntimeException("no path from: " + from + " to: " + to);
    }

    public static List<AbsolutePosition> selectPathTiles(AbsolutePosition from, FringeEntry first, LinkedList<AbsolutePosition> container) {
        Objects.requireNonNull(first, "should not reach end of chain");
        if (first.getSource() == null || first.getSource().equals(from)) {
            return container;
        }
        container.addFirst(first.getSource());
        return selectPathTiles(from, first.previousPath(), container);
    }

    public static void explore(AbsolutePosition from, FringeEntry current, Collection<? super FringeEntry> fringe, Set<AbsolutePosition> visited, Map<AbsolutePosition, CellType> knownCells, AbsolutePosition startingPoint, AbsolutePosition goal, int step) {
        int nextStep = step + 1000;
        for (Direction direction : Direction.values()) {
            var next = from.walk(direction);
            CellType type = knownCells.get(next);
            //next.isWithin(los) && next.cellAt(los) != CellType.WLL && next.cellAt(los) != CellType.UNK
            if (!visited.contains(next) && type != CellType.WLL) {
                FringeEntry entry = new FringeEntry(current, next, nextStep, next.stepDistance(startingPoint) + next.stepDistance(goal));
                fringe.add(entry);
            }
        }
    }

    private record FloorCount(int count, AbsolutePosition cell) {
    }

    private FloorCount countUnknownNeighboors(AbsolutePosition position) {
//        int count = (int) IntStream.rangeClosed(position.row() - searchRadius, position.row() + searchRadius).mapToObj(Integer::valueOf).flatMap(
//                row -> IntStream.rangeClosed(position.col() - searchRadius, position.col() + searchRadius).mapToObj(col -> new AbsolutePosition(row, col)).filter(cell -> knownCells.get(cell) == CellType.UNK)).count();
////        int count = (int) Arrays.stream(Direction.values()).map(position::walk).map(knownCells::get).filter(Objects::nonNull).filter(ct -> ct == CellType.UNK).count();
        int count = countUnknownsAround(position, searchRadius, new HashSet<>());
        return new FloorCount(count, position);
    }

    private int countUnknownsAround(AbsolutePosition center, int steps, Set<AbsolutePosition> visited) {
        //TODO improve this to find better targets
        CellType cellType = knownCells.get(center);
        int c = cellType == CellType.UNK ? 1 : 0;
        if (steps == 0 || cellType == CellType.WLL) {
            return c;
        }
        for (var dir : Direction.values()) {
            var next = center.walk(dir);
            if (!visited.contains(next)) {
                visited.add(next);
                c += countUnknownsAround(next, steps - 1, visited);
            }
        }
        return c;
    }

    private AbsolutePosition findBestExplorePosition(AbsolutePosition currentPosition) {
        Stream<FloorCount> stream = IntStream.rangeClosed(min.col(), max.col()).boxed().flatMap(col -> IntStream.rangeClosed(min.row(), max.row())
                        .mapToObj(row -> new AbsolutePosition(row, col)))
                .filter(abs -> !visitedCells.contains(abs))
                .filter(abs -> knownCells.get(abs) == CellType.FLR)
                .map(this::countUnknownNeighboors);
        Optional<FloorCount> max = stream.max(Comparator.comparingInt(FloorCount::count).thenComparingInt(fc -> -fc.cell().stepDistance(currentPosition)));
        return max.map(FloorCount::cell).orElseThrow(() -> new RuntimeException("could not find next position to explore"));
    }

    public static void print(Map<AbsolutePosition, CellType> knownCells, Map<AbsolutePosition, String> markers) {
        AbsolutePosition[] minMax = discoverMinMax(knownCells);
        print(knownCells, markers, minMax[0], minMax[1]);
    }

    public static AbsolutePosition[] discoverMinMax(Collection<? extends AbsolutePosition> cells) {
        AbsolutePosition[] minMax = new AbsolutePosition[2];

        cells.forEach(pos -> {
            AbsolutePosition min = minMax[0];
            if (min == null) {
                minMax[0] = pos;
                minMax[1] = pos;
            } else {
                AbsolutePosition max = minMax[1];
                minMax[0] = new AbsolutePosition(Math.min(min.row(), pos.row()), Math.min(min.col(), pos.col()));
                minMax[1] = new AbsolutePosition(Math.max(max.row(), pos.row()), Math.max(max.col(), pos.col()));
            }

        });
        return minMax;
    }

    public static AbsolutePosition[] discoverMinMax(Map<AbsolutePosition, CellType> knownCells) {
        return discoverMinMax(knownCells.keySet());
    }

    public static void print(Map<AbsolutePosition, CellType> knownCells, Map<AbsolutePosition, String> markers, AbsolutePosition min, AbsolutePosition max) {
        IntStream.rangeClosed(min.row(), max.row()).boxed().flatMap(row -> IntStream.rangeClosed(min.col(), max.col()).mapToObj(col -> new AbsolutePosition(row, col))).map(p -> format(p, markers, knownCells, max)).forEachOrdered(System.out::print);
    }

    private void printOut(Map<AbsolutePosition, String> markers) {
        Map<AbsolutePosition, String> markersCopy = new HashMap<>(markers);
        markersCopy.put(currentPosition, " C ");
        for (int i = currentIndex; i < selectedPath.size(); i++) {
            String p = String.format("%3d", (i - currentIndex));
            markersCopy.put(selectedPath.get(i), p);
        }
        print(knownCells, markersCopy, min, max);
    }

    private static String format(AbsolutePosition absolutePosition, Map<AbsolutePosition, String> markers, Map<AbsolutePosition, CellType> knownCells, AbsolutePosition max) {
        CellType cellType = knownCells.get(absolutePosition);
        String base;
        if (markers.containsKey(absolutePosition)) {
            base = markers.get(absolutePosition);
        } else if (cellType == null || cellType == CellType.UNK) {
            base = " ? ";
        } else {
            base = switch (cellType) {
                case WLL -> " W ";
                case FSH -> " F ";
                case SRT -> " S ";
                case FLR -> "   ";
                default -> " ? ";
            };
        }

        if (absolutePosition.col() == max.col()) {
            base += '\n';
        }
        return base;
    }

    //I put a convenience launcher here in case you want to run a single maze headless.
    public static void main(String[] args) {
        new HeadlessMain(new ChallengeImpl(),
//                MazeFactory.getFlowingCave(
//                MazeFactory.get1WMap(
                MazeFactory.getDungeon(
//                        Configuration.SMALL
                        Configuration.MEDIUM
//                        Configuration.LARGE
//                        Configuration.HUGE
                        , 102
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
