package gamejam4.game

class Timer {
    private val timers = mutableListOf<TimerElement>()

    fun add(delay: Float, callback: TimerBody.() -> Unit) {
        timers += TimerElement(delay, callback)
    }

    fun update(delta: Float) {
        timers.removeAll {
            it.timeLeft -= delta
            var remove = false
            if (it.timeLeft <= 0f) {
                val answer = it.trigger()
                if (answer.willRepeat) {
                    it.timeLeft = answer.rewindTime
                } else {
                    remove = true
                }
            }
            remove
        }
    }
}

class TimerBody {
    var rewindTime = -1f
        private set
    val willRepeat get() = rewindTime > 0f

    fun rewindTimer(delay: Float) {
        rewindTime = delay
    }

    fun stopTimer() {
        rewindTime = -1f
    }
}

private data class TimerElement(var timeLeft: Float, val callback: TimerBody.() -> Unit) {
    fun trigger() = TimerBody().apply(callback)
}
