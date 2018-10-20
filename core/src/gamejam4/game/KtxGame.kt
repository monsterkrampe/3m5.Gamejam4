package gamejam4.game

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use
import java.util.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20


class GameplayScreen : KtxScreen {
    private val playerSprite = Texture("player.png")
    private val zombieManager = ZombieManager()
    private val groundSprite = Texture("ground/default.png")

    private val stage = Stage(ExtendViewport(20f, 10f))

    private val font = BitmapFont()
    private val batch = SpriteBatch().apply {
        color = Color.WHITE
    }

    private fun update(delta: Float) {
        val randomFloat = Random().nextFloat()

        if (randomFloat < 0.1 && zombieManager.zombies.size < 10) {
            zombieManager.spawnZombieNear(Pair(1000f, 600f))
        }

        stage.act(delta)
    }

    private fun draw(delta: Float) {
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // will be deleted later on
        batch.use {
            font.draw(it, "Hello Kotlin!", 100f, 100f)
            it.draw(playerSprite, 300f, 300f)
            zombieManager.drawZombies(batch)
        }

        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        update(delta)
        draw(delta)
    }

    override fun dispose() {
        font.dispose()
        batch.dispose()
    }
}

class TheGame : KtxGame<Screen>() {
    override fun create() {
        addScreen(GameplayScreen())
        setScreen<GameplayScreen>()
    }
}
