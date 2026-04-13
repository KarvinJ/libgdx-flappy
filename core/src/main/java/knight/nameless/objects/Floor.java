package knight.nameless.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Floor extends GameObject {

    public Floor(Rectangle bounds, Texture texture) {
        super(bounds, texture);
    }

    public void update(float deltaTime) {

        actualBounds.x -= 150 * deltaTime;

        if (actualBounds.x <= -473)
            actualBounds.setPosition(480, 0);
    }
}
