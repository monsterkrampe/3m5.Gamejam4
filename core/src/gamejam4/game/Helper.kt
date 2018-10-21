package gamejam4.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.math.minus
import java.lang.Math.abs
import kotlin.coroutines.experimental.buildIterator
import kotlin.math.*

data class PointI(val x: Int, val y: Int) {
    operator fun rangeTo(endInclusive: PointI) = RectI(
            x,
            y,
            endInclusive.x,
            endInclusive.y
    )

    fun add(x: Int, y: Int) = PointI(this.x + x, this.y + y)

    fun toVector2() = Vector2(x.toFloat(), y.toFloat())

    fun maxSingleAxisDistanceTo(other: PointI) = max(abs(x - other.x), abs(y - other.y)).toFloat()
    fun hammingDistanceTo(other: PointI) = abs(x - other.x) + abs(y - other.y).toFloat()
    fun distanceTo(other: PointI) = other.toVector2().dst(x.toFloat(), y.toFloat())
}

fun Vector2.round() = PointI(
        x.roundToInt(),
        y.roundToInt()
)

fun Vector2.roundDown() = PointI(
        x.nextDown().toInt(),
        y.nextDown().toInt()
)

fun Vector2.roundUp() = PointI(
        x.nextUp().toInt(),
        y.nextUp().toInt()
)

data class RectI(
        val startX: Int,
        val startY: Int,
        val endInclusiveX: Int,
        val endInclusiveY: Int
) {
    val start by lazy { PointI(startX, startY) }
    val endInclusive by lazy { PointI(endInclusiveX, endInclusiveY) }

    operator fun iterator() = buildIterator {
        for (y in startY..endInclusiveY) {
            for (x in startX..endInclusiveX) {
                yield(PointI(x, y))
            }
        }
    }
}

fun Vector2.clone() = Vector2(this)
fun Vector2.normalize() = nor()

var Actor.position: Vector2
    get() = Vector2(x, y)
    set(value) {
        x = value.x
        y = value.y
    }

val Actor.radius
    get() = this.width / 2

fun Actor.intersectsCircle(actor: Actor, delta: Float = 0f) = (this.position - actor.position).len() < (this.radius + actor.radius - delta)