package com.example.ipcclient

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.graphics.ColorUtils

class StatusBarUtils {
    //获取状态栏高度
    fun getHeight(context: Context): Int {
        var statusBarHeight = 0;
        try {
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return statusBarHeight
    }

    //适配5.0版本以上
    fun setStatusColor(window: Window, color: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = color
        setStatusTextColor(window,!isDarkColor(color))
    }

    private fun setStatusTextColor(window: Window, makeDark: Boolean) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val value = window.decorView.systemUiVisibility
            if(makeDark) {
                window.decorView.systemUiVisibility = value or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility = value xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun isDarkColor(color: Int): Boolean{
        return ColorUtils.calculateLuminance(color) < 0.5
    }

    fun setStatusTransparent(window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
    }
}