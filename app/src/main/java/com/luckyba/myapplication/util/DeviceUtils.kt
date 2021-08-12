package com.luckyba.myapplication.util

import android.content.res.Configuration
import android.content.res.Resources

/**
 * Utility class for accessing Android device-specific information
 */
object DeviceUtils {
    const val TIMELINE_ITEMS_PORTRAIT = 4
    const val TIMELINE_ITEMS_LANDSCAPE = 5
    /**
     * Returns the state of device being in Landscape orientation.
     */
    fun isLandscape(resources: Resources) = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    /**
     * Returns the state of device being in Portrait orientation.
     */
    fun isPortrait(resources: Resources) = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}