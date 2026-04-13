package knight.nameless.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends GameObject {

    private final Vector2 initialPosition;
    private float animationTimer;
    private final Animation<TextureRegion> flappingAnimation;
    private float gravity = 0;
    private final float impulse = 25000;
    private final float gravityIncrement = -400;

    public Player(float positionX, float positionY) {
        super(
            new Rectangle(positionX, positionY, 50, 40),
            "yellowbird-midflap.png", "wing.wav"
        );

        initialPosition = new Vector2(positionX, positionY);

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
        gravity += gravityIncrement * deltaTime;

        if (Gdx.input.justTouched()) {

            actionSound.play();
            gravity = impulse * deltaTime;
        }
    }

    public void resetPlayerState() {

        actualBounds.setPosition(initialPosition);
        gravity = 0;
    }

    public boolean hasCollide(Rectangle collisionBounds){

        return actualBounds.overlaps(collisionBounds);
    }
}
