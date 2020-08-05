package com.yyzh.myapplication

import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.*

/**
 * Created by WJ
 * Date: 2020/8/5 - 15:22
 * des:
 */
class FloatView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = -1) :
    View(context, attributeSet, def) {

    private var bShow: Boolean=false
    private var mRelativeY: Float=0f
    private var mRelativeX: Float=0f
    private var mScreenY: Float=0f
    private var mScreenX: Float=0f
    private lateinit var mContentView: View
    private var wmParams: WindowManager.LayoutParams?=null
    private var wm:WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private lateinit var mContext:Context
    init {
        if (wmParams == null) {
            wmParams = WindowManager.LayoutParams()
        }
        mContext = context
    }

    fun setLayout(layout_id: Int) {
        mContentView = LayoutInflater.from(mContext).inflate(layout_id, null)
        mContentView.setOnTouchListener(OnTouchListener { v, event ->
            mScreenX = event.rawX
            mScreenY = event.rawY
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mRelativeX = event.x
                    mRelativeY = event.y
                }
                MotionEvent.ACTION_MOVE -> updateViewPosition()
                MotionEvent.ACTION_UP -> {
                    updateViewPosition()
                    mRelativeY = 0f
                    mRelativeX = 0f
                }
            }
            true
        })
    }
    private fun updateViewPosition() {
        wmParams?.x = (mScreenX - mRelativeX).toInt()
        wmParams?.y = (mScreenY - mRelativeY).toInt()
        wm.updateViewLayout(mContentView, wmParams)
    }

    fun show() {
        if (mContentView != null) {
            wmParams?.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            wmParams?.format = PixelFormat.RGBA_8888
            wmParams?.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            wmParams?.alpha = 1.0f
            wmParams?.gravity = Gravity.LEFT or Gravity.TOP
            wmParams?.x = 0
            wmParams?.y = 0
            wmParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
            wmParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
            // 显示自定义悬浮窗口
            wm.addView(mContentView, wmParams)
            bShow = true
        }
    }

    fun close() {
        if (mContentView != null) {
            wm.removeView(mContentView)
            bShow = false
        }
    }

    fun isShow(): Boolean {
        return bShow
    }
}