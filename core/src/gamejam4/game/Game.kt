package gamejam4.game

import ktx.app.KtxApplicationAdapter
import ktx.graphics.*
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.clearScreen

class Game : KtxApplicationAdapter {
    private lateinit var batch: SpriteBatch
    private lateinit var img: Texture

    override fun create() {
        batch = SpriteBatch()
        img = Texture("badlogic.jpg")
    }

    override fun render() {
        clearScreen(1f, 0f, 0f, 1f)
        batch.use {
            it.draw(img, 0f, 0f)
        }
    }

    override fun dispose() {
        batch.dispose()
        img.dispose()
    }
}
