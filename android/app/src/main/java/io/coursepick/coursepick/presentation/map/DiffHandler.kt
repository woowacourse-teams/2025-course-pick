package io.coursepick.coursepick.presentation.map

class DiffHandler<T>(
    private val onItemAdded: (T) -> Unit,
    private val onItemRemoved: (T) -> Unit,
    initialValue: Set<T> = emptySet(),
) {
    private val items = initialValue.toMutableSet()

    fun updateItems(newValue: Set<T>) {
        val removedItems = items.subtract(newValue)
        val addedItems = newValue.subtract(items)

        removedItems.forEach { item: T ->
            items.remove(item)
            onItemRemoved(item)
        }
        addedItems.forEach { item: T ->
            items.add(item)
            onItemAdded(item)
        }
    }
}
