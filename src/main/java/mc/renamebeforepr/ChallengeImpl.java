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
    private AbsolutePosition currentPosition = new AbsolutePosition(CENTER.row(), CENTER.col());

    private PositionOffset offset = new PositionOffset(0, 0);
    private Direction selectedDirection = Direction.NORTH;
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
        newGridSpotted(los);
        visitedCells.add(currentPosition);
        if (selectedPath.isEmpty() || currentIndex >= selectedPath.size() || currentTargetUnfeasible()) {
            System.out.println("Looking for new destination");
            selectedPath.clear();
            currentIndex = 0;
            traceNextPath(currentPosition);
        }
        printOut(Collections.emptyMap());
        AbsolutePosition remove = selectedPath.get(currentIndex++);
        if (!remove.equals(currentPosition)) {
            this.selectedDirection = currentPosition.directionTo(remove);
            System.out.println(selectedDirection);
            adjustOffset(selectedDirection);
            this.currentPosition = currentPosition.walk(selectedDirection);
        } else {
            System.out.println("Done?");
        }
//        this.offset = offset.walk(this.selectedDirection);
    }

    private boolean currentTargetUnfeasible() {
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
        System.out.println("New destination selected: " + target);
        Collection<AbsolutePosition> path = findPath(currentPosition, target, knownCells, true);
        if (path == null) {
            printOut(Map.of(currentPosition, " F ", target, " T "));
        }
        selectedPath.addAll(path);
    }

    private void newGridSpotted(CellType[][] los) {
        //optimize according to last moved direction, don't need to probe the entire array every time
        for (int row = 0; row < los.length; row++) {
            for (int col = 0; col < los[0].length; col++) {
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
        }
    }

    private void adjustOffset(Direction selectedDirection) {
//        this.offset = this.offset.walk(reverse(selectedDirection));
        this.offset = this.offset.walk(selectedDirection);
    }

    public static Collection<AbsolutePosition> findPath(AbsolutePosition from, AbsolutePosition to, Map<AbsolutePosition, CellType> knownCells) {
        return findPath(from, to, knownCells, false);
    }

    public static Collection<AbsolutePosition> findPath(AbsolutePosition from, AbsolutePosition to, Map<AbsolutePosition, CellType> knownCells, boolean lenient) {
        //TODO this needs work, path finding
        Queue<FringeEntry> fringe = new PriorityQueue<>(Comparator.comparingInt(FringeEntry::cost).thenComparingInt(System::identityHashCode));
        Queue<FringeEntry> closestEntries = new PriorityQueue<>(Comparator.comparingDouble(fe -> fe.destination().distanceTo(from) + fe.destination().distanceTo(to)));
        fringe.add(new FringeEntry(null, from, 0));
        Set<AbsolutePosition> visited = new HashSet<>();
        while (!fringe.isEmpty()) {
            FringeEntry first = fringe.remove();
            visited.add(first.destination());
            if (lenient) {
                closestEntries.add(first);
            }
            if (to.equals(first.destination())) {
                LinkedList<AbsolutePosition> container = new LinkedList<>();
                container.addFirst(first.destination());
                return selectPathTiles(from, first, container);
            }
            explore(first.destination(), first, fringe, visited, knownCells, from, to);
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

    public static Collection<AbsolutePosition> selectPathTiles(AbsolutePosition from, FringeEntry first, LinkedList<AbsolutePosition> container) {
        Objects.requireNonNull(first, "should not reach end of chain");
        if (first.getSource() == null || first.getSource().equals(from)) {
            return container;
        }
        container.addFirst(first.getSource());
        return selectPathTiles(from, first.previousPath(), container);
    }

    public static void explore(AbsolutePosition from, FringeEntry current, Collection<? super FringeEntry> fringe, Set<AbsolutePosition> visited, Map<AbsolutePosition, CellType> knownCells, AbsolutePosition startingPoint, AbsolutePosition goal) {
        for (Direction direction : Direction.values()) {
            var next = from.walk(direction);
            CellType type = knownCells.get(next);
            //next.isWithin(los) && next.cellAt(los) != CellType.WLL && next.cellAt(los) != CellType.UNK
            if (!visited.contains(next) && type != null && type != CellType.WLL && type != CellType.UNK) {
                FringeEntry entry = new FringeEntry(current, next, next.stepDistance(startingPoint) + next.stepDistance(goal));
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

    private AbsolutePosition findBestExplorePosition() {
        Stream<FloorCount> stream = IntStream.rangeClosed(min.col(), max.col()).mapToObj(Integer::valueOf).flatMap(col -> IntStream.rangeClosed(min.row(), max.row())
                        .mapToObj(row -> new AbsolutePosition(row, col)))
                .filter(abs -> !visitedCells.contains(abs))
                .filter(abs -> knownCells.get(abs) == CellType.FLR)
                .map(this::countUnknownNeighboors);
        Optional<FloorCount> max = stream.max(Comparator.comparingInt(FloorCount::count));
        return max.map(FloorCount::cell).orElseThrow(() -> new RuntimeException("could not find next position to explore"));
    }

    private void printOut(Map<AbsolutePosition, String> markers) {
        IntStream.rangeClosed(min.row(), max.row()).mapToObj(Integer::valueOf).flatMap(row -> IntStream.rangeClosed(min.col(), max.col()).mapToObj(col -> new AbsolutePosition(row, col))).map(p -> format(p, markers)).forEachOrdered(System.out::print);
    }

    private String format(AbsolutePosition absolutePosition, Map<AbsolutePosition, String> markers) {
        CellType cellType = knownCells.get(absolutePosition);
        String base = "";
        int indexOf = selectedPath.indexOf(absolutePosition);
        if (markers.containsKey(absolutePosition)) {
            base = markers.get(absolutePosition);
        } else if (currentPosition.equals(absolutePosition)) {
            base = " C ";
        } else if (cellType == null || cellType == CellType.UNK) {
            base = " ? ";
        } else if (indexOf >= 0) {
            base = String.format("%3d", indexOf);
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
