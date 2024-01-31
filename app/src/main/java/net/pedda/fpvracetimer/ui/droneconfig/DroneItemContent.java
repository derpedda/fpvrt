package net.pedda.fpvracetimer.ui.droneconfig;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DroneItemContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<DroneItem> ITEMS = new ArrayList<DroneItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static Map<String, DroneItem> ITEM_MAP = new HashMap<String, DroneItem>();


    private static void addItem(DroneItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.droneId, item);
    }

    public static int addNewItem(String id, String deviceMac, String droneName, String droneId, int dRSSI, Integer dColor) {
        DroneItem item = null;
        if (!ITEM_MAP.containsKey(droneId)) {
            if(dColor == null)
                dColor = Color.BLUE;
            item = new DroneItemContent.DroneItem(id, droneName, droneId, dRSSI, dColor, deviceMac);
            ITEMS.add(item);
            ITEM_MAP.put(droneId, item);
            return ITEMS.indexOf(ITEM_MAP.get(droneId));
        } else {
            item = ITEM_MAP.get(droneId);
            item.dRSSI = dRSSI;
            return ITEMS.indexOf(ITEM_MAP.get(droneId));
        }
    }


    /**
     * A placeholder item representing a piece of content.
     */
    public static class DroneItem {
        public final String id;
        public final String droneName;
        public final String droneId;

        public int dRSSI;

        public int dColor;

        public final String bleMac;

        public DroneItem(String id, String droneName, String droneId, int dRSSI, int dColor, String bleMac) {
            this.id = id;
            this.droneName = droneName;
            this.droneId = droneId;
            this.dRSSI = dRSSI;
            this.dColor = dColor;
            this.bleMac = bleMac;
        }

        @Override
        public String toString() {
            return String.format("ID: %1s, Name: %2s, Color: %3s", droneId, droneName, dColor);
        }
    }
}