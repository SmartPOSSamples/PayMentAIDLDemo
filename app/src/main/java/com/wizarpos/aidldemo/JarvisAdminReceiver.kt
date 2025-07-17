package com.wizarpos.aidldemo

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

/**
 * adb shell dpm set-device-owner com.android.jarvis/.receivers.JarvisAdminReceiver
 */
class JarvisAdminReceiver : DeviceAdminReceiver() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onEnabled(context: Context, intent: Intent) {
        Log.d("JarvisAdminReceiver", "onEnabled")
        val devicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        //设置应用不可卸载
        devicePolicyManager.setUninstallBlocked(
            getComponentName(context),
            context.packageName,
            true
        )
        super.onEnabled(context, intent)
    }

    /**
     * 获取ComponentName，DevicePolicyManager的大多数方法都会用到
     */
    private fun getComponentName(context: Context): ComponentName {
        return ComponentName(
            context.applicationContext,
            JarvisAdminReceiver::class.java
        )
    }
}