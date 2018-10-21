package gamejam4.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor

data class Player(val texture: Texture,val speed: Float) : Actor() {
    private val sprite = Sprite(texture)
    var health = 100f
    init {
        setBounds(0f, 0f, 1f, 1f)
        setOrigin(0.5f, 0.5f)
        setPosition(x, y)
    }
    
    override fun draw(batch: Batch, parentAlpha: Float) {
        sprite.setScale((1 / sprite.width) * (health / 100f), (1 / sprite.height) * (health / 100f))
        sprite.setOriginCenter()
        sprite.rotation = rotation
        sprite.setCenter(x, y)
        sprite.draw(batch)
    }
}
