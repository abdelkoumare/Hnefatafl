package net.varunramesh.hnefatafl.simulator;

import com.annimon.stream.function.Consumer;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.UUID;

/**
 * Created by varunramesh on 7/22/15.
 */
public class GameState implements Serializable {
    /** A GameType instance */
    private final GameType type;

    /** The entire board history of the match */
    private final Deque<Board> boards = new ArrayDeque<Board>();

    /** The action history of the match */
    private final Deque<Action> actions = new ArrayDeque<Action>();

    /** A unique id to reference this game */
    private final UUID uuid;
    public UUID getUUID() { return uuid; }

    /** The date when the game was created */
    private final Date createdDate;
    public Date getCreatedDate() { return createdDate; }

    /** The date when the last move was made. */
    private Date lastMoveDate;
    public Date getLastMoveDate() { return lastMoveDate; }

    /** A function that can be produced */
    private transient Persister persister;
    public void setPersister(Persister persister) { this.persister = persister; }

    public GameState(GameType type) {
        this.type = type;
        this.boards.push(new Board());
        this.uuid = UUID.randomUUID();

        this.createdDate = new Date();
        this.lastMoveDate = new Date();
    }

    /** Returns true if no moves have been made yet */
    public boolean isFirstMove() { return actions.size() == 0; }

    /** The current configuration of the board */
    public Board currentBoard() { return boards.peek(); }

    public void pushBoard(Action action, Board board) {
        this.actions.push(action);
        this.boards.push(board);
        this.lastMoveDate = new Date();

        // Try to persist the game state.
        if(this.persister != null) {
            this.persister.persist(this);
        }
    }

    /** Get the GameType of this GameState */
    public GameType getType() { return type; }
}