package knight.nameless.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import knight.nameless.Flappy;
import knight.nameless.helpers.GameDataHelper;
import knight.nameless.objects.Floor;
import knight.nameless.objects.Pipe;
import knight.nameless.objects.Player;

import java.util.Iterator;

public class GameScreen extends ScreenAdapter {

    private final Flappy game;
    private final OrthographicCamera camera;
    public SpriteBatch batch;
    private final Texture background;
    private final Player player;
    private final Array<Pipe> pipes;
    private final Floor floor;
    private final Floor floor2;
    private final Floor floor3;
    private final TextureAtlas numbersAtlas;
    private TextureRegion scoreNumbers;
    private TextureRegion scoreNumbersUnits;
    private final Rectangle scoreBounds;
    private final Texture startGame;
    private int score;
    private long lastPipeSpawnTime;
    private final Sound pointSound;

    public GameScreen() {

        game = Flappy.INSTANCE;

        game.isGameOver = false;

        camera = game.camera;

        batch = new SpriteBatch();

        player = new Player(new Vector2(game.screenWidth / 2f, game.screenHeight / 2f));

        pipes = new Array<>();

        floor = new Floor(new Rectangle(0, 0, game.screenWidth, 80));
        floor2 = new Floor(new Rectangle(game.screenWidth, 0, game.screenWidth, 80));
        floor3 = new Floor(new Rectangle(game.screenWidth, 0, game.screenWidth, 80));

        background = new Texture("images/background-day.png");
        startGame = new Texture("images/message.png");

        numbersAtlas = new TextureAtlas("images/numbers.atlas");

        scoreNumbers = numbersAtlas.findRegion(String.valueOf(score));
        scoreNumbersUnits = numbersAtlas.findRegion(String.valueOf(score));

        scoreBounds = new Rectangle(
            game.screenWidth / 2f, 500, scoreNumbers.getRegionWidth(), scoreNumbers.getRegionHeight()
        );

        pointSound = Gdx.audio.newSound(Gdx.files.internal("sounds/point.wav"));
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height);
    }

    private void generatePipes() {

        float upPipePosition = MathUtils.random(320, game.screenHeight - 120);

        Pipe upPipe = new Pipe(new Rectangle(game.screenWidth, upPipePosition, 64, 320), true);

        // gap size = 120.
        float downPipePosition = upPipePosition - upPipe.actualBounds.height - 120;

        Pipe downPipe = new Pipe(new Rectangle(game.screenWidth, downPipePosition, 64, 320), false);

        pipes.add(upPipe, downPipe);

        lastPipeSpawnTime = TimeUtils.nanoTime();
    }

    private void update(float deltaTime) {

        player.update(deltaTime);

        if (TimeUtils.nanoTime() - lastPipeSpawnTime > 2000000000)
            generatePipes();

        for (Iterator<Pipe> pipesIterator = pipes.iterator(); pipesIterator.hasNext(); ) {

            Pipe pipe = pipesIterator.next();

            pipe.update(deltaTime);

            player.hasCollide(pipe);

            if (!pipe.isBehind && pipe.actualBounds.x < player.actualBounds.x) {

                pipe.isBehind = true;

                if (pipe.actualBounds.y < player.actualBounds.y) {

                    score++;
                    pointSound.play();
                }
            }

            if (pipe.actualBounds.x < -64) {

                pipesIterator.remove();
                pipe.dispose();
            }
        }

        floor.update(deltaTime);
        floor2.update(deltaTime);

        player.hasCollide(floor);
        player.hasCollide(floor2);

        if (score < 10)
            scoreNumbers = numbersAtlas.findRegion(String.valueOf(score));
        else {

            scoreNumbers = numbersAtlas.findRegion(String.valueOf(Integer.parseInt(("" + score).substring(0, 1))));
            scoreNumbersUnits = numbersAtlas.findRegion(String.valueOf(Integer.parseInt(("" + score).substring(1, 2))));
        }
    }

    @Override
    public void render(float deltaTime) {

        ScreenUtils.clear(0, 0, 0, 0);

        if (!game.isGameOver)
            update(deltaTime);

        else if (Gdx.input.isTouched()) {

            GameDataHelper.saveHighScore(score);
            game.setScreen(new GameScreen());
        }

        draw();
    }

    private void draw() {

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(background, 0, 0, game.screenWidth, game.screenHeight);

        for (Pipe pipe : pipes)
            pipe.draw(batch);

        floor3.draw(batch);
        floor.draw(batch);
        floor2.draw(batch);

        player.draw(batch);

        batch.draw(scoreNumbers, scoreBounds.x, scoreBounds.y, scoreBounds.width, scoreBounds.height);

        if (score > 9) {
            batch.draw(
                scoreNumbersUnits, scoreBounds.x + 25,
                scoreBounds.y, scoreBounds.width, scoreBounds.height
            );
        }

        if (game.isGameOver)
            batch.draw(startGame, 1, 1, game.screenWidth, game.screenHeight);

        batch.end();
    }

    @Override
    public void hide() {
//        Dispose fails in web build
//        dispose();
    }

    @Override
    public void dispose() {

        player.dispose();
        background.dispose();
        batch.dispose();

        scoreNumbers.getTexture().dispose();
        scoreNumbersUnits.getTexture().dispose();
    }
}
