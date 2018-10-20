package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

data class Player(val texture: Texture,val speed: Float) : Actor() {
    init {
        width = 1f
        height = 1f
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.draw(texture, x, y, width, height)
    }
}