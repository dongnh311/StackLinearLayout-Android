package com.dongnh.smartlayoutmanager

import android.os.Parcel
import android.os.Parcelable

/**
 * Project : SmartlinearLayout
 * Created by DongNH on 26/12/2022.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class SmartSavedState(parcel: Parcel) : Parcelable {

    var superState: Parcelable? = null
    var centerItemPosition = 0

    fun CarouselSavedState(superState: Parcelable?) {
        this.superState = superState
    }

    open fun CarouselSavedState(`in`: Parcel) {
        superState = `in`.readParcelable(Parcelable::class.java.classLoader)
        centerItemPosition = `in`.readInt()
    }

    fun CarouselSavedState(other: SmartSavedState) {
        superState = other.superState
        centerItemPosition = other.centerItemPosition
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeParcelable(superState, i)
        parcel.writeInt(centerItemPosition)
    }

    @JvmField
    val CREATOR: Parcelable.Creator<SmartSavedState?> =
        object :
            Parcelable.Creator<SmartSavedState?> {
            override fun createFromParcel(parcel: Parcel): SmartSavedState? {
                return SmartSavedState(
                    parcel
                )
            }

            override fun newArray(i: Int): Array<SmartSavedState?> {
                return arrayOfNulls<SmartSavedState>(
                    i
                )
            }
        }
}