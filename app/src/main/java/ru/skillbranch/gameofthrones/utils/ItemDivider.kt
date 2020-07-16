package ru.skillbranch.gameofthrones.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

class ItemDivider : RecyclerView.ItemDecoration() {
    companion object {
        private val DIVIDER_COLOR = Color.parseColor("#E1E1E1")
    }

    private val _paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = DIVIDER_COLOR
        strokeWidth = 0f
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.context.dpToPx(72)
        val right = parent.right.toFloat()
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val bottom = child.bottom.toFloat()
            c.drawLine(left, bottom, right, bottom, _paint)
        }
        super.onDraw(c, parent, state)
    }
}