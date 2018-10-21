package gamejam4.game

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.math.max

data class Player(
        val sprites: List<Sprite>,
        val highlightLevelGetter: (Vector2) -> Float,
        val onHit: () -> Unit,
        val onDeath: () -> Unit
) : Actor() {
    var health = 100f

    init {
        setBounds(0f, 0f, 1f, 1f)
        setOrigin(0.5f, 0.5f)
        setPosition(x, y)
    }

    fun damage(damage: Float) {
        health = max(health - damage, 0f)
        if (health > 0f) {
            onHit()
        } else {
            onDeath()
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val intensity = highlightLevelGetter(position)
        val index = intensity.toInt().clamp(0, sprites.size - 1)
        val sprite = sprites[index]
        sprite.setScale((1 / sprite.width) * (health / 100f), (1 / sprite.height) * (health / 100f))
        sprite.color = color
        sprite.setOriginCenter()
        sprite.rotation = rotation
        sprite.setCenter(x, y)
        sprite.draw(batch)
    }
}
