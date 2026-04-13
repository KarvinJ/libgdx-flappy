package knight.nameless.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Pipe extends GameObject {

    public boolean isBehind;

    public Pipe(Rectangle bounds, Texture texture) {
        super(bounds, texture);
    }

    public void update(float deltaTime) {
        actualBounds.x -= 150 * deltaTime;
    }

    public Rectangle getCollisionBounds() {

        if (actualBounds.height < 0) {

            //need to adjust the collision bounds given that the height of the inverted pipe is negative
            //I need to convert it to positive and subtract this height to the y position to have correct collision
            // between the player and this pipe
            var actualHeight = (actualBounds.height) * -1;
            return new Rectangle(actualBounds.x, actualBounds.y - actualHeight, actualBounds.width, actualHeight);
        }
        else
            return actualBounds;
    }
}
