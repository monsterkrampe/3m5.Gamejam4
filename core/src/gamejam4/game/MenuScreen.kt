package gamejam4.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxScreen
import ktx.graphics.use

class MenuScreen(val game: TheGame) : KtxScreen, InputProcessor {

    val items = ZipList(listOf(
            MenuItem("Start") { it.start() },
            MenuItem("Quit") {Gdx.app.exit()}
    ))

    private val batch = SpriteBatch().apply {
        color = Color.WHITE
    }
    private val font = BitmapFont()

    init {
        Gdx.input.inputProcessor = this
    }

    override fun render(delta: Float) {

        batch.use {
            font.data.setScale(1.5f)

            for ((index, item) in items.withIndex()) {
                val text = if (item == items.current)
                    item.text.padEnd(8) + " >"
                else
                    item.text

                font.draw(it, text, 90f, Gdx.graphics.height * 0.6f - index * 40f)
            }

            font.data.setScale(1.0f)

            font.draw(it, "Selection [W] and [S] or Up and Down arrow keys, [Enter] start or quit", 60f, 40f)
            font.draw(it, "Gameplay [W] [A] [S] {D] for movement, [LButton} for shooting", 60f, 25f)
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W, Input.Keys.UP -> items.backward()
            Input.Keys.S, Input.Keys.DOWN -> items.forward()
            Input.Keys.ENTER -> items.current.callback.invoke(game)
        }
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false

    override fun scrolled(amount: Int): Boolean = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

    override fun keyTyped(character: Char): Boolean = false

    override fun keyUp(keycode: Int): Boolean = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false
}

data class MenuItem(val text: String, val callback: (TheGame) -> Unit)