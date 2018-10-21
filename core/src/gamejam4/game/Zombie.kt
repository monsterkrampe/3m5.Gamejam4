package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import ktx.math.minus
import ktx.math.vec2
import kotlin.math.max

abstract class AbstractZombie(x: Float, y: Float, val player: Player) : Actor() {
    protected val sprite = Sprite(Texture("zombie.png"))
    val speed: Float = 0.3f
    var health: Float = 100f
    var isDead = false
    private val attackDamage: Float = 10f
    private val attackRange: Float = 1f

    init {
        setBounds(0f, 0f, 1f, 1f)
        setOrigin(0.5f, 0.5f)
        setPosition(x, y)

        sprite.setOriginCenter()

        addActionListener()
    }

    fun actionCreator(function: () -> Action) {
        if (health > 0) {
            val action = function()
            addAction(sequence(action, Actions.run {
                addActionListener()
            }))
        }
    }

    fun attack() {
        actionCreator {
            sequence(Actions.run { player.health = max(player.health - attackDamage, 0f) }, delay(1f))
        }
    }

    fun move() {
        actionCreator {
            val moveVector = vec2(player.x - x, player.y - y)
            moveVector.setLength(speed)
            rotation = moveVector.angle()
            moveTo(x + moveVector.x, y + moveVector.y, 0.1f)
        }
    }

    fun bounceToDirection(bounceVector: Vector2) {
        if (bounceVector.len() == 0f) return

        actionCreator {
            removeMoveSequenceActions()
            moveTo(x + bounceVector.x, y + bounceVector.y)
        }
    }

    private fun addActionListener() {
        addAction(Actions.run {
            val distanceVector = vec2(player.x, player.y) - vec2(this.x, this.y)
            val distance = distanceVector.len()

            if (distance <= attackRange) {
                attack()
            } else {
                move()
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
