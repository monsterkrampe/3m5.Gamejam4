package gamejam4.game

import com.badlogic.gdx.graphics.g2d.Batch
import java.util.*

class ZombieManager {
    private val zombies = mutableListOf<Zombie>()

    fun spawnZombieNear(position: Pair<Float, Float>) {
        val randomMinusX = if (Random().nextBoolean()) 1 else -1
        val randomMinusY = if (Random().nextBoolean()) 1 else -1
        val randomX = Random().nextInt(500).toFloat()
        val randomY = Random().nextInt(500).toFloat()
        zombies.add(Zombie(Pair(
                position.first + randomMinusX * (100f + randomX),
                position.second + randomMinusY * (100f + randomY)
        )))
    }

    fun drawZombies(batch: Batch) {
        val randomFloat = Random().nextFloat()

        if (randomFloat < 0.02 && zombies.size < 10) {
            spawnZombieNear(Pair(1000f, 600f))
        }

        zombies.forEach {it.draw(batch)}
    }
}