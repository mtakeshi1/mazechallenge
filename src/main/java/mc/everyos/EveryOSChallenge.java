	package mc.everyos;

import java.util.Arrays;

import mc.Configuration;
import mc.challenge.Challenge;
import mc.challenge.maze.Direction;
import mc.challenge.maze.HeadlessMain;
import mc.challenge.maze.Maze.CellType;
import mc.challenge.maze.MazeFactory;
import mc.challenge.maze.Position;

/**
 * The whole challenge can be completed by just adding to this file.
 * You can create other java files in the same package though.
 * <p>
 * Run this:
 * Graphically -> {@link mc.GraphicalLauncher} ( for debug purposes )
 * Non graphically ->  {@link mc.HeadlessLauncher}
 * <p>
 * Configure and choose what type of mazes are ran : {@link mc.Configuration}
 */
public class EveryOSChallenge implements Challenge {
	
	private static final Position LOS_CENTER = new Position(6, 6);
	
	private final MazeCache mazeCache = new MazeCache();
	private final MazeSolver mazeSolver;
	
	private Position playerPosition = new Position(0, 0);
	private Position flagPosition;
	
	public EveryOSChallenge() {
		mazeCache.setCell(playerPosition, createCell(CellType.SRT));
		mazeSolver = new MazeSolver(mazeCache, playerPosition) {
			@Override
			protected Position getFlagPosition() {
				return flagPosition;
			}
		};
	}

	/**
	 * This method will be called on init and after each move.
	 *
	 * @param los [13,13] array where 'you' always are at [6,6] ( further explained in printLOSUpdate )
	 */
	@Override
	public void handleLineOfSightUpdate(CellType[][] los) {
		//printLOSUpdate(los); // Uncomment this for debug printing the array
		integrateLineOfSight(los);
		eliminateDeadPaths();
	}

	/**
	 * This method will be called before each move.
	 * Here you must supply the program with a 'Direction', an enum that can be: NORTH, SOUTH, EAST or WEST
	 * <p>
	 * 'You' will walk in that direction unless there is a wall there.
	 */
	@Override
	public Direction getMove() {
		Direction move = mazeSolver.getMove();
		playerPosition = move(playerPosition, move);
		
		return move;
	}
	
	private void integrateLineOfSight(CellType[][] los) {
		for (int x = -6; x <= 6; x++) {
			for (int y = -6; y <= 6; y++) {
				integrateTile(los, new Position(x, y));
			}
		}
	}

	private void integrateTile(CellType[][] los, Position direction) {
		Position tilePos = playerPosition.plus(direction);
		Position losPos = LOS_CENTER.plus(direction);
		
		MazeCell oldCell = mazeCache.getCell(tilePos);
		if (oldCell == null || oldCell.cellType() == CellType.UNK) {
			CellType cellType = los[losPos.row()][losPos.col()];
			mazeCache.setCell(tilePos, createCell(cellType));
			if (cellType == CellType.FSH) {
				flagPosition = tilePos;
			}
		}
	}

	private MazeCell createCell(CellType cellType) {
		switch(cellType) {
			case FLR:
			case FSH:
			case SRT:
				return new MazeCell(cellType, new boolean[] {true, true, true, true});
			case UNK:
			case WLL:
			default:
				return new MazeCell(cellType, new boolean[4]);
		}
	}
	
	private void eliminateDeadPaths() {
		MazeSolver futureSolver = new MazeSolver(mazeCache, playerPosition) {
			@Override
			protected boolean excludeCell(MazeCell cell) {
				return
					cell.cellType() == CellType.FSH ||
					cell.cellType() == CellType.UNK;
			}

			@Override
			protected Position getFlagPosition() {
				return flagPosition;
			}
		};
		
		while (futureSolver.getMove() != null);
		futureSolver.clearCurrentPath();
	}

	private Position move(Position position, Direction direction) {
		return position.plus(direction.getTP());
	}


	//I put a convenience launcher here in case you want to run a single maze headless.
	public static void main(String[] args) {
		new HeadlessMain(new EveryOSChallenge(),
//				MazeFactory.getFlowingCave(
//				MazeFactory.get1WMap(
				MazeFactory.getDungeon(
						Configuration.SMALL
//						Configuration.MEDIUM
//						Configuration.LARGE
//						Configuration.HUGE
				)
		).doAllMoves();
	}


	//Just here to show explain how the Line Of Sight array is built.
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
