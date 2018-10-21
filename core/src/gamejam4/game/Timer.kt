package gamejam4.game

object Timer {
    private val timers = mutableListOf<TimerElement>()

    fun add(delay: Float, callback: () -> Unit) {
        timers += TimerElement(delay, callback)
    }

    fun <T> add(delay: Float, context: T, callback: (T) -> Unit) {
        timers += TimerElement(delay) { callback(context) }
    }

    fun update(delta: Float) {
        timers.removeAll {
            it.timeLeft -= delta
            val shouldTrigger = it.timeLeft <= 0f
            if (shouldTrigger) it.callback()
            shouldTrigger
        }
    }
}

private data class TimerElement(var timeLeft: Float, val callback: () -> Unit)
