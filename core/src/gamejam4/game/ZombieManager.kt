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
        val randomType = Random().nextFloat()
        return if (randomType < 0.4) {
            DefaultZombie(x, y, sprites, player, timer)
        } else if (randomType < 0.7){
            BigZombie(x, y, sprites, player, timer)
        } else if (randomType < 0.9){
            SmallZombie(x, y, sprites, player, timer)
        } else {
            HugeZombie(x, y, sprites, player, timer)
        }
    }
}
