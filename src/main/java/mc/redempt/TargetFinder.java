package mc.redempt;

import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.Position;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TargetFinder {
	
	private static int areaSize = 10;
	
	private record Area(int row, int col) {
		
		public Position getCenter() {
			int offset = areaSize / 2;
			int row = this.row * areaSize + offset;
			int col = this.col * areaSize + offset;
			return new Position(row, col);
		}
		
		public Position getMinimumCorner() {
			return new Position(row * areaSize, col * areaSize);
		}
		
		public Position getMaximumCorner() {
			return getMinimumCorner().plus(areaSize, areaSize);
		}
		
		public int getInBoundsScore(MazeMap map) {
			int score = 0;
			score += map.isInBounds(getMinimumCorner(), 3) ? 1 : 0;
			score += map.isInBounds(getMaximumCorner(), 3) ? 1 : 0;
			return score;
		}
		
	}
	
	private Position exit;
	private Map<Area, Set<Position>> unmapped = new HashMap<>();
	private Set<Position> removed = new HashSet<>();
	private Position last;
	private MazeMap map;
	
	public TargetFinder(MazeMap map) {
		this.map = map;
	}
	
	private Area getArea(Position pos) {
		int col = Math.floorDiv(pos.col(), areaSize);
		int row = Math.floorDiv(pos.row(), areaSize);
		return new Area(row, col);
	}
	
	public void remove(Position pos) {
		removed.add(pos);
		Area area = getArea(pos);
		Set<Position> set = unmapped.get(area);
		if (set == null) {
			return;
		}
		set.remove(pos);
		if (set.size() == 0) {
			unmapped.remove(area);
		}
	}
	
	private void addUnmapped(Position pos) {
		unmapped.computeIfAbsent(getArea(pos), k -> new HashSet<>()).add(pos);
	}
	
	public void add(Position pos) {
		if (removed.contains(pos)) {
			return;
		}
		CellType type = map.getType(pos);
		if (type == CellType.FLR) {
			remove(pos);
			getAdjacentTiles(pos, CellType.UNK).forEach(this::addUnmapped);
		} else if (type == CellType.UNK) {
			if (!getAdjacentTiles(pos, CellType.FLR).isEmpty()) {
				addUnmapped(pos);
			}
			return;
		} else {
			remove(pos);
		}
		if (type == CellType.FSH) {
			exit = pos;
		}
	}
	
	private void rescan() {
		unmapped.clear();
		removed.clear();
		map.forEachTile(this::add);
	}
	
	private <T> T rescanUntil(Supplier<Optional<T>> supplier) {
		Optional<T> opt = supplier.get();
		if (opt.isEmpty()) {
			rescan();
			return supplier.get().get();
		}
		return opt.get();
	}
	
	private Area getNearestArea(Position p) {
		return rescanUntil(() -> unmapped.keySet().stream().min(Comparator.comparingInt(a -> Pathfinder.dist(a.getCenter(), p))));
	}
	
	public boolean hasExit() {
		return exit != null;
	}
	
	public Position getNextTarget() {
		if (exit != null) {
			return exit;
		}
		Area nearest = getNearestArea(map.getPosition());
		Optional<Position> optNext = unmapped.get(nearest).stream().min(Comparator.comparingInt(p -> Pathfinder.dist(map.getPosition(), p)));
		if (optNext.isEmpty()) {
			rescan();
			return getNextTarget();
		}
		Position next = optNext.get();
		if (next.equals(last)) {
			remove(next);
		}
		last = next;
		return next;
	}
	
	private List<Position> getAdjacentTiles(Position pos, CellType type) {
		return Arrays.stream(Direction.values())
				.map(dir -> pos.plus(dir.getTP()))
				.filter(p -> map.getType(p) == type)
				.collect(Collectors.toList());
	}
	
}
