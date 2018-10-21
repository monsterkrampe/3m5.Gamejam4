package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import java.util.*

class ZombieManager {
    private val sprites = (1..6).map { Sprite(Texture("zombie$it.png")) }

    fun spawnZombieNear(player: Player): Actor {
        val randomMinusX = if (Random().nextBoolean()) 1 else -1
        val randomMinusY = if (Random().nextBoolean()) 1 else -1
        val randomX = Random().nextFloat() * 5
        val randomY = Random().nextFloat() * 5

        return if (Random().nextFloat() < 0.8)
            DefaultZombie(
                    player.x + randomMinusX * (11f + randomX),
                    player.y + randomMinusY * (6f + randomY),
                    sprites,
                    player
            )
        else BigZombie(
                player.x + randomMinusX * (11f + randomX),
                player.y + randomMinusY * (6f + randomY),
                sprites,
                player
        )
    }
}
