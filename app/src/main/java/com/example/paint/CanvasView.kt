package com.example.paint

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import kotlin.math.abs

class CanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    var mode = Modes.DRAW

    private val figures = mutableListOf<Path>()
    private val paint = Paint().apply {
        color = Color.WHITE
        strokeWidth = LINE_WIDTH
        style = Paint.Style.STROKE
    }

    private var offsetX = 0f
    private var offsetY = 0f

    private var lastTouchX = 0f
    private var lastTouchY = 0f

    var scale = 1f

    var previousSpan = 0f

    var focusX = 0f
    var focusY = 0f

    var isScaling = false

    private val scaleDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                previousSpan = detector.currentSpan
                isScaling = true
                return true
            }
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (abs(1 - detector.scaleFactor) > MIN_SCALE_STEP) {
                    scale *= detector.currentSpan / previousSpan
                    scale = scale.coerceIn(MIN_SCALE, MAX_SCALE)
                    binding.scaleTV.text = "${String.format("%.1f", scale)}X"

                    previousSpan = detector.currentSpan

                    focusX = detector.focusX
                    focusY = detector.focusY
                }
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                super.onScaleEnd(detector)
                isScaling = false
            }
        }).apply { isQuickScaleEnabled = true }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            translate(offsetX, offsetY)
            scale(scale, scale, focusX, focusY)
            for (figure in figures) {
                drawPath(figure, paint.apply { color = LINE_COLOR })
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = (event.x - offsetX - focusX) / scale + focusX
        val y = (event.y - offsetY - focusY) / scale + focusY

        return when (mode) {
            Modes.DRAW -> {
                drawMode(event, x, y)
                true
            }
            Modes.SELECT -> {
                selectMode(event, x, y)
                true
            }
            Modes.MOVE -> {
                moveMode(event, x, y)
                true
            }
            else -> false
        }
    }

    private fun drawMode(event: MotionEvent, xTouch: Float, yTouch: Float) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val newPath = Path()
                newPath.moveTo(xTouch, yTouch)
                figures.add(newPath)
            }
            MotionEvent.ACTION_MOVE -> {
                val currentFigure = figures.lastOrNull()
                currentFigure?.lineTo(xTouch, yTouch)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {

            }
        }
    }
    private fun selectMode(event: MotionEvent, xTouch: Float, yTouch: Float) {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val touchedIndex = getTouchedFigureIndex(xTouch, yTouch)
                if (touchedIndex != -1) {
                    Log.w(GLOBAL_DEBUG, touchedIndex.toString())
                }
                Toast.makeText(context, "Touched figure index: $touchedIndex", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun moveMode(event: MotionEvent, xTouch: Float, yTouch: Float) {
        scale(event)
        if (!isScaling) {
            move(event)
        }
        invalidate()
    }
    private fun move(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isScaling) {
                    lastTouchX = event.x
                    lastTouchY = event.y
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isScaling) {
                    offsetX += event.x - lastTouchX
                    offsetY += event.y - lastTouchY
                    lastTouchX = event.x
                    lastTouchY = event.y
                }
            }
        }
    }
    private fun scale(event: MotionEvent) {
        scaleDetector.onTouchEvent(event)
    }
    private fun getTouchedFigureIndex(x: Float, y: Float): Int {
        for (index in figures.indices.reversed()) {
            val bounds = RectF()
            figures[index].computeBounds(bounds, true)
            if (bounds.contains(x, y)) {
                return index
            }
        }
        return -1
    }
}
