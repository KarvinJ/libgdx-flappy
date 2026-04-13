package knight.nameless;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import knight.nameless.helpers.GameDataHelper;
import knight.nameless.objects.Floor;
import knight.nameless.objects.Pipe;
import knight.nameless.objects.Player;

import java.util.Iterator;

public class Flappy extends ApplicationAdapter {

    public OrthographicCamera camera;
    public FitViewport viewport;
    public final int SCREEN_WIDTH = 480;
    public final int SCREEN_HEIGHT = 640;
    public boolean isGameOver;
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    private Texture background;
    private Player player;
    private Array<Pipe> pipes;
    private Array<Floor> floors;
    private Floor backFloor;
    private TextureAtlas numbersAtlas;
    private TextureRegion scoreNumbers;
    private TextureRegion scoreNumbersUnits;
    private Rectangle scoreBounds;
    private Texture startGame;
    private int score;
    private long lastPipeSpawnTime;
    private Sound pointSound;
    private Sound dieSound;
    private Sound flapSound;
    private boolean isDebugMode = true;

    @Override
    public void create() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        pipes = new Array<>();
        floors = new Array<>();

        floors.add(
            new Floor(new Rectangle(0, 0, SCREEN_WIDTH, 80)),
            new Floor(new Rectangle(SCREEN_WIDTH, 0, SCREEN_WIDTH, 80))
        );

        backFloor = new Floor(new Rectangle(0, 0, SCREEN_WIDTH, 80));

        background = new Texture("images/background-day.png");
        startGame = new Texture("images/message.png");

        numbersAtlas = new TextureAtlas("images/numbers.atlas");

        scoreNumbers = numbersAtlas.findRegion(String.valueOf(score));
        scoreNumbersUnits = numbersAtlas.findRegion(String.valueOf(score));

        scoreBounds = new Rectangle(
            SCREEN_WIDTH / 2f, 500, scoreNumbers.getRegionWidth(), scoreNumbers.getRegionHeight()
        );

        pointSound = Gdx.audio.newSound(Gdx.files.internal("sounds/point.wav"));
        dieSound = Gdx.audio.newSound(Gdx.files.internal("sounds/die.wav"));
        flapSound = Gdx.audio.newSound(Gdx.files.internal("sounds/wing.wav"));

        player = new Player(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, flapSound);

        camera = new OrthographicCamera();
        camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);

        //a FitViewPort is better when the resolution or your game is lower than the device.
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void generatePipes() {

        var pipeSpriteHeight = 320;

        float upPipePosition = MathUtils.random(SCREEN_HEIGHT, SCREEN_HEIGHT + 180);

        Pipe upPipe = new Pipe(new Rectangle(SCREEN_WIDTH, upPipePosition, 64, -pipeSpriteHeight));

        // gap size = 100.
        float downPipePosition = upPipePosition - (pipeSpriteHeight * 2) - 100;

        Pipe downPipe = new Pipe(new Rectangle(SCREEN_WIDTH, downPipePosition, 64, pipeSpriteHeight));

        pipes.add(upPipe, downPipe);

        lastPipeSpawnTime = TimeUtils.nanoTime();
    }

    private void update(float deltaTime) {

        player.update(deltaTime);

        if (player.actualBounds.y > 700)
            isGameOver = true;

        if (TimeUtils.nanoTime() - lastPipeSpawnTime > 2000000000)
            generatePipes();

        for (Iterator<Pipe> pipesIterator = pipes.iterator(); pipesIterator.hasNext(); ) {

            Pipe pipe = pipesIterator.next();

            pipe.update(deltaTime);

            var hasCollide = player.hasCollide(pipe.getCollisionBounds());

            if (hasCollide) {

                dieSound.play();
                isGameOver = true;
                break;
            }

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

        for (Floor floor : floors) {

            floor.update(deltaTime);

            if (player.hasCollide(floor.actualBounds)) {

                dieSound.play();
                isGameOver = true;
                break;
            }
        }

        if (score < 10)
            scoreNumbers = numbersAtlas.findRegion(String.valueOf(score));
        else {

            scoreNumbers = numbersAtlas.findRegion(String.valueOf(Integer.parseInt(("" + score).substring(0, 1))));
            scoreNumbersUnits = numbersAtlas.findRegion(String.valueOf(Integer.parseInt(("" + score).substring(1, 2))));
        }
    }

    @Override
    public void render() {

        ScreenUtils.clear(0, 0, 0, 0);

        float deltaTime = Gdx.graphics.getDeltaTime();

        if (!isGameOver)
            update(deltaTime);

        else if (Gdx.input.isTouched()) {

            GameDataHelper.saveHighScore(score);
            resetGame();
        }

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.F1))
            isDebugMode = !isDebugMode;

        if (isDebugMode)
            debugDraw();
        else
            draw();
    }

    private void resetGame() {

        player.resetPlayerState();
        score = 0;
        lastPipeSpawnTime = 0;
        isGameOver = false;
        pipes.clear();
    }

    private void draw() {

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        batch.draw(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        for (Pipe pipe : pipes)
            pipe.draw(batch);

        backFloor.draw(batch);

        for (Floor floor : floors)
            floor.draw(batch);

        player.draw(batch);

        batch.draw(scoreNumbers, scoreBounds.x, scoreBounds.y, scoreBounds.width, scoreBounds.height);

        if (score > 9) {
            batch.draw(
                scoreNumbersUnits, scoreBounds.x + 25,
                scoreBounds.y, scoreBounds.width, scoreBounds.height
            );
        }

        if (isGameOver)
            batch.draw(startGame, 1, 1, SCREEN_WIDTH, SCREEN_HEIGHT);

        batch.end();
    }

    private void debugDraw() {

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.GREEN);

        for (Pipe pipe : pipes)
            pipe.draw(shapeRenderer);

        shapeRenderer.setColor(Color.WHITE);

        backFloor.draw(shapeRenderer);

        shapeRenderer.setColor(Color.YELLOW);
        player.draw(shapeRenderer);

        shapeRenderer.end();
    }

    @Override
    public void dispose() {

        player.dispose();
        background.dispose();
        batch.dispose();
        shapeRenderer.dispose();

        scoreNumbers.getTexture().dispose();
        scoreNumbersUnits.getTexture().dispose();

        backFloor.dispose();
        dieSound.dispose();
        pointSound.dispose();
        flapSound.dispose();

        for (Floor floor : floors)
            floor.dispose();

        for (Pipe pipe : pipes)
            pipe.dispose();
    }
}
