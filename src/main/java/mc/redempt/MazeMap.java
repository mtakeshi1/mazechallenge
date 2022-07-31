package mc.redempt;

import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;

public class MazeMap {
	
	private static Position combine(Position first, Position second, IntBinaryOperator op) {
		return new Position(op.applyAsInt(first.row(), second.row()), op.applyAsInt(first.col(), second.col()));
	}
	
	private Map<Position, CellType> tiles = new HashMap<>();
	private Position pos = new Position(0, 0);
	private Position min = pos;
	private Position max = pos;
	
	public Position getPosition() {
		return pos;
	}
	
	public Position getStartPosition() {
		return tiles.keySet().stream().filter(t -> getType(t) == CellType.SRT).findFirst().orElse(null);
	}
	
	public Position getCenter() {
		return combine(min, max, (a, b) -> (a + b) / 2);
	}
	
	public boolean move(Direction dir) {
		Position diff = dir.getTP();
		Position newPos = pos.plus(diff.row(), diff.col());
		if (getType(newPos) == CellType.WLL) {
			System.out.println("Invalid move");
			return false;
		}
		pos = newPos;
		return true;
	}
	
	public Position add(int row, int col, CellType type) {
		Position pos = this.pos.plus(row, col);
		if (tiles.containsKey(pos) && type == CellType.UNK) {
			return pos;
		}
		tiles.put(pos, type);
		min = combine(min, pos, Math::min);
		max = combine(max, pos, Math::max);
		return pos;
	}
	
	public boolean isInBounds(Position pos, int boundShrink) {
		return pos.row() >= min.row() + boundShrink && pos.row() <= max.row() - boundShrink
				&& pos.col() >= min.col() + boundShrink && pos.col() <= max.col() - boundShrink;
	}
	
	public void forEachTile(Consumer<Position> consumer) {
		for (int col = min.col(); col < max.col(); col++) {
			for (int row = min.row(); row < max.row(); row++) {
				Position pos = new Position(row, col);
				consumer.accept(pos);
			}
		}
	}
	
	public void printMap() {
		for (int col = min.col(); col < max.col(); col++) {
			for (int row = min.row(); row < max.row(); row++) {
				Position pos = new Position(row, col);
				if (pos.equals(this.pos)) {
					System.out.print("+");
					continue;
				}
				System.out.print(toChar(getType(pos)));
			}
			System.out.println();
		}
		System.out.println("\n");
	}
	
	public CellType getType(Position pos) {
		return tiles.get(pos);
	}
	
	private char toChar(CellType cell) {
		if (cell == null) {
			return '_';
		}
		return switch(cell) {
			case FSH -> 'x';
			case FLR -> '.';
			case SRT -> 'S';
			case UNK -> '_';
			case WLL -> '#';
		};
	}
	
}
