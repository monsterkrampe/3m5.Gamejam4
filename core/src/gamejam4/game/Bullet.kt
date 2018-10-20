package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction

data class Bullet(val vec: Vector2) : Actor() {
    private val texture = Texture("bullet.png")

    init {
        setBounds(0f, 0f, 0.2f, 0.2f)
        setOrigin(0.1f, 0.1f)
        setPosition(x, y)

        val action = MoveByAction()
        action.setAmount(vec.x, vec.y)
        action.duration = 0.3f
        addAction(forever(action))
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.draw(texture, x, y, width, height)
    }


}
