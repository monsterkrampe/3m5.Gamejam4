package gamejam4.game

import com.badlogic.gdx.scenes.scene2d.Actor
import java.util.*

class ZombieManager(val timer: Timer) {
    fun spawnZombieNear(player: Player) : Actor {
        val randomMinusX = if (Random().nextBoolean()) 1 else -1
        val randomMinusY = if (Random().nextBoolean()) 1 else -1
        val randomX = Random().nextFloat() * 5
        val randomY = Random().nextFloat() * 5
        if (Random().nextFloat() < 0.8) {
            return DefaultZombie(player.x + randomMinusX * (11f + randomX), player.y + randomMinusY * (6f + randomY), player, timer)
        }
        return BigZombie(player.x + randomMinusX * (11f + randomX), player.y + randomMinusY * (6f + randomY), player, timer)
    }
}