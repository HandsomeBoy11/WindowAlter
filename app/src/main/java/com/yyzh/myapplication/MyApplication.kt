package com.yyzh.myapplication

import android.app.Application

/**
 * Created by WJ
 * Date: 2020/8/5 - 15:14
 * des:
 */
class MyApplication : Application() {

    companion object {
        private lateinit var instance: MyApplication
        fun getInstance(): MyApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}