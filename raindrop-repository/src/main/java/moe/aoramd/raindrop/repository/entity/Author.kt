package moe.aoramd.raindrop.repository.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "author")
@Parcelize
data class Author(
    @PrimaryKey
    val id: Long,
    val name: String
) : Parcelable