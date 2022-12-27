package com.dongnh.smartlayoutmanager

import android.graphics.Camera
import android.graphics.Matrix
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign

/**
 * Project : SmartlinearLayout
 * Created by DongNH on 26/12/2022.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class ZoomPostLayoutListener(var scaleMultiplier: Float = 0F, var transformAlpha: Boolean): PostLayoutListener() {

    override fun transformChild(
        child: View,
        itemPositionToCenterDiff: Float,
        orientation: Int
    ): ItemTransformation {
        try {
            if (abs(itemPositionToCenterDiff) in 0.0F .. 0.6F) {
                if (transformAlpha) {
                    child.alpha = 1F
                }

                child.translationZ = 100F
            } else {
                val adjOne = 1.0F / 3
                val adj = ((3 + 1) - abs(itemPositionToCenterDiff)) * adjOne
                if (transformAlpha) {
                    child.alpha = adj
                }
                child.translationZ = adj * 100F
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val scale: Float = 1.0f - scaleMultiplier * abs(itemPositionToCenterDiff)

        // because scaling will make view smaller in its center, then we should move this item to the top or bottom to make it visible
        val translateY: Float
        val translateX: Float
        val translateZ: Float
        if (SmartLayoutManager.VERTICAL == orientation) {
            val translateYGeneral = child.measuredHeight * (1 - scale) / 2f
            translateY = sign(itemPositionToCenterDiff) * translateYGeneral
            translateX = 0f
            translateZ = 0F
        } else {
            val translateXGeneral = child.measuredWidth * (1 - scale) / 2f
            translateX = sign(itemPositionToCenterDiff) * translateXGeneral
            translateY = 0f
            translateZ = 0F
        }

        return ItemTransformation(scale, scale, translateX, translateY, translateZ)
    }
}