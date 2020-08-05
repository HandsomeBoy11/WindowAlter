package com.yyzh.myapplication

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by WJ
 * Date: 2020/8/5 - 15:16
 * des:
 */
class AlterService : Service() {
    companion object {
        const val TYPE: String = "type"
        const val OPEN: String = "open"
        const val CLOSE: String = "close"
    }

    private lateinit var floatView: FloatView
    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        floatView = FloatView(MyApplication.getInstance())
        floatView.setLayout(R.layout.float_view)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val type = it.getStringExtra(TYPE)
            when (type) {
                OPEN -> {
                    if (!floatView.isShow()) {
                        floatView.show()
                    }
                }
                CLOSE -> {
                    if (this::floatView.isInitialized && floatView.isShow()) {
                        floatView.close()
                        stopSelf()
                    }

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}