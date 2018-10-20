package gamejam4.game

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
