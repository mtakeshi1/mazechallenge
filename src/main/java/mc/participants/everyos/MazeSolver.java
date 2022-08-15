package mc.participants.everyos;

import mc.challenge.maze.Direction;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.Position;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class MazeSolver {
	
	private static final Direction[] DIRECTIONS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

	private final MazeCache mazeCache;
	private final Deque<Integer> trail = new ArrayDeque<>();
	
	private Position playerPosition;

	public MazeSolver(MazeCache cache, Position playerPosition) {
		this.mazeCache = cache;
		this.playerPosition = playerPosition;
	}
	
	public Direction getMove() {
		MazeCell cell = mazeCache.getCell(playerPosition);
		if (cell == null || excludeCell(cell) || !cellNeighboursPresent()) {
			return null;
		}
		markCellNeighbours();
		int nextMove = findMove(cell);
		if (nextMove != -1) {
			cell.moves()[nextMove] = false;
			trail.push(nextMove);
			Direction direction = DIRECTIONS[nextMove];
			playerPosition = move(playerPosition, direction);
			
			return direction;
		} else {
			if (trail.isEmpty()) {
				return null;
			}
			Direction direction = DIRECTIONS[(trail.pop() + 2) % 4];
			playerPosition = move(playerPosition, direction);
			return direction;
		}
	}
	
	protected boolean excludeCell(MazeCell cell) {
		return false;
	}
	
	protected abstract Position getFlagPosition();
	
	public void clearCurrentPath() {
		while (!trail.isEmpty()) {
			int move = trail.pop();
			Direction direction = DIRECTIONS[(move + 2) % 4];
			playerPosition = move(playerPosition, direction);
			mazeCache
				.getCell(playerPosition)
				.moves()[move] = true;
		}
	}

	private int findMove(MazeCell cell) {
		int preferredStart = getPreferredStart();
		for (int i = 0; i < 4; i++) {
			int j = (i + preferredStart) % 4;
			if (cell.moves()[j] && !nextCellIsSolid(DIRECTIONS[j])) {
				return j;
			}
		}
		return -1;
	}

	private boolean nextCellIsSolid(Direction direction) {
		Position tilePos = move(playerPosition, direction);
		CellType cellType = mazeCache.getCell(tilePos).cellType();
		return cellType == CellType.UNK || cellType == CellType.WLL;
	}
	
	private boolean cellNeighboursPresent() {
		for (int i = 0; i < 4; i++) {
			MazeCell cell = mazeCache.getCell(move(playerPosition, DIRECTIONS[i]));
			if (cell == null || cell.cellType() == CellType.UNK) {
				return false;
			}
		}
		
		return true;
	}
	
	private void markCellNeighbours() {
		for (int i = 0; i < 4; i++) {
			mazeCache
				.getCell(move(playerPosition, DIRECTIONS[i]))
				.moves()[(i + 2) % 4] = false;
		}
	}
	
	private int getPreferredStart() {
		//TODO: Add whiskers
		Position flagPosition = getFlagPosition();
		if (flagPosition != null) {
			return getMoveTowardsFlag(flagPosition);
		}
		
		return getOptimalWhiskerSide(8, 1);
	}
	
	

	private int getOptimalWhiskerSide(int distance, int radius) {
		int mostUnexploredSide = 0;
		int musScore = 0;
		for (int i = 0; i < 4; i++) {
			int sideScore = computeWhiskerScore(DIRECTIONS[i], distance, radius);
			if (sideScore > musScore) {
				mostUnexploredSide = i;
				musScore = sideScore;
			}
		}
		
		return mostUnexploredSide;
	}

	private int computeWhiskerScore(Direction direction, int distance, int radius) {
		// Check reachable
		outerLoop: for (int i = 1; i < distance; i++) {
			Position rawPos = getPositionAwayFromPlayer(direction, i);
			for (int j = -1; j <= 1; j++) {
				Position pos = rawPos.plus(
					// Swapping col and row is a simple way to check perpendicular tiles
					direction.getTP().col() * j,
					direction.getTP().row() * j
				);
			
				MazeCell cell = mazeCache.getCell(pos);
				if (cell == null || cell.cellType() != CellType.WLL) {
					continue outerLoop;
				}
			}
			
			return 0;
		}
		
		// Compute score
		Position whiskerCenter = getPositionAwayFromPlayer(direction, distance);
		int score = 0;
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				Position pos = whiskerCenter.plus(x, y);
				MazeCell cell = mazeCache.getCell(pos);
				if (cell == null || cell.cellType() == CellType.UNK) {
					score++;
				}
			}
		}
		return score;
	}
	
	private Position getPositionAwayFromPlayer(Direction direction, int distance) {
		return playerPosition.plus(
			direction.getTP().row() * distance,
			direction.getTP().col() * distance
		);
	}

	private int getMoveTowardsFlag(Position flagPosition) {
		if (playerPosition.row() < flagPosition.row()) {
			return 0;
		} else if (playerPosition.row() > flagPosition.row()) {
			return 2;
		} if (playerPosition.col() < flagPosition.col()) {
			return 1;
		} else {
			return 3;
		}
	}

	private Position move(Position position, Direction direction) {
		return position.plus(direction.getTP());
	}
	
}
