package gamejam4.game

import kotlin.coroutines.experimental.buildIterator

class ZipList<T>(list: List<T>) {
    private val before = mutableListOf<T>()
    var current = list.first()
        private set
    private val after = list.drop(1).toMutableList()

    fun forward() {
        if (after.isNotEmpty()) {
            before.add(current)
            current = after.removeAt(0)
        }
    }

    fun backward() {
        if (before.isNotEmpty()) {
            after.add(0, current)
            current = before.removeAt(before.size - 1)
        }
    }

    operator fun iterator() = buildIterator {
        yieldAll(before)
        yield(current)
        yieldAll(after)
    }

    fun withIndex(): Iterator<IndexedValue<T>> = buildIterator {
        var index = 0
        for (x in iterator()) {
            yield(IndexedValue(index, x))
            index++
        }
    }

    val index: Int
        get() = before.size
}