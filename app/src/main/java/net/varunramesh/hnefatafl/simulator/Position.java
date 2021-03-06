package net.varunramesh.hnefatafl.simulator;

import com.annimon.stream.Stream;

import java.io.Serializable;

/**
 * Created by varunramesh on 7/22/15.
 */
public final class Position implements Serializable {
    /** Static cache of the values of the Direction enum. */
    private static final Direction[] directions = Direction.values();

    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public Position getNeighbor(Direction dir) {
        switch (dir) {
            case UP:
                return new Position(x, y + 1);
            case DOWN:
                return new Position(x, y - 1);
            case LEFT:
                return new Position(x - 1, y);
            case RIGHT:
                return new Position(x + 1, y);
        }
        throw new UnsupportedOperationException();
    }

    /** Returns the manhattan distance to another position */
    public int distanceTo(Position other) {
        return Math.abs(other.x - x) + Math.abs(other.y - y);
    }

    /** Returns true if a position is the neighbor of the current position. */
    public boolean isNeighbor(Position other) { return distanceTo(other) == 1; }

    /** Return a stream containing the positions neighboring this point. */
    public Stream<Position> getNeighborStream() {
        return Stream.of(directions).map((Direction dir) -> getNeighbor(dir));
    }

    public Direction directionTo(Position other) {
        if (x == other.x) {
            if (other.y > y) return Direction.UP;
            else return Direction.DOWN;
        } else if (y == other.y) {
            if (other.x > x) return Direction.RIGHT;
            else return Direction.LEFT;
        } else {
            throw new UnsupportedOperationException("Diagonal directions not supported.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (x != position.x) return false;
        return y == position.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
