package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite

class Zombie(var position: Pair<Float, Float>) {
    private val sprite: Sprite = Sprite(Texture("zombie.png"))
    private var faceDirection: Float = 10f

    fun draw(batch: Batch) {
        sprite.setPosition(position.first, position.second)
        sprite.rotation = faceDirection
        sprite.draw(batch)
    }
}