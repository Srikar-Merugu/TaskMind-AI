package com.taskmind.app.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator

class ProgressRingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f
    private var maxProgress = 100f

    private val strokeWidth = 20f
    
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#33FFFFFF") // 20% White
        style = Paint.Style.STROKE
        strokeWidth = this@ProgressRingView.strokeWidth
        strokeCap = Paint.Cap.ROUND
    }
    
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#00E5FF") // Vibrant Cyan
        style = Paint.Style.STROKE
        strokeWidth = this@ProgressRingView.strokeWidth
        strokeCap = Paint.Cap.ROUND
    }

    private val rect = RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val inset = strokeWidth / 2f
        rect.set(inset, inset, w - inset, h - inset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Background Ring
        canvas.drawArc(rect, 0f, 360f, false, backgroundPaint)

        // Progress Ring
        val sweepAngle = (progress / maxProgress) * 360f
        canvas.drawArc(rect, -90f, sweepAngle, false, progressPaint)
    }

    fun setProgress(newProgress: Float, animated: Boolean = true) {
        if (animated) {
            val animator = ValueAnimator.ofFloat(progress, newProgress)
            animator.duration = 600
            animator.interpolator = DecelerateInterpolator()
            animator.addUpdateListener { animation ->
                progress = animation.animatedValue as Float
                invalidate()
            }
            animator.start()
        } else {
            progress = newProgress
            invalidate()
        }
    }

    fun setMaxProgress(max: Float) {
        maxProgress = max
        invalidate()
    }
}
