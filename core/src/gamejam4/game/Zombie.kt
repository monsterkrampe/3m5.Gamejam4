package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import ktx.math.minus
import ktx.math.plus
import ktx.math.vec2
import kotlin.math.max

abstract class AbstractZombie(x: Float, y: Float, val player: Player) : Actor() {
    protected val sprite = Sprite(Texture("zombie.png"))
    val speed: Float = 2f
    var health: Float = 100f
    private val attackDamage: Float = 10f
    private val attackRange: Float = 1f

    init {
        setBounds(0f, 0f, 1f, 1f)
        setOrigin(0.5f, 0.5f)
        setPosition(x, y)

        sprite.setOriginCenter()

        addActionListener()
    }

    fun attack() {
        player.health = max(player.health - attackDamage, 0f)

        addAction(sequence(delay(1f), Actions.run {
            addActionListener()
        }))
    }

    fun move(angle: Float, duration: Float) {
        rotation = angle
        addAction(parallel(
                moveTo(player.x, player.y, duration),
                sequence(delay(0.5f), Actions.run {
                    clearActions()
                    addActionListener()
                })
        ))
    }

    fun bounceToDirection(bounceVector: Vector2) {
        clearActions()
        setPosition(x + bounceVector.x, y + bounceVector.y)
        addActionListener()
    }

    private fun addActionListener() {
        addAction(Actions.run {
            val distanceVector = vec2(player.x, player.y) - vec2(this.x, this.y)
            val distance = distanceVector.len()

            if (distance <= attackRange) {
                attack()
            } else {
                move(distanceVector.angle(), distance / speed)
            }
        })
    }

    protected abstract fun setDrawingScale()

    override fun draw(batch: Batch, parentAlpha: Float) {
        setDrawingScale()
        sprite.rotation = rotation
        sprite.setCenter(x, y)
        sprite.draw(batch)
    }
}

class DefaultZombie(x: Float, y: Float, player: Player) : AbstractZombie(x, y, player) {
    override fun setDrawingScale() {
        sprite.setScale((1 / sprite.width) * (health / 200f + 0.5f), (1 / sprite.height) * (health / 200f + 0.5f))
    }
}

class BigZombie(x: Float, y: Float, player: Player) : AbstractZombie(x, y, player) {
    override fun setDrawingScale() {
        sprite.setScale((1 / sprite.width) / (health / 200f + 0.5f), (1 / sprite.height) / (health / 200f + 0.5f))
    }
}
