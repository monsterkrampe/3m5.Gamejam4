package gamejam4.game

import com.badlogic.gdx.graphics.g2d.Batch
import java.util.*

class ZombieManager {
    val zombies = mutableListOf<Zombie>()

    fun spawnZombieNear(position: Pair<Float, Float>) {
        val randomMinusX = if (Random().nextBoolean()) 1 else -1
        val randomMinusY = if (Random().nextBoolean()) 1 else -1
        val randomX = Random().nextInt(300).toFloat()
        val randomY = Random().nextInt(300).toFloat()
        zombies.add(Zombie(Pair(
                position.first + randomMinusX * (100f + randomX),
                position.second + randomMinusY * (100f + randomY)
        )))
    }

    fun drawZombies(batch: Batch) {
        zombies.forEach {it.draw(batch)}
    }
}