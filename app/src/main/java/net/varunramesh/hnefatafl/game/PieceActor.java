package net.varunramesh.hnefatafl.game;

import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.varunramesh.hnefatafl.simulator.Piece;
import net.varunramesh.hnefatafl.simulator.Position;

/**
 * Created by varunramesh on 7/27/15.
 */
public class PieceActor extends Actor implements LayerActor {
    private final String TAG = "PieceActor";

    private final TextureRegion region;
    private final Texture texture;

    private Position boardPosition;
    private final HnefataflGame game;

    public PieceActor(HnefataflGame game, Piece.Type type, Position boardPos){
        this.game = game;

        switch(type) {
            case KING:
                texture = game.getTexture("king.png");
                break;
            case DEFENDER:
                texture = game.getTexture("defender.png");
                break;
            case ATTACKER:
                texture = game.getTexture("attacker.png");
                break;
            default:
                throw new UnsupportedOperationException();
        }

        region = new TextureRegion(texture);
        this.boardPosition = boardPos;

        setWidth(texture.getWidth());
        setHeight(texture.getHeight());
        setWorldPosition(game.toWorldPosition(boardPos));


        final PieceActor piece = this;
        this.addListener(new InputListener() {
            public static final float DRAG_THRESHOLD = BoardActor.SQUARE_SIZE / 2.0f;
            private final Vector2 touchStartPosition = new Vector2();
            private boolean dragging = false;
            private boolean newSelection = false;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (game.getMoveState() != HnefataflGame.MoveState.SELECT_MOVE) return false;

                Log.d(TAG, boardPosition.toString() + " touched...");

                if(!game.isSelected(piece)) {
                    newSelection = true;
                    game.selectPiece(piece);
                } else {
                    newSelection = false;
                }

                event.toCoordinates(piece.getParent(), touchStartPosition);
                dragging = false;
                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if(dragging) {
                    piece.slideTo(boardPosition);
                } else {
                    if(!newSelection) game.clearMoveSelectors();
                }
            }

            @Override
            public void touchDragged (InputEvent event, float x, float y, int pointer) {
                Vector2 worldPosition = new Vector2();
                event.toCoordinates(piece.getParent(), worldPosition);

                if(!dragging) {
                    if(touchStartPosition.dst(worldPosition) > DRAG_THRESHOLD)
                        dragging = true;
                } else
                    setWorldPosition(worldPosition);
            }
        });
    }

    public void slideTo(Position newPosition) {
        boardPosition = newPosition;
        Vector2 worldPosition = game.toWorldPosition(newPosition);
        worldPosition.sub(getWidth() / 2.0f, 87.0f);

        MoveToAction action = Actions.action(MoveToAction.class);
        action.setPosition(worldPosition.x, worldPosition.y);
        action.setDuration(1.0f);
        action.setInterpolation(Interpolation.pow3Out);

        addAction(action);
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    public void setWorldPosition(Vector2 worldPosition) {
        setX(worldPosition.x - getWidth() / 2.0f);
        setY(worldPosition.y - 87.0f);
    }

    @Override
    public int getLayer() {
        return 12 - boardPosition.getY();
    }

    public Position getPosition() {
        return boardPosition;
    }
}