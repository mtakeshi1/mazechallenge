package mc.everyos;

import mc.challenge.maze.Position;

public class MazeCache {
	
	private BidirectionalList<BidirectionalList<MazeCell>> matrix = new BidirectionalList<>();

	public void setCell(Position position, MazeCell cell) {
		matrix
			.getOrSet(position.row(), () -> new BidirectionalList<>())
			.set(position.col(), cell);
	}
	
	public MazeCell getCell(Position position) {
		BidirectionalList<MazeCell> row = matrix.get(position.row());
		if (row == null) {
			return null;
		}
		return row.get(position.col());
	}
	
}
