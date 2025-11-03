package fr.stein.maxbooker.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SncfStationEntity(
    @ColumnInfo(name = "rrCode") @PrimaryKey val rrCode: String,
    @ColumnInfo(name = "label") val label: String
)
