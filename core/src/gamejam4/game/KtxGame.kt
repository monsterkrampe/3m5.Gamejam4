package gamejam4.game

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use
import java.util.*

class GameplayScreen : KtxScreen {
    private val playerSprite = Texture("player.png")
    private val zombieManager = ZombieManager()
    private val groundSprite = Texture("ground/default.png")

    private val font = BitmapFont()
    private val batch = SpriteBatch().apply {
        color = Color.WHITE
    }

    override fun render(delta: Float) {
        val randomFloat = Random().nextFloat()

        if (randomFloat < 0.1 && zombieManager.zombies.size < 10) {
            zombieManager.spawnZombieNear(Pair(1000f, 600f))
        }

        batch.use {
            font.draw(it, "Hello Kotlin!", 100f, 100f)
            it.draw(playerSprite, 300f, 300f)
            zombieManager.drawZombies(batch)
        }
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
