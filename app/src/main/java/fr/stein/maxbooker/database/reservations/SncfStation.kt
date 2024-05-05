package fr.stein.maxbooker.database.reservations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SncfStation(
    @PrimaryKey @ColumnInfo(name = "rrcode") val rrcode: String,
    @ColumnInfo(name = "label") val label: String
)
