package gamejam4.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.graphics.use
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Floor(
        private val stage: Stage
) {
    private val batch = SpriteBatch()
    private val sprites = (1..3).map { Sprite(Texture("ground/default$it.png")) }
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
                val highlightLevel = min(tileHighlightLevel(pos), sprites.size - 1)
                val screenSpacePos = pos
                        .toVector2()
                        .toScreenSpace()
                sprites[highlightLevel].apply {
                    setScale(1f / width * stage.viewport.screenWidth / stage.viewport.worldWidth)
                    setCenter(screenSpacePos.x, screenSpacePos.y)
                    draw(batch)
                }
            }
        }
    }

    fun addWaterDropHighlight(
            pos: PointI,
            maxLifeTime: Float = 1.5f,
            maxRadius: Float = 5f,
            windowWidth: Float = 1.9f,
            maxHighlightLevel: Int = 3
    ) {
        highlights += Highlight(pos, maxLifeTime, maxRadius, windowWidth, maxHighlightLevel)
    }

    fun tileHighlightLevel(pos: PointI): Int = highlights
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

private class Highlight(
        val start: PointI,
        val maxLifeTime: Float,
        val maxRadius: Float,
        val windowWidth: Float,
        val maxHighlightLevel: Int
) {
    val isDone get() = lifetime >= maxLifeTime

    private var lifetime = 0f
    private val segmentWidth = windowWidth / (2 * maxHighlightLevel - 1)

    fun highlightLevelOf(pos: PointI): Int {
        val distanceToOrigin = start.distanceTo(pos)
        if (distanceToOrigin > maxRadius) return 0
        val windowCenter = lifetime / maxLifeTime * (maxRadius + windowWidth / 2f)
        val dist = abs(distanceToOrigin - windowCenter)
        val level = maxHighlightLevel - (dist / segmentWidth + 0.5f).toInt()
        return max(level, 0)
    }

    fun update(delta: Float) {
        lifetime += delta
    }
}
