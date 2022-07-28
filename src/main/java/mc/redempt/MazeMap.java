package mc.redempt;

import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.Position;

import java.util.HashMap;
import java.util.Map;
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
	
	public void move(Direction dir) {
		Position diff = dir.getTP();
		pos = pos.plus(diff.row(), diff.col());
	}
	
	public void add(int row, int col, CellType type) {
		Position pos = this.pos.plus(row, col);
		tiles.put(pos, type);
		min = combine(min, pos, Math::min);
		max = combine(max, pos, Math::max);
	}
	
	public void printMap() {
		System.out.println();
		System.out.println();
		for (int row = min.row(); row < max.row(); row++) {
			for (int col = min.col(); col < max.col(); col++) {
				System.out.print(toChar(getType(new Position(row, col))));
			}
			System.out.println();
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
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
