package gamejam4.game

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction
import ktx.math.vec2
import java.util.*

class ZombieManager {
    val zombies = mutableListOf<Zombie>()

    fun spawnZombieNear(x: Float, y: Float) : Actor {
        val randomMinusX = if (Random().nextBoolean()) 1 else -1
        val randomMinusY = if (Random().nextBoolean()) 1 else -1
        val randomX = Random().nextFloat() * 3
        val randomY = Random().nextFloat() * 3
        val zombie = Zombie(x + randomMinusX * (1f + randomX), y + randomMinusY * (1f + randomY))
        zombies.add(zombie)
        return zombie
    }

    fun attack(x: Float, y: Float) {
        zombies.forEach {
            it.clearActions()
            val distance = vec2(it.x, it.y).dst(vec2(x, y))
            if (distance < 0.5f) {
                println("attack")
            } else {
                val moveAction = MoveToAction()
                moveAction.setPosition(x, y)
                moveAction.duration = distance / it.speed
                it.addAction(moveAction)
            }
        }
    }
}