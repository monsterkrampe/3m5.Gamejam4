package gamejam4.game

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use

class GameplayScreen : KtxScreen {
    private val font = BitmapFont()
    private val batch = SpriteBatch().apply {
        color = Color.WHITE
    }

    override fun render(delta: Float) {
        batch.use {
            font.draw(it, "Hello Kotlin!", 100f, 100f)
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
