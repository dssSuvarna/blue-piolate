package com.bluepilot.utils

import org.mockito.Mockito.any
class Mockito {
    companion object {
        fun <T> anyObject(): T {
            any<T>()
            return uninitialized()
        }

    private fun <T> uninitialized(): T = null as T
    }
}