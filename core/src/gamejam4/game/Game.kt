package gamejam4.game

import ktx.app.KtxApplicationAdapter
import ktx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.clearScreen

class Game : KtxApplicationAdapter {
    private lateinit var zombies: List<Zombie>
    private lateinit var batch: SpriteBatch

    override fun create() {
        zombies = listOf(Zombie(Pair(0f, 0f)), Zombie(Pair(500f, 500f)))
        batch = SpriteBatch()
    }

    override fun render() {
        clearScreen(0f, 0f, 0f, 1f)
        batch.use {batch ->
            zombies.forEach {
                it.draw(batch)
            }
        }
    }

    override fun dispose() {
        batch.dispose()
    }
}
