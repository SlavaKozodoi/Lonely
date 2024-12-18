package com.example.timely1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomFastScroller(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
    }
    private var dateList = mutableListOf<String>()
    private var recyclerView: RecyclerView? = null

    // Set the list of dates for the fast scroller
    fun setDateList(dates: List<String>) {
        dateList.clear()
        dateList.addAll(dates)
        invalidate() // Redraw the view when the date list is updated
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the list of dates vertically
        var yOffset = 0f
        dateList.forEach { date ->
            canvas?.drawText(date, 0f, yOffset, paint)
            yOffset += paint.measureText(date) + 10f // Adjust space between dates
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val scrollPercentage = event.y / height
                val position = (scrollPercentage * recyclerView?.adapter?.itemCount!!).toInt()
                recyclerView?.smoothScrollToPosition(position)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // Set the RecyclerView to scroll
    fun attachToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }
}
