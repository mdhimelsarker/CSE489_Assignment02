package com.example.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView

/**
 * Custom ImageView supporting pinch-to-zoom, panning, double-tap zoom, and boundary constraints.
 */
class ZoomImageView : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val zoomMatrix = Matrix()
    private val matrixValues = FloatArray(9)

    private var baseScale = 1.0f
    private var minScale = 1.0f
    private var maxScale = 5.0f

    private var onScaleListener: ((Float) -> Unit)? = null

    val normalizedScale: Float
        get() {
            if (baseScale <= 0f) return 1.0f
            return currentScale / baseScale
        }

    private val currentScale: Float
        get() {
            zoomMatrix.getValues(matrixValues)
            return matrixValues[Matrix.MSCALE_X]
        }

    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val gestureDetector = GestureDetector(context, GestureListener())

    init {
        scaleType = ScaleType.MATRIX
    }

    fun setOnScaleChangeListener(listener: (Float) -> Unit) {
        this.onScaleListener = listener
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        post { resetZoom() }
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        post { resetZoom() }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed || baseScale <= 0f) {
            resetZoom()
        }
    }

    fun resetZoom() {
        val d = drawable ?: return
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        if (viewWidth <= 0f || viewHeight <= 0f) return

        val dw = d.intrinsicWidth.toFloat()
        val dh = d.intrinsicHeight.toFloat()
        if (dw <= 0f || dh <= 0f) return

        val scaleX = viewWidth / dw
        val scaleY = viewHeight / dh
        val fitScale = Math.min(scaleX, scaleY)

        baseScale = fitScale
        minScale = fitScale
        maxScale = fitScale * 5.0f

        zoomMatrix.reset()
        zoomMatrix.postScale(fitScale, fitScale)

        val redundantYSpace = viewHeight - (dh * fitScale)
        val redundantXSpace = viewWidth - (dw * fitScale)
        zoomMatrix.postTranslate(redundantXSpace / 2.0f, redundantYSpace / 2.0f)

        imageMatrix = zoomMatrix
        onScaleListener?.invoke(1.0f)
    }

    private fun checkAndBoundMatrix() {
        val d = drawable ?: return
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        if (viewWidth <= 0f || viewHeight <= 0f) return

        val rect = RectF(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
        zoomMatrix.mapRect(rect)

        var deltaX = 0f
        var deltaY = 0f

        if (rect.width() <= viewWidth) {
            deltaX = (viewWidth - rect.width()) / 2.0f - rect.left
        } else {
            if (rect.left > 0f) {
                deltaX = -rect.left
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right
            }
        }

        if (rect.height() <= viewHeight) {
            deltaY = (viewHeight - rect.height()) / 2.0f - rect.top
        } else {
            if (rect.top > 0f) {
                deltaY = -rect.top
            } else if (rect.bottom < viewHeight) {
                deltaY = viewHeight - rect.bottom
            }
        }

        zoomMatrix.postTranslate(deltaX, deltaY)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (drawable == null) return super.onTouchEvent(event)
        if (event.action == MotionEvent.ACTION_UP) {
            performClick()
        }
        val scaleHandled = scaleDetector.onTouchEvent(event)
        val gestureHandled = gestureDetector.onTouchEvent(event)
        return scaleHandled || gestureHandled || super.onTouchEvent(event)
    }

    private fun zoomTo(targetNormScale: Float, focusX: Float, focusY: Float) {
        val startNormScale = normalizedScale
        val animator = ValueAnimator.ofFloat(startNormScale, targetNormScale)
        animator.duration = 250
        animator.addUpdateListener { anim ->
            val animatedNormScale = anim.animatedValue as Float
            val targetAbsScale = animatedNormScale * baseScale
            val factor = targetAbsScale / currentScale
            zoomMatrix.postScale(factor, factor, focusX, focusY)
            checkAndBoundMatrix()
            imageMatrix = zoomMatrix
            onScaleListener?.invoke(normalizedScale)
        }
        animator.start()
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val origScale = currentScale
            var newScale = origScale * scaleFactor

            if (newScale < minScale) {
                newScale = minScale
            } else if (newScale > maxScale) {
                newScale = maxScale
            }

            val factor = newScale / origScale
            zoomMatrix.postScale(factor, factor, detector.focusX, detector.focusY)
            checkAndBoundMatrix()
            imageMatrix = zoomMatrix
            onScaleListener?.invoke(normalizedScale)
            return true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (normalizedScale > 1.01f) {
                zoomMatrix.postTranslate(-distanceX, -distanceY)
                checkAndBoundMatrix()
                imageMatrix = zoomMatrix
                return true
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            val targetNormScale = if (normalizedScale > 1.2f) 1.0f else 2.5f
            zoomTo(targetNormScale, e.x, e.y)
            return true
        }

        override fun onDown(e: MotionEvent): Boolean = true
    }
}
