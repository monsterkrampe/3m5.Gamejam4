package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction

data class Bullet(val vec: Vector2) : Actor() {
    private val texture = Texture("bullet.png")
    private val actionLength = 0.1f

    init {
        setBounds(0f, 0f, 0.2f, 0.2f)
        setOrigin(0.1f, 0.1f)
        setPosition(x, y)

        val action = MoveByAction()
        action.setAmount(vec.x * actionLength, vec.y * actionLength)
        action.duration = actionLength
        addAction(parallel(
                forever(action),
                delay(20f, removeActor())
        ))
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.draw(texture, x, y, width, height)
    }
}
