package me.cowpay.util
import android.app.Activity
import android.content.res.Resources
import android.graphics.Point
import me.cowpay.CowPayBaseActivity

class ScreenSizeUtils {
    fun getItemWidth(activityCowPay: CowPayBaseActivity, numOfColumn: Double, isPadding: Boolean): Double {
        var columnWidth = 0.0
        val res = getScreenResolution(activityCowPay)
        val width = res[0]
        var padding = 0.0
        if (isPadding)
            padding = convertDpToPixel(25f).toDouble()
        columnWidth = (width - padding) / numOfColumn
        return columnWidth
    }

    fun getScreenResolution(activity: Activity): IntArray {
        val sizeInpixels = intArrayOf(500, 960)
        try {

            val display = activity.windowManager.defaultDisplay
            val size = Point()
            activity.windowManager.defaultDisplay
            display.getSize(size)
            sizeInpixels[0] = size.x
            sizeInpixels[1] = size.y
            return sizeInpixels
        } catch (e: Exception) {
            return sizeInpixels
        }
    }

    fun convertPixelsToDp(px: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val dp = px / (metrics.densityDpi / 160f)
        return Math.round(dp).toFloat()
    }

    fun convertDpToPixel(dp: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px).toFloat()
    }
}