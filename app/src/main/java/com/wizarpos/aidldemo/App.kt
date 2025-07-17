package com.wizarpos.aidldemo

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

class App : Application(){
    companion object {
        lateinit var instance: App
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }


    fun getVersionName(): String? {
        // 获取packagemanager的实例
        val packageManager: PackageManager =  getPackageManager()
        // getPackageName()是你当前类的包名
        var packInfo: PackageInfo? = null
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),
                0
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }
        val version = packInfo!!.versionName
        return version
    }

    fun isAppInstalled(packageName: String?): Boolean {
        var isAppInstalled = false
        val packageInfos = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        for (packageInfo in packageInfos) {
            if (packageInfo.packageName.equals(packageName, ignoreCase = true)) {
                isAppInstalled = true
                break
            }
        }
        return isAppInstalled
    }
}