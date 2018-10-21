package gamejam4.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.graphics.use
import ktx.math.*
import kotlin.math.*

class Floor(
        private val stage: Stage
) {
    private val batch = SpriteBatch()
    private val sprites = (1..6).map { Sprite(Texture("ground/default$it.png")) }
    private val waves = mutableListOf<Wave>()

    fun drawFloorTiles() {
        val start = Vector2(0f, Gdx.graphics.height.toFloat())
                .toWorldSpace()
                .roundDown()
                .add(-1, -1)
        val end = Vector2(Gdx.graphics.width.toFloat(), 0f)
                .toWorldSpace()
                .roundUp()
                .add(1, 1)

        batch.use {
            for (pos in start..end) {
                val floatPos = pos.toVector2()
                val highlightLevel = waveIntensityAt(floatPos).clamp(0f, sprites.size - 0.05f)
                val highlightIndex = highlightLevel.toInt()
                val screenSpacePos = floatPos.toScreenSpace()
                sprites[highlightIndex].apply {
                    setScale(1f / width * stage.viewport.screenWidth / stage.viewport.worldWidth)
                    setCenter(screenSpacePos.x, screenSpacePos.y)
                    draw(batch)
                }
            }
        }
    }

    fun addWave(wave: Wave) {
        waves += wave
    }

    fun addCircularWave(
            origin: Vector2,
            maxLifeTime: Float = 1.2f,
            maxIntensity: Float = 3.5f,
            sustainRadius: Float = 5f,
            releaseRadius: Float = 8f,
            windowWidth: Float = 2.8f,
            highlightType: HighlightType = HighlightType.Circle,
            inverted: Boolean = false
    ) {
        waves += CircularWave(
                origin,
                maxLifeTime,
                maxIntensity,
                sustainRadius,
                releaseRadius,
                windowWidth,
                highlightType,
                inverted
        )
    }

    fun waveNormalVectorAt(pos: Vector2): Vector2? = waves
            .map { it.normalAt(pos) * it.intensityAt(pos) }
            .let { if (it.isEmpty()) null else it }
            ?.reduce { a, b -> a + b }

    fun waveIntensityAt(pos: Vector2): Float = waves
            .asSequence()
            .map { it.intensityAt(pos) }
            .sum()

    fun update(delta: Float) {
        waves.forEach { it.update(delta) }
        waves.removeAll { it.isDone }
    }

    private fun Vector2.toWorldSpace() = stage.viewport.unproject(this)
    private fun Vector2.toScreenSpace() = stage.viewport.project(this)
}

sealed class Wave {
    abstract val isDone: Boolean
    abstract fun intensityAt(pos: Vector2): Float
    abstract fun normalAt(pos: Vector2): Vector2
    abstract fun update(delta: Float)
}

class LinearWave(
        private val startPoint: Vector2,
        endPoint: Vector2,
        private val windowWidth: Float,
        private val maxIntensity: Float,
        private val travelTime: Float,
        private val sustainRatio: Float
) : Wave() {
    private val travelVector = endPoint - startPoint
    private val travelDistance = travelVector.len()
    private val normal = travelVector / travelDistance

    private var lifeTime = 0f
    private var currPoint = startPoint
    private var currPointDistance = 0f
    private var currIntensity = 0f

    override val isDone: Boolean get() = lifeTime >= travelTime

    override fun intensityAt(pos: Vector2): Float {
        val startToPos = pos - startPoint
        val distanceToWindowCenter = abs(startToPos.dot(normal) - currPointDistance)
        val intensityMultiplier = (1f - distanceToWindowCenter / windowWidth * 2f).clamp(0f, 1f)
        return intensityMultiplier * currIntensity
    }

    override fun normalAt(pos: Vector2): Vector2 = normal

    override fun update(delta: Float) {
        lifeTime += delta
        val ratio = lifeTime / travelTime
        currPoint = startPoint + travelVector * ratio
        currPointDistance = travelDistance * ratio
        val slope = maxIntensity * 2f / (1f - sustainRatio)
        val x = 1f - abs(ratio - 0.5f)
        currIntensity = (x * slope).clamp(0f, maxIntensity)
    }

}

enum class HighlightType(val distanceFunction: (Vector2, Vector2) -> Float) {
    Circle({ a, b -> a.distanceTo(b) }),
    Diamond({ a, b -> a.hammingDistanceTo(b) }),
    Square({ a, b -> a.maxSingleAxisDistanceTo(b) }),
}

class CircularWave(
        private val origin: Vector2,
        private val maxLifeTime: Float,
        private val maxIntensity: Float,
        private val sustainRadius: Float,
        private val releaseRadius: Float,
        private val windowWidth: Float,
        private val type: HighlightType,
        private val inverted: Boolean
) : Wave() {
    override val isDone get() = lifetime >= maxLifeTime

    private var lifetime = 0f
    private var windowCenter = 0f

    override fun intensityAt(pos: Vector2): Float {
        val distanceToOrigin = type.distanceFunction(origin, pos)
        val ratio = (distanceToOrigin - sustainRadius) / (releaseRadius - sustainRadius)
        val maxLocalIntensity = (1f - ratio * maxIntensity).clamp(0f, maxIntensity)
        val dist = abs(distanceToOrigin - windowCenter)
        val intensityMultiplier = (1f - dist / windowWidth * 2f).clamp(0f, 1f)
        return maxLocalIntensity * intensityMultiplier
    }

    override fun normalAt(pos: Vector2): Vector2 {
        val d = pos - origin
        val v = when (type) {
            HighlightType.Circle -> d
            HighlightType.Diamond -> Vector2(sign(d.x), sign(d.y))
            HighlightType.Square -> if (abs(d.x) > abs(d.y)) {
                Vector2(sign(d.x), 0f)
            } else {
                Vector2(0f, sign(d.y))
            }
        }
        v.normalize()
        return if (inverted) -v else v
    }

    override fun update(delta: Float) {
        lifetime += delta
        val ratio = if (inverted) 1f - lifetime / maxLifeTime else lifetime / maxLifeTime
        windowCenter = ratio * releaseRadius
    }
}
