package net.pedda.fpvracetimer.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BluetoothLeConnectionService extends Service {

    private Binder binder = new LocalBinder();

    private HashMap<String, Integer> macsandcolors = new HashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Bound successfully");
        return binder;
    }

    public class LocalBinder extends Binder {
        public BluetoothLeConnectionService getService() {
            return BluetoothLeConnectionService.this;
        }
    }

    public static final String TAG = "BluetoothLeConnectionService";
    public final static String ACTION_GATT_CONNECTED =
            "net.pedda.fpvracetimer.ble.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "net.pedda.fpvracetimer.ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "net.pedda.fpvracetimer.ble.ACTION_GATT_SERVICES_DISCOVERED";


    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;

    private int connectionState;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;

    public boolean initialize() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private void close() {
        if (bluetoothGatt == null) {
            return;
        }
        try {
            bluetoothGatt.close();
        } catch (SecurityException se) {
            Log.e(TAG, "close: Permission not granted" );
        }
        bluetoothGatt = null;
    }


    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectionState = STATE_CONNECTED;
                broadcastUpdate(ACTION_GATT_CONNECTED);
                // successfully connected to the GATT Server
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                connectionState = STATE_DISCONNECTED;
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }


    public void updateColorOnDrone(String mac, int color) {
        macsandcolors.put(mac,color);
        connect(mac);
    }

    public boolean connect(final String address) {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        try {
            BluetoothDevice device;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                 device = bluetoothAdapter.getRemoteLeDevice(address, BluetoothDevice.ADDRESS_TYPE_PUBLIC);
            } else {
                device = bluetoothAdapter.getRemoteDevice(address);
            }
            try {
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
                return true;
            } catch (SecurityException se) {
                Log.e(TAG, "connect: Permission not granted");
            }

        } catch (IllegalArgumentException exception) {
            Log.w(TAG, "Device not found with provided address.");
            return false;
        }
        // connect to the GATT server on the device
        return true;
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (bluetoothGatt == null) return null;
        return bluetoothGatt.getServices();
    }

    public boolean writeUpdates() {
        if(bluetoothGatt == null) return false;
        for(String m : macsandcolors.keySet()) {
            int color = macsandcolors.get(m);
            BluetoothGattService gattService = bluetoothGatt.getService(BLETool.CHARACTERISTIC_CONFIG);
            BluetoothGattCharacteristic btGattChar = gattService.getCharacteristic(BLETool.CHARACTERISTIC_CONFIG);
            int r = (color>>16)&0xFF;
            int g = (color>>8)&0xFF;
            int b = (color>>0)&0xFF;
            final byte[] command = {'C',(byte)r,(byte)g, (byte)b};
            btGattChar.setValue(command);
            bluetoothGatt.writeCharacteristic(btGattChar);
            macsandcolors.remove(m);
        }

        bluetoothGatt.disconnect();

        return true;
    }



}
