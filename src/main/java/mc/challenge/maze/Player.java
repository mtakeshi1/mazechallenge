package mc.challenge.maze;

/**
 * Maybe in the future this will have more that only 'position' for other challenges.
 */
public class Player {

    private Position position;

    public Player(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }


}
