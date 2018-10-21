package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.math.vec2
import java.util.*

class ZombieManager(val timer: Timer) {
    private val sprites = (1..6).map { Sprite(Texture("zombie$it.png")) }

    fun spawnZombieNear(player: Player): Actor {
        val randomAngle = Random().nextInt(360).toFloat()
        val distanceVector = vec2(15f)
        distanceVector.setAngle(randomAngle)

        val x = player.x + distanceVector.x
        val y = player.y + distanceVector.y
        return if (Random().nextFloat() < 0.8) {
            DefaultZombie(x, y, sprites, player, timer)
        } else {
            BigZombie(x, y, sprites, player, timer)
        }
    }
}
