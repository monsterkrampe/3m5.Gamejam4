package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import ktx.math.vec2

class Zombie(x: Float, y: Float, player: Player) : Actor() {
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



        addAction(forever(Actions.run {
            if (actionInProgress) return@run

            val distance = vec2(this.x, this.y).dst(vec2(player.x, player.y))

            val moveAction = sequence(moveTo(player.x, player.y, distance / speed), Actions.run { actionInProgress = false})
            val attackAction = sequence(delay(2f), Actions.run { println("attack") }, Actions.run { actionInProgress = false})

            actionInProgress = true

            if (distance < 0.1f) {
                addAction(attackAction)
            } else {
                addAction(parallel(moveAction, sequence(delay(0.5f), Actions.run { removeAction(moveAction); actionInProgress = false })))
            }
        }))
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        sprite.setScale(1 / sprite.width, 1 / sprite.height)
        sprite.setOriginCenter()
        sprite.rotation = rotation
        sprite.setCenter(x, y)
        sprite.draw(batch)
    }
}