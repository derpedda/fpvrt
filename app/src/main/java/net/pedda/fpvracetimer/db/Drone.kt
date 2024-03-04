package net.pedda.fpvracetimer.db

import android.graphics.Color
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorLong
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import net.pedda.fpvracetimer.camtools.HelperFunctionsCV

@Entity(tableName = "drones")
class Drone {

    @ColumnInfo(name = "name")
    var dronename: String? = null


    @ColumnInfo(name = "color")
    var color: String? = null

    @Ignore
    var colorL: Long = 0
        get() = Color.parseColor(color).toColorLong()
        set(value) {
            setColorFromLong(value)
            field = value
        }

    @Ignore
    var colorI: Int = 0
        get() = Color.parseColor(color).toColor().toArgb()
        set(value) {
            setColorFromInt(value)
            field = value
        }

    private fun setColorFromLong(colLong: Long) {
        color = HelperFunctionsCV.Longcolor2ColorName(colLong)
    }

    private fun setColorFromInt(colInt: Int) {
        color = HelperFunctionsCV.color2ColorName(colInt)
    }

    var rssi: Int = 0;

    var lastSeen: Long = 0;

    @ColumnInfo(name = "transponderid")
    @PrimaryKey
    var transponderid: Long = 0

    var mac: String? = null
}
