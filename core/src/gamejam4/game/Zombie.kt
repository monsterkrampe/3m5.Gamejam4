package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor

class Zombie(x: Float, y: Float) : Actor() {
    private val sprite = Sprite(Texture("zombie.png"))
    val speed: Float = 1f
    var actionInProgress = false
    private var health: Int = 100
    private val attackDamage: Int = 10
    private val attackRange: Float = 5f

    init {
        setBounds(0f, 0f, 1f, 1f)
        setOrigin(0.5f, 0.5f)
        setPosition(x, y)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        sprite.setScale(1 / sprite.width, 1 / sprite.height)
        sprite.setOriginCenter()
        sprite.rotation = rotation
        sprite.setCenter(x, y)
        sprite.draw(batch)
    }
}