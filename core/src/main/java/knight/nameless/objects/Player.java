package knight.nameless.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import knight.nameless.Flappy;

public class Player extends GameObject {

    private float animationTimer;
    private final Animation<TextureRegion> flappingAnimation;
    private float gravity = 0;

    public Player(Vector2 position) {
        super(
            new Rectangle(position.x, position.y, 50, 40),
            "yellowbird-midflap.png", "wing.wav"
        );

        TextureRegion region = new TextureAtlas("images/birds.atlas").findRegion("yellow-bird");

        int regionWidth = region.getRegionWidth() / 3;

        flappingAnimation = makeAnimationByRegion(region, regionWidth, region.getRegionHeight());
    }

    private Animation<TextureRegion> makeAnimationByRegion(TextureRegion region, int regionWidth, int regionHeight) {

        Array<TextureRegion> animationFrames = new Array<>();

        for (int i = 0; i < 3; i++)
            animationFrames.add(new TextureRegion(region, i * regionWidth, 0, regionWidth, regionHeight));

        return new Animation<>(0.1f, animationFrames);
    }

    public void update(float deltaTime) {

        animationTimer += deltaTime;

        actualRegion = flappingAnimation.getKeyFrame(animationTimer, true);

        actualBounds.y += gravity * deltaTime;
        gravity += -400 * deltaTime;

        if (Gdx.input.justTouched()) {

            actionSound.play();
            gravity = 20000 * deltaTime;
        }

        if (actualBounds.y > 700)
            Flappy.INSTANCE.isGameOver = true;
    }

    public void hasCollide(GameObject collisionObject){

        if (actualBounds.overlaps(collisionObject.actualBounds)) {

            collisionObject.actionSound.play();
            Flappy.INSTANCE.isGameOver = true;
        }
    }
}
