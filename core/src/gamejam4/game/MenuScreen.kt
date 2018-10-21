package gamejam4.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxScreen
import ktx.graphics.use

class MenuScreen(val game: TheGame) : KtxScreen, InputProcessor {

    private val stage = Stage(ExtendViewport(20f, 10f))
    private val floor = Floor(stage)
    private val timer = Timer()

    init {
        timer.add(1f) {
            floor.addWave(LinearWave(
                    startPoint =  Vector2(20f, 3f),
                    endPoint = Vector2(-20f, -3f),
                    maxIntensity = 1.8f,
                    windowWidth = 2.5f,
                    sustainRatio = 0.8f,
                    travelTime = 5f
            ))
            rewindTimer(13f)
        }
    }

    private val items = ZipList(listOf(
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
        timer.update(delta)
        floor.update(delta)

        floor.drawFloorTiles()

        batch.use {
            font.data.setScale(1.5f)

            for ((index, item) in items.withIndex()) {
                val text = if (item == items.current)
                    item.text.padEnd(8) + " >"
                else
                    item.text

                font.draw(it, text, 105f, menuYPosition(index))
            }

            font.data.setScale(1.0f)

            font.draw(it, "Selection [W] and [S] or Up and Down arrow keys, [Enter] start or quit", 60f, 45f)
            font.draw(it, "Gameplay [W] [A] [S] {D] for movement, [LButton} for shooting", 60f, 25f)
        }
    }

    private fun menuYPosition(index: Int) = Gdx.graphics.height * 0.61f - index * 64f

    override fun keyDown(keycode: Int): Boolean {
        var selected = false

        when (keycode) {
            Input.Keys.W, Input.Keys.UP -> {
                items.backward()
                selected = true
            }
            Input.Keys.S, Input.Keys.DOWN -> {
                items.forward()
                selected = true
            }
            Input.Keys.ENTER -> items.current.callback.invoke(game)
        }

        if (selected) {
            floor.addWave(CircularWave(
                    origin = stage.screenToStageCoordinates(Vector2(120f, stage.viewport.screenHeight - menuYPosition(items.index))),
                    maxLifeTime = 1.2f,
                    maxIntensity = 3.5f,
                    sustainRadius = 5f,
                    releaseRadius = 8f,
                    windowWidth = 2.8f,
                    inverted = false,
                    type = HighlightType.Circle
            ))
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