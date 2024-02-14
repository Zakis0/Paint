package com.example.paint

import android.graphics.Color

const val LINE_WIDTH = 20f
val LINE_COLOR = Color.WHITE

const val MIN_SCALE = 1f
const val MAX_SCALE = 5f

const val MIN_SCALE_STEP = 0.001f

enum class Modes {
    DRAW,
    SELECT,
    MOVE;
//    operator fun not() = if (this == DRAW) SELECT else DRAW
}

const val GLOBAL_DEBUG = "GlobalDebug"