package eu.magicsk.transi.data.remote.responses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Stop(
    val stop: String,
    val time: String
) : Parcelable