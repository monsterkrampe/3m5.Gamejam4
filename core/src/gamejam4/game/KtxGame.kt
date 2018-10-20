package gamejam4.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import java.util.*


class GameplayScreen : KtxScreen {
    private val playerSprite = Texture("player.png")
    private val player = Player(playerSprite, 2f)
    private val zombieManager = ZombieManager()
    private val groundSprite = Sprite(Texture("ground/default.png"))

    private val viewport = ExtendViewport(20f, 10f)
    private val stage = Stage(viewport).apply {
        // static filling will be replaced with dynamic stuff later on
        for (x in 0..viewport.worldWidth.toInt()) {
            for (y in 0..viewport.worldHeight.toInt()) {
                addActor(FloorTile(groundSprite, x.toFloat(), y.toFloat()))
            }
        }
    }

    init {
        stage.addActor(player)
    }

    private fun update(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit()

        val randomFloat = Random().nextFloat()

        if (randomFloat < 0.02 && stage.actors.filter{it is Zombie}.size < 10) {
            // should spawn Zombie near player
            stage.addActor(zombieManager.spawnZombieNear(10f, 5f))
        }

        zombieManager.attack(0f, 0f)

        stage.act(delta)

        handleInput(delta)
        stage.camera.position.set(player.x, player.y , stage.camera.position.z)
    }

    private fun draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.draw()

        // maybe for gui drawing later
//        batch.use {
//            font.draw(it, "Hello Kotlin!", 100f, 100f)
//            it.draw(playerSprite, 300f, 300f)
//            zombieManager.drawZombies(batch)
//        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        update(delta)
        draw()
    }

    private fun handleInput(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            player.y += delta * player.speed

        if (Gdx.input.isKeyPressed(Input.Keys.A))
            player.x -= delta * player.speed

        if (Gdx.input.isKeyPressed(Input.Keys.S))
            player.y -= delta * player.speed

        if (Gdx.input.isKeyPressed(Input.Keys.D))
            player.x += delta * player.speed
    }

    override fun dispose() {
        stage.dispose()
    }
}

class TheGame : KtxGame<Screen>() {
    override fun create() {
        addScreen(GameplayScreen())
        setScreen<GameplayScreen>()
    }
}
