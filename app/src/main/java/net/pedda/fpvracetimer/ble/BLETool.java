package net.pedda.fpvracetimer.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import net.pedda.fpvracetimer.ui.droneconfig.DroneFragment;
import net.pedda.fpvracetimer.ui.droneconfig.DroneItemContent;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BLETool {

    private static final String TAG = "BLETool";

    public static final UUID CHARACTERISTIC_CONFIG = UUID.fromString("13C13A8C-10F9-4C66-AE7D-D57BB3A9B869");

    public static boolean check_blepermission(Context ctx) {
        return ctx.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean setColor(Context ctx, DroneItemContent.DroneItem droneItem) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bleDevice;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bleDevice = adapter.getRemoteLeDevice(droneItem.bleMac, BluetoothDevice.ADDRESS_TYPE_PUBLIC);
        } else {
            bleDevice = adapter.getRemoteDevice(droneItem.bleMac);
        }

        try {
            String command = "C" + Integer.toHexString(droneItem.dColor);
            boolean servicesDiscovered = false;
            BluetoothGatt bleGatt = bleDevice.connectGatt(ctx, false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    // TODO: add handling
                }

                @Override
                public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
                    super.onCharacteristicRead(gatt, characteristic, value, status);
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    if(characteristic.getStringValue(0).equals(command)) {
                        gatt.abortReliableWrite();
                    }
                }

                @Override
                public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
                    super.onCharacteristicChanged(gatt, characteristic, value);
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);

                    Log.d(TAG, "onServicesDiscovered: "+ gatt.getServices().size());

                }
            });

            bleGatt.connect();
            bleGatt.discoverServices();


//            BluetoothGattService gattService = bleGatt.getService(CHARACTERISTIC_CONFIG);
//            BluetoothGattCharacteristic configCharac = gattService.getCharacteristic(CHARACTERISTIC_CONFIG);
//            bleGatt.beginReliableWrite();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                bleGatt.writeCharacteristic(configCharac, "CFF00FF".getBytes(StandardCharsets.UTF_8), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
//            } else {
//                configCharac.setValue("CFF00FF");
//                bleGatt.writeCharacteristic(configCharac);
//            }
//            bleGatt.executeReliableWrite();
//            bleGatt.disconnect();

        } catch (SecurityException se) {
            Log.e(TAG, "BLE setColor - Security exception: %s", se);
        } finally {

        }


        return false;
    }

}
