package gamejam4.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.math.minus
import ktx.math.plus
import ktx.graphics.use
import ktx.math.times
import java.util.*

class GameplayScreen : KtxScreen, InputProcessor {

    private val viewport = ExtendViewport(20f, 10f)
    private val stage = Stage(viewport)
    private val batch = SpriteBatch().apply {
        color = Color.WHITE
    }
    private val font = BitmapFont()

    private val playerSprite = Texture("player.png")
    private val player = Player(playerSprite, 10.5f)
    private val zombieManager = ZombieManager()
    private val floor = Floor(stage)
    private val random = Random()

    init {
        stage.addActor(player)
        Gdx.input.inputProcessor = this
    }

    private fun update(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit()

        val bullets = stage.actors.mapNotNull { it as? Bullet }
        val zombies = stage.actors.mapNotNull { it as? Zombie }

        if (random.nextFloat() < 0.01 && zombies.size < 1000) {
            // should spawn Zombie near player
            stage.addActor(zombieManager.spawnZombieNear(player))
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            val ran = random.nextFloat() * 3f
            val type = when {
                ran < 1f -> HighlightType.Circle
                ran < 2f -> HighlightType.Diamond
                else -> HighlightType.Square
            }
            floor.addFloorHighlight(
                    origin = player.position,
                    highlightType = type
            )
        }
        floor.update(delta)
        stage.act(delta)

        handleInput(delta)
        stage.camera.position.set(player.x, player.y , stage.camera.position.z)

        for (zombie in zombies) {
            floor.waveNormalVectorAt(zombie.position)?.let{
                zombie.bounceToDirection(it * delta)
            }

            for (it in bullets) {
                if (it.intersectsCircle(zombie, 0.2f)) {
                    val distVec = Vector2(zombie.x - it.x, zombie.y - it.y)

                    val dmgRate = it.vec.nor().dot(distVec.nor())
                    zombie.health -= dmgRate * 25f

                    if (zombie.health <= 0) {
                        zombie.clearActions()

                        zombie.addAction(
                                sequence(
                                        repeat(2 * 60, sequence(
                                                delay(1f / 60),
                                                rotateBy(20f)
                                        )),
                                        removeActor()
                                )
                        )
                    }
                    it.remove()
                }
            }
        }
    }

    private fun draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (player.health > 0) {
            floor.drawFloorTiles()
            stage.draw()
        } else {
            batch.use {
                font.data.setScale(2f)
                font.draw(it, "Game Over", 500f, 400f)
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        update(delta)
        draw()
    }

    private fun handleInput(delta: Float) {
        player.apply {
            val w = Gdx.input.isKeyPressed(Input.Keys.W)
            val a = Gdx.input.isKeyPressed(Input.Keys.A)
            val s = Gdx.input.isKeyPressed(Input.Keys.S)
            val d = Gdx.input.isKeyPressed(Input.Keys.D)

            val upDown = if (w xor s)
                if (w) 1f else -1f
                else 0f

            val leftRight = if (a xor d)
                if (d) 1f else -1f
                else 0f

            val vec = Vector2(leftRight, upDown)
            vec.setLength(delta * player.speed)

            player.position = player.position + vec
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {
            createBullet(
                stage.screenToStageCoordinates(Vector2(screenX.toFloat(), screenY.toFloat())) - Vector2(player.x, player.y)
            )
            return true
        }

        return false
    }

    private fun createBullet(vec: Vector2) {
        vec.normalize()
        val bullet = Bullet(vec)
        val offsetVector = vec.clone()
        offsetVector.setLength(player.width / 2)
        bullet.x = player.x - bullet.originX + offsetVector.x
        bullet.y = player.y - bullet.originY + offsetVector.y

        stage.addActor(bullet)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false

    override fun keyTyped(character: Char): Boolean = false

    override fun scrolled(amount: Int): Boolean  = false

    override fun keyUp(keycode: Int): Boolean  = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean  = false

    override fun keyDown(keycode: Int): Boolean  = false

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
