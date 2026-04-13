package knight.nameless.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {

    public final Rectangle actualBounds;
    protected TextureRegion actualRegion;

    protected GameObject(Rectangle bounds, String spritePath) {
        actualBounds = bounds;
        actualRegion = new TextureRegion(new Texture("images/" + spritePath));
    }

    public void draw(Batch batch) {

        batch.draw(actualRegion, actualBounds.x, actualBounds.y, actualBounds.width, actualBounds.height);
    }

    public void draw(ShapeRenderer shapeRenderer) {

        shapeRenderer.rect(actualBounds.x, actualBounds.y, actualBounds.width, actualBounds.height);
    }

    public void dispose() {
        actualRegion.getTexture().dispose();
    }
}
