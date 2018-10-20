package gamejam4.game

import kotlin.coroutines.experimental.buildIterator

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

    operator fun iterator() = buildIterator {
        for (y in startY..endInclusiveY) {
            for (x in startX..endInclusiveX) {
                yield(PointI(x, y))
            }
        }
    }
}
