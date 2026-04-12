package knight.nameless.objects;

import com.badlogic.gdx.math.Rectangle;

public class Pipe extends GameObject {

    public boolean isBehind;

    public Pipe(Rectangle bounds, boolean isRotated) {
        super(bounds, isRotated ? "pipe-green-180.png" : "pipe-green.png", "die.wav");
    }

    public void update(float deltaTime) {
        actualBounds.x -= 150 * deltaTime;
    }
}
