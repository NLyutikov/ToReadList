package ru.appkode.base.entities.core.books.lists.wish

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wish_list")
data class WishListSM (
    @PrimaryKey
    val id: Long,
    val title: String? = null,
    @ColumnInfo(name = "average_rating")
    val averageRating: Double? = null,
    @ColumnInfo(name = "image_path")
    val imagePath: String? = null
)