package net.pedda.fpvracetimer.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "drones")
public class Drone {
        @PrimaryKey
        public long uid;

        @ColumnInfo(name = "name")
        public String dronename;

        @ColumnInfo(name = "color")
        public long color;

        @ColumnInfo(name = "transponderid")
        public long transponderid;

}



