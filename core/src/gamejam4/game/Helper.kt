package gamejam4.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
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

fun Vector2.maxSingleAxisDistanceTo(other: Vector2) = max(abs(x - other.x), abs(y - other.y))
fun Vector2.hammingDistanceTo(other: Vector2) = abs(x - other.x) + abs(y - other.y)
fun Vector2.distanceTo(other: Vector2) = other.dst(x, y)

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

fun Float.clamp(min: Float, max: Float) = max(min, min(max, this))
fun Int.clamp(min: Int, max: Int) = max(min, min(max, this))

var Actor.position: Vector2
    get() = Vector2(x, y)
    set(value) {
        x = value.x
        y = value.y
    }

val Actor.radius
    get() = this.width * scaleX / 2

fun Actor.intersectsCircle(actor: Actor, delta: Float = 0f) = (this.position - actor.position).len() < (this.radius + actor.radius - delta)

class SoundWithVolume(val sound: Sound, val volume: Float) {
    fun play() = sound.play(volume)
}

fun sound(path: String, volume: Float = 1f) = SoundWithVolume(
        Gdx.audio.newSound(Gdx.files.internal(path)),
        volume
)

fun music(path: String, volume: Float = 1f) =
        Gdx.audio.newMusic(Gdx.files.internal(path)).also {
            it.volume = volume
            it.isLooping = true
        }
