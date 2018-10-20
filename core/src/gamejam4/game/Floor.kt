package gamejam4.game

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor

class FloorTile(
        private val sprite: Sprite,
        x: Float = 0f,
        y: Float = 0f
) : Actor() {
    init {
        setOrigin(0.5f, 0.5f)
        setBounds(0f, 0f, 1f, 1f)
        setPosition(x, y)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        sprite.setCenter(x, y)
        sprite.setScale(1f / sprite.width, 1f / sprite.height)
        sprite.draw(batch)
    }
}
