package com.dongnh.smartlayoutmanager

/**
 * Project : SmartlinearLayout
 * Created by DongNH on 26/12/2022.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class ItemTransformation(scaleX: Float, scaleY: Float, translationX: Float, translationY: Float, translationZ: Float) {
    var scaleX: Float
    var scaleY: Float
    val translationX: Float
    val translationY: Float
    val translationZ: Float

    init {
        this.scaleX = scaleX
        this.scaleY = scaleY
        this.translationX = translationX
        this.translationY = translationY
        this.translationZ = translationZ
    }
}