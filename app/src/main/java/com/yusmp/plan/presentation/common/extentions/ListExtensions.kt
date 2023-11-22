package com.yusmp.plan.presentation.common.extentions

import com.yusmp.plan.presentation.common.models.Identity

fun <T> List<T>.replace(
    newItem: T,
    appendIfNotFound: Boolean = true,
    predicate: (T) -> Boolean = { newItem.id == it.id }
): List<T> where T : Identity<*> {
    val index = indexOfFirst(predicate)
    return if (index == -1) {
        if (appendIfNotFound) {
            this + newItem
        } else {
            this
        }
    } else {
        toMutableList().apply { set(index, newItem) }
    }
}