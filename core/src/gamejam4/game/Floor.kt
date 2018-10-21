package gamejam4.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.graphics.use
import ktx.math.minus
import ktx.math.plus
import ktx.math.times
import kotlin.math.*

class Floor(
        private val stage: Stage
) {
    private val batch = SpriteBatch()
    private val sprites = (1..6).map { Sprite(Texture("ground/default$it.png")) }
    private val highlights = mutableListOf<Highlight>()

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

    fun addFloorHighlight(
            origin: Vector2,
            maxLifeTime: Float = 1.2f,
            maxIntensity: Float = 3.5f,
            sustainRadius: Float = 5f,
            releaseRadius: Float = 8f,
            windowWidth: Float = 2.8f,
            highlightType: HighlightType = HighlightType.Circle
    ) {
        highlights += Highlight(
                origin,
                maxLifeTime,
                maxIntensity,
                sustainRadius,
                releaseRadius,
                windowWidth,
                highlightType
        )
    }

    fun waveNormalVectorAt(pos: Vector2): Vector2 = highlights
            .asSequence()
            .map { it.normalAt(pos) * it.highlightLevelOf(pos) }
            .reduce { a, b -> a + b }

    fun waveIntensityAt(pos: Vector2): Float = highlights
            .asSequence()
            .map { it.highlightLevelOf(pos) }
            .sum()

    fun update(delta: Float) {
        highlights.forEach { it.update(delta) }
        highlights.removeAll { it.isDone }
    }

    private fun Vector2.toWorldSpace() = stage.viewport.unproject(this)
    private fun Vector2.toScreenSpace() = stage.viewport.project(this)
}

enum class HighlightType(val distanceFunction: (Vector2, Vector2) -> Float) {
    Circle({ a, b -> a.distanceTo(b) }),
    Diamond({ a, b -> a.hammingDistanceTo(b) }),
    Square({ a, b -> a.maxSingleAxisDistanceTo(b) }),
}

private class Highlight(
        val origin: Vector2,
        val maxLifeTime: Float,
        val maxIntensity: Float,
        val sustainRadius: Float,
        val releaseRadius: Float,
        val windowWidth: Float,
        val type: HighlightType
) {
    val isDone get() = lifetime >= maxLifeTime

    private var lifetime = 0f
    private var windowCenter = 0f

    fun highlightLevelOf(pos: Vector2): Float {
        val distanceToOrigin = type.distanceFunction(origin, pos)
        val ratio = (distanceToOrigin - sustainRadius) / (releaseRadius - sustainRadius)
        val maxLocalIntensity = ((1f - ratio) * maxIntensity).clamp(0f, maxIntensity)
        val dist = abs(distanceToOrigin - windowCenter)
        val intensityMultiplier = (1f - dist / windowWidth * 2f).clamp(0f, 1f)
        return maxLocalIntensity * intensityMultiplier
    }

    fun normalAt(pos: Vector2): Vector2 {
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
        return v.normalize()
    }

    fun update(delta: Float) {
        lifetime += delta
        windowCenter = lifetime / maxLifeTime * releaseRadius
    }
}
