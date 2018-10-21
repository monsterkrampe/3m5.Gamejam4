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
import ktx.math.vec2
import java.util.*
import kotlin.math.max

private const val attractionTimer = 3f
private const val wavePushMultiplier = 0.3f

class GameplayScreen(val game: TheGame) : KtxScreen {

    private val timer = Timer()
    private val viewport = ExtendViewport(20f, 10f)
    private val stage = Stage(viewport)
    private val batch = SpriteBatch().apply {
        color = Color.WHITE
    }
    private val font = BitmapFont()

    private val player = Player(
            sprites = (1..6).map { Sprite(Texture("player$it.png")) },
            speed = 3.3f,
            highlightLevelGetter = { floor.waveIntensityAt(it) }
    )
    private val zombieManager = ZombieManager(timer)
    private val floor = Floor(stage)
    private val random = Random()
    private var playerCanShot = true
    private var score = 0
    private var specialMoveEenergy = 5
    private val specialMoveNeededEnergy = 10

    init {
        stage.addActor(player)
        timer.add(1f) {
            floor.addCircularWave(
                    origin = player.position,
                    type = CircularWaveType.Circle,
                    inverted = true,
                    maxLifeTime = 2.3f,
                    windowWidth = 2.4f,
                    maxIntensity = 2.2f,
                    sustainRadius = 10f,
                    releaseRadius = 12f
            )
            if (gameIsRunning) rewindTimer(attractionTimer)
        }
        timer.add(1f) {
            floor.addWave(LinearWave(
                    startPoint = player.position + Vector2(20f, 3f),
                    endPoint = player.position + Vector2(-20f, -3f),
                    maxIntensity = 1.8f,
                    windowWidth = 2.5f,
                    sustainRatio = 0.8f,
                    travelTime = 5f
            ))
            if (gameIsRunning) rewindTimer(2f)
        }
    }

    private val gameIsRunning get() = player.health > 0

    private fun update(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit()

        val bullets = stage.actors.mapNotNull { it as? Bullet }
        val zombies = stage.actors.mapNotNull { it as? AbstractZombie }

        if (random.nextFloat() < 0.005 + score * 0.001  && zombies.size < 1000) {
            // should spawn Zombie near player
            stage.addActor(zombieManager.spawnZombieNear(player))
        }

        handleInput(delta)

        timer.update(delta)
        floor.update(delta)
        stage.act(delta)
        stage.camera.position.set(player.x, player.y, stage.camera.position.z)

        for (zombie in zombies) {
            floor.waveNormalVectorAt(zombie.position)?.let {
                zombie.bounceToDirection(it * delta * wavePushMultiplier)
            }

            for (it in bullets) {
                if (it.intersectsCircle(zombie, 0.1f)) {
                    val distVec = Vector2(zombie.x - it.x, zombie.y - it.y)

                    val dmgRate = it.vec.nor().dot(distVec.nor())
                    zombie.health = max(zombie.health - dmgRate * 25f, 0f)

                    if (zombie.health <= 0 && !zombie.isDead) {
                        zombie.isDead = true
                        addZombieDeathWaves(zombie)
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

                        score++
                        if (specialMoveEenergy < specialMoveNeededEnergy) {
                            specialMoveEenergy++
                        }
                    }

                    it.remove()
                }
            }
        }

        if (!gameIsRunning) {
            game.menu(PreviousGameResult(score))
        }
    }

    private fun randomWaveType() = CircularWaveType
            .values()
            .let { it[random.nextInt(it.size)] }

    private fun addZombieDeathWaves(zombie: AbstractZombie) {
        val type = randomWaveType()
        floor.addWave(CircularWave(
                origin = zombie.position,
                type = type,
                maxIntensity = 3.9f,
                windowWidth = 3.4f,
                sustainRadius = 4.5f,
                releaseRadius = 8f,
                maxLifeTime = 7f,
                inverted = false
        ))
    }

    private fun draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        floor.drawFloorTiles()
        stage.draw()

        batch.use {
            font.data.setScale(1.5f)
            font.draw(it, "Score: 0x" + score.toString(16), 10f, 20f)
            font.draw(it, "Energy: " + specialMoveEenergy.toString() + " / " + specialMoveNeededEnergy.toString(), 10f, 45f)
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && specialMoveEenergy == specialMoveNeededEnergy) {
            specialMoveEenergy = 0
            floor.addCircularWave(
                    origin = player.position,
                    type = randomWaveType(),
                    maxIntensity = 3.9f
            )
        }

        player.apply {
            val w = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)
            val a = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)
            val s = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)
            val d = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)

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

        if (playerCanShot && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            val xPos = Gdx.input.x.toFloat()
            val yPos = Gdx.input.y.toFloat()
            createBullet(
                    stage.screenToStageCoordinates(vec2(xPos, yPos)) - vec2(player.x, player.y)
            )
            playerCanShot = false
            timer.add(0.5f) {
                playerCanShot = true
            }
        }
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

    override fun dispose() {
        stage.dispose()
    }
}

class TheGame : KtxGame<Screen>() {

    override fun create() {
        addScreen(MenuScreen(this, null))
        setScreen<MenuScreen>()
    }

    fun start() {
        Gdx.input.inputProcessor = null
        screens.clear()
        addScreen(GameplayScreen(this))
        setScreen<GameplayScreen>()
    }

    fun menu(previousGameResult: PreviousGameResult?) {
        screens.clear()
        addScreen(MenuScreen(this, previousGameResult))
        setScreen<MenuScreen>()
    }
}

data class PreviousGameResult(val score: Int)