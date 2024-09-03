package com.tramnung.vietnamesenationalday

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Vietnamese National Day",
    ) {
        App()
    }
}