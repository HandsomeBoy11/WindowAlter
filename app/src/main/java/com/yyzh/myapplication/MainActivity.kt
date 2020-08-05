package com.yyzh.myapplication

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

companion object{
    const val FloatRequestCode=0x100
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        open.setOnClickListener(this)
        close.setOnClickListener(this)
    }
    override fun onClick(view: View) {
       when(view.id){
           R.id.open->{
               if (!checkFloatPermission(this)) {
                   requestFloatPermission(this, FloatRequestCode)
               } else {
                   val intent = Intent(this, AlterService::class.java)
                   intent.putExtra(AlterService.TYPE, AlterService.OPEN)
                   startService(intent)
               }
           }
           R.id.close->{
               val intent = Intent(this, AlterService::class.java)
               intent.putExtra(AlterService.TYPE, AlterService.CLOSE)
               startService(intent)
           }
       }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val context = this
        if (requestCode == FloatRequestCode) {
            val handler = Handler()
            handler.postDelayed({
                if (!checkFloatPermission(context)) {
                    requestFloatPermission(context, FloatRequestCode)
                } else {
                    val intent = Intent(MainActivity@this, AlterService::class.java)
                    intent.putExtra(AlterService.TYPE, AlterService.OPEN)
                    startService(intent)
                }
            }, 1000)
        }
    }
    /***
     * 检查悬浮窗开启权限
     * @param context
     * @return
     */
    fun checkFloatPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                var cls = Class.forName("android.content.Context")
                val declaredField = cls.getDeclaredField("APP_OPS_SERVICE")
                declaredField.isAccessible = true
                var obj: Any? = declaredField.get(cls) as? String ?: return false
                obj = cls.getMethod("getSystemService", String::class.java).invoke(context, obj)
                cls = Class.forName("android.app.AppOpsManager")
                val declaredField2 = cls.getDeclaredField("MODE_ALLOWED")
                declaredField2.isAccessible = true
                val checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String::class.java)
                val result = checkOp.invoke(obj, 24, Binder.getCallingUid(), context.packageName) as Int
                return result == declaredField2.getInt(cls)
            } catch (e: Exception) {
                return false
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appOpsMgr = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager ?: return false
                val mode = appOpsMgr.checkOpNoThrow(
                    "android:system_alert_window", android.os.Process.myUid(), context
                        .packageName
                )
                return Settings.canDrawOverlays(context) || mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED
            } else {
                return Settings.canDrawOverlays(context)
            }
        }
    }

    /**
     * 悬浮窗开启权限
     * @param context
     * @param requestCode
     */
    fun requestFloatPermission(context: Activity, requestCode: Int) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivityForResult(intent, requestCode)
    }
}
