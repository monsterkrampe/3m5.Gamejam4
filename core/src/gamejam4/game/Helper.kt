package gamejam4.game

data class PointI(val x: Int, val y: Int)

operator fun PointI.rangeTo(endInclusive: PointI) = RectI(this, endInclusive)

data class RectI(val start: PointI, val endInclusive: PointI)
