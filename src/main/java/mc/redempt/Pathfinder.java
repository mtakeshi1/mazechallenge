package mc.redempt;

import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.Position;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiConsumer;

public class Pathfinder {
	
	public static int dist(Position first, Position second) {
		return Math.abs(first.row() - second.row()) + Math.abs(first.col() - second.col());
	}
	
	private Position goal;
	private MazeMap map;
	private Stack<Direction> moves = new Stack<>();
	
	public Pathfinder(MazeMap map) {
		this.map = map;
	}
	
	public boolean setTarget(Position target) {
		if (target.equals(goal)) {
			return true;
		}
		goal = target;
		return pathfind();
	}
	
	public Position getTarget() {
		return goal;
	}
	
	private record SearchNode(Position pos, int score, SearchNode parent, Direction dir) {}
	
	private boolean pathfind() {
		moves.clear();
		if (goal.equals(map.getPosition())) {
			return false;
		}
		Set<Position> seen = new HashSet<>();
		Queue<SearchNode> queue = new PriorityQueue<>(Comparator.comparingInt(SearchNode::score));
		queue.add(new SearchNode(map.getPosition(), 0, null, null));
		while (!queue.isEmpty()) {
			SearchNode next = queue.poll();
			if (!seen.add(next.pos())) {
				continue;
			}
			if (next.pos().equals(goal)) {
				unwrapPath(next);
				return true;
			}
			forEachMove(next.pos(), (pos, dir) -> {
				SearchNode move = new SearchNode(pos, dist(pos, goal), next, dir);
				queue.add(move);
			});
		}
		return false;
	}
	
	private void unwrapPath(SearchNode node) {
		while (node.parent() != null) {
			moves.add(node.dir());
			node = node.parent();
		}
	}
	
	public boolean hasPath() {
		return !moves.isEmpty();
	}
	
	public Direction getNextMove() {
		return moves.pop();
	}
	
	private void forEachMove(Position pos, BiConsumer<Position, Direction> consumer) {
		for (Direction dir : Direction.values()) {
			Position rel = pos.plus(dir.getTP());
			CellType type = map.getType(rel);
			if (!map.isInBounds(rel, 0) || type == CellType.WLL) {
				continue;
			}
			consumer.accept(rel, dir);
		}
	}
	
}
