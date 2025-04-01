package Model

import android.os.Parcel
import android.os.Parcelable

data class TaskDetails(
    val feladat_id: Int,
    val feladat_nev: String,
    val feladat_leiras: String,
    val feladat_hatarido: String,
    val feladat_szin: String,
    val allapot_id: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(feladat_id)
        parcel.writeString(feladat_nev)
        parcel.writeString(feladat_leiras)
        parcel.writeString(feladat_hatarido)
        parcel.writeString(feladat_szin)
        parcel.writeInt(allapot_id)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TaskDetails> {
        override fun createFromParcel(parcel: Parcel): TaskDetails {
            return TaskDetails(parcel)
        }

        override fun newArray(size: Int): Array<TaskDetails?> {
            return arrayOfNulls(size)
        }
    }
}
