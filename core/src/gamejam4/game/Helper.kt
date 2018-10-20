package gamejam4.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

data class PointI(val x: Int, val y: Int) {
    operator fun rangeTo(endInclusive: PointI) = RectI(
            x,
            y,
            endInclusive.x,
            endInclusive.y
    )
}

data class RectI(
        val startY: Int,
        val startX: Int,
        val endInclusiveX: Int,
        val endInclusiveY: Int
) {
    val start by lazy { PointI(startX, startY) }
    val endInclusive by lazy { PointI(endInclusiveX, endInclusiveY) }
}

fun Vector2.clone() = Vector2(this)
fun Vector2.normalize() = nor()

var Actor.position: Vector2
    get() = Vector2(x, y)
    set(value) {
        x = value.x;
        y = value.y
    }