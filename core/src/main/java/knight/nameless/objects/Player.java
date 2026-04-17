package knight.nameless.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends GameObject {

    private Sprite sprite;
    private final Vector2 initialPosition;
    private float animationTimer;
    private final Animation<TextureRegion> flappingAnimation;
    private float gravity = 0;
    private final Sound actionSound;
    private float startGameTimer;
    boolean shouldRotateUp;
    float downRotationTimer = 0;
    float upRotationTimer = 0;
    float initialAngle = 0;

    public Player(float positionX, float positionY, Sound sound, Texture texture) {
        super(
            new Rectangle(positionX, positionY, texture.getWidth(), texture.getHeight()),
            texture
        );

        initialPosition = new Vector2(positionX, positionY);
        actionSound = sound;

        TextureRegion region = new TextureAtlas("images/birds.atlas").findRegion("yellow-bird");

        sprite = new Sprite(region);
        sprite.setPosition(positionX, positionY);

        flappingAnimation = makeAnimationByRegion(region, 3);
    }

    private Animation<TextureRegion> makeAnimationByRegion(TextureRegion region, int totalFrames) {

        int regionWidth = region.getRegionWidth() / totalFrames;

        Array<TextureRegion> animationFrames = new Array<>();

        for (int i = 0; i < totalFrames; i++)
            animationFrames.add(new TextureRegion(region, i * regionWidth, 0, regionWidth, region.getRegionHeight()));

        return new Animation<>(0.1f, animationFrames);
    }

    public void update(float deltaTime) {

        animationTimer += deltaTime;

        actualRegion = flappingAnimation.getKeyFrame(animationTimer, true);

        startGameTimer += deltaTime;

        if (startGameTimer < 0.5)
            return;

        handleRotation(deltaTime);

        actualBounds.y += gravity * deltaTime;

        float gravityIncrement = -400;
        gravity += gravityIncrement * deltaTime;

        if (Gdx.input.justTouched()) {

            actionSound.play();

            float impulse = 25000;
            gravity = impulse * deltaTime;

            shouldRotateUp = true;
            upRotationTimer = 1;
            downRotationTimer = 0;
            initialAngle = 20;
        }
    }

    public void drawWithRotation(SpriteBatch batch) {

        sprite.setRegion(actualRegion);
        sprite.setBounds(actualBounds.x, actualBounds.y, actualBounds.width, actualBounds.height);

        //set origin fix the rotation and now the rotation is in sync with the sprite position.
        sprite.setOrigin(0, 0);
        sprite.setRotation(initialAngle);
        sprite.draw(batch);
    }

    private void handleRotation(float deltaTime) {

        if (startGameTimer > 0.5) {

            if (shouldRotateUp) {

                if (upRotationTimer > 0)
                    upRotationTimer -= deltaTime;

                if (upRotationTimer <= 0)
                    shouldRotateUp = false;
            }

            downRotationTimer += deltaTime;

            if (downRotationTimer > 0.5f && initialAngle >= -90)
                initialAngle -= 100 * deltaTime;
        }
    }

    public void resetPlayerState() {

        actualBounds.setPosition(initialPosition);
        gravity = 0;
        startGameTimer = 0;
        shouldRotateUp = false;
        upRotationTimer = 0;
        downRotationTimer = 0;
        initialAngle = 0;
    }

    public boolean hasCollide(Rectangle collisionBounds) {
        return actualBounds.overlaps(collisionBounds);
    }
}
