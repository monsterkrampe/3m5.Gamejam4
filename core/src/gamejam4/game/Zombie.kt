package gamejam4.game

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.math.minus
import ktx.math.vec2
import kotlin.math.max

abstract class AbstractZombie(
        x: Float,
        y: Float,
        private val sprites: List<Sprite>,
        private val player: Player,
        private val timer: Timer
) : Actor() {
    var health: Float = 100f
    var isDead = false
    private var canAttack = true

    private var spriteIndex = 0
    private val sprite get() = sprites[spriteIndex]

    init {
        setBounds(0f, 0f, 1f, 1f)
        setOrigin(0.5f, 0.5f)
        setPosition(x, y)

        sprites.forEach { it.setOriginCenter() }

        //addActionListener()
    }

    override fun act(delta: Float) {
        // call all actions
        super.act(delta)

        if (health <= 0) return

        val distanceVector = vec2(player.x, player.y) - vec2(this.x, this.y)
        val distance = distanceVector.len()

        if (distance <= zombieAttackRange) {
            attack()
        } else {
            move(delta)
        }
    }

    fun attack() {
        if (canAttack) {
            player.health = max(player.health - zombieAttackDamage, 0f)
            canAttack = false
            timer.add(zombieAttackCooldown) {
                canAttack = true
            }
        }
    }

    fun move(delta: Float) {
        val moveVector = vec2(player.x - x, player.y - y)
        moveVector.setLength(zombieSpeed * delta)
        rotation = moveVector.angle()
        setPosition(x + moveVector.x, y + moveVector.y)
    }

    fun bounceToDirection(bounceVector: Vector2) {
        if (health <= 0 || bounceVector.len() == 0f) return

        setPosition(x + bounceVector.x, y + bounceVector.y)
    }

    protected abstract fun setDrawingScale()

    override fun draw(batch: Batch, parentAlpha: Float) {
        setDrawingScale()
        sprite.setScale((1 / sprite.width) * scaleX, (1 / sprite.height) * scaleY)
        sprite.rotation = rotation
        sprite.setCenter(x, y)
        sprite.draw(batch)
    }
}

class DefaultZombie(
        x: Float,
        y: Float,
        sprites: List<Sprite>,
        player: Player,
        timer: Timer
) : AbstractZombie(x, y, sprites, player, timer) {
    override fun setDrawingScale() {
        setScale(health / 200f + 0.5f)
    }
}

class BigZombie(
        x: Float,
        y: Float,
        sprites: List<Sprite>,
        player: Player,
        timer: Timer
) : AbstractZombie(x, y, sprites, player, timer) {
    override fun setDrawingScale() {
        setScale(1 / (health / 200f + 0.5f))
    }
}
