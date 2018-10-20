package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import ktx.math.minus
import ktx.math.vec2

class Zombie(x: Float, y: Float, val player: Player) : Actor() {
    private val sprite = Sprite(Texture("zombie.png"))
    val speed: Float = 1f
    var actionInProgress = false
    private var health: Float = 100f
    private val attackDamage: Float = 10f
    private val attackRange: Float = 0.1f

    init {
        setBounds(0f, 0f, 1f, 1f)
        setOrigin(0.5f, 0.5f)
        setPosition(x, y)

        sprite.setScale(1 / sprite.width, 1 / sprite.height)
        sprite.setOriginCenter()
        
        addAction(forever(sequence(Actions.run {
            if (actionInProgress) return@run
            actionInProgress = true

            val distanceVector = vec2(player.x, player.y) - vec2(this.x, this.y)
            val distance = distanceVector.len()

            val endAction = Actions.run { actionInProgress = false }

            val moveAction = sequence(Actions.run { rotation = distanceVector.angle() }, moveTo(player.x, player.y, distance / speed), endAction)
            val attackAction = sequence(Actions.run { attack() }, delay(2f), endAction)
            val resetActionInProgressAfterDelay = sequence(delay(0.5f), endAction)

            if (distance <= attackRange) {
                addAction(attackAction)
            } else {
                addAction(parallel(moveAction, resetActionInProgressAfterDelay))
            }
        })))
    }

    fun attack() {
        player.health -= attackDamage
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        sprite.rotation = rotation
        sprite.setCenter(x, y)
        sprite.draw(batch)
    }
}