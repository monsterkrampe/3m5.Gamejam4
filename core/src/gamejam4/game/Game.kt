package gamejam4.game

import ktx.app.KtxApplicationAdapter
import ktx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.clearScreen

class Game : KtxApplicationAdapter {
    private lateinit var zombieManager: ZombieManager
    private lateinit var batch: SpriteBatch

    override fun create() {
        zombieManager = ZombieManager()
        batch = SpriteBatch()
    }

    override fun render() {
        clearScreen(0f, 0f, 0f, 1f)
        batch.use {batch ->
            zombieManager.drawZombies(batch)
        }
    }

    override fun dispose() {
        batch.dispose()
    }
}
