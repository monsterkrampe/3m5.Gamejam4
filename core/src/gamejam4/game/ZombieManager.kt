package gamejam4.game

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import ktx.math.vec2
import java.util.*

class ZombieManager {
    val zombies = mutableListOf<Zombie>()

    fun spawnZombieNear(player: Player) : Actor {
        val randomMinusX = if (Random().nextBoolean()) 1 else -1
        val randomMinusY = if (Random().nextBoolean()) 1 else -1
        val randomX = Random().nextFloat() * 3
        val randomY = Random().nextFloat() * 3
        val zombie = Zombie(player.x + randomMinusX * (1f + randomX), player.y + randomMinusY * (1f + randomY), player)
        zombies.add(zombie)
        return zombie
    }
}