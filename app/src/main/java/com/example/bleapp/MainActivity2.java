package com.example.bleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity2 extends ListActivity {
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 1;


    private BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning;
    private Handler handler;


//    private final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//    private final Handler handler = new Handler();

    //Stop scanning;
//    private static final long SCAN_PERIOD = 10000;
    private static final long SCAN_PERIOD = 20000;


//    private RecyclerView devicesRecyclerView;

//    private ArrayList<BluetoothDevice> bluetoothDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main2);



//        devicesRecyclerView = findViewById(R.id.bLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothManager = getSystemService(BluetoothManager.class);
            }
        }
//        bluetoothManager = getSystemService(BluetoothManager.class);
//        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        bluetoothAdapter = bluetoothManager.getAdapter();

//        //Scanning
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        handler = new Handler();

//        bluetoothDevices = new ArrayList<>();
//        recViewAdapter = new DevicesRecViewAdapter(this);
//        recViewAdapter.setBluetoothDeviceArrayList();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            Toast.makeText(this, "ble_supported", Toast.LENGTH_LONG).show();
        }
        locationPermission();
//        turnOnBluetooth();
        scanDevice();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth ont Supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
////        inflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_scan:
////                Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
//                turnOnBluetooth();
//                return true;
//            case R.id.menu_stop:
////                Toast.makeText(this, "Stop Scanning...", Toast.LENGTH_SHORT).show();
//                scanDevice();
////                scanDevice2();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }

//    private void scanDevice2(Context context, handler: ScanHandler) {
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(!bluetoothAdapter.isEnabled()){
//            return handler(Scan)
//        }
//    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = MainActivity2.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
//                mLeDevices.add(device);

                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.activity_main2, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
//                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
//                viewHolder.deviceName.setText("unknown_device");
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    /*
     *  turning On Bluetooth
     * */



    public void turnOnBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity2.this,Manifest.permission.BLUETOOTH_CONNECT)){
                    ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);

                }
                else {
                    ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                    startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
                    Toast.makeText(this, "enableBluetooth", Toast.LENGTH_SHORT).show();
                    System.out.println("enableBluetooth++++++++++++++++++++++++++++");
                }
//                Toast.makeText(this, "enableBluetooth", Toast.LENGTH_SHORT).show();
//                System.out.println("enableBluetooth++++++++++++++++++++++++++++");
//                startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
                return;
            }
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
            Toast.makeText(this, "enableBluetooth", Toast.LENGTH_SHORT).show();
            System.out.println("Per Given++++++++++++++++++++++++++++");
//            startActivity(enableBluetooth);
        }
    }
    /*
     *  handling ScanDevice
     * */
    private void scanDevice() {

        //Scanning
//        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//        handler = new Handler();

//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_LONG).show();
//            finish();
//        }
//        else {
//            Toast.makeText(this, "ble_supported", Toast.LENGTH_LONG).show();
//        }
        if(bluetoothLeScanner == null){
            return;
        }

        if (!scanning) {
            handler.postDelayed( new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    if (ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity2.this,Manifest.permission.BLUETOOTH_SCAN)){
                            ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
                        }

                        else {
                            ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
                        }

                        Toast.makeText(MainActivity2.this, "stopScan", Toast.LENGTH_SHORT).show();
                        System.out.println("********** Permissions is not going through stopScan  "+leScanCallback+"++++++++++++++++++++++++++++");
                        bluetoothLeScanner.stopScan(leScanCallback);
                        return;

                    }
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {


                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity2.this,Manifest.permission.BLUETOOTH_SCAN)){
                    ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
                }

                System.out.println("##################Permissions is not going through startScan "+leScanCallback+"++++++++++++++++++++++++++++");
                bluetoothLeScanner.startScan(leScanCallback);
                return;
            }
            bluetoothLeScanner.startScan(leScanCallback);
            Toast.makeText(this, "Good Scan", Toast.LENGTH_SHORT).show();


            invalidateOptionsMenu();
        } else {
            scanning = false;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {


                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity2.this,Manifest.permission.BLUETOOTH_SCAN)){
                    ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
                }
                Toast.makeText(this, "stopScan", Toast.LENGTH_SHORT).show();
                System.out.println("########## Permissions is not going through stopScan "+leScanCallback+"++++++++++++++++++++++++++++");
                bluetoothLeScanner.stopScan(leScanCallback);
                return;
            }
            bluetoothLeScanner.stopScan(leScanCallback);
            Toast.makeText(this, "Bad Scan", Toast.LENGTH_SHORT).show();
            invalidateOptionsMenu();
        }
    }



    /*
     *  turning On Location
     * */

    private void locationPermission(){


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity2.this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else {
                ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity2.this,Manifest.permission.ACCESS_COARSE_LOCATION)){
                ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            else {
                ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }

    }
//    private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();
    /****
     * HHHHHHHHH
     *
     */
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            System.out.println("*++++++++++==================+++++++++:::::::"+errorCode);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            System.out.println("****************************::::::::::::::::::::" + result);
            System.out.println("****************************::::::::::::::::::::" + result.getDevice() + "66666666666");
//            System.out.println((char[]) null);
//            recViewAdapter.setBluetoothDeviceArrayList(result.getDevice());
//            devicesRecyclerView.setAdapter(recViewAdapter);

            if(result.getDevice() == null){
//                mLeDeviceListAdapter.addDevice(result.getDevice());
//                mLeDeviceListAdapter.notifyDataSetChanged();
                System.out.println(result.getDevice() == null);
                return;
            }

            else {
                System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB**************************");
                System.out.println(result.getDevice() == null);
                mLeDeviceListAdapter.addDevice(result.getDevice());
                mLeDeviceListAdapter.notifyDataSetChanged();
            }

//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    mLeDeviceListAdapter.addDevice(result.getDevice());
//                    mLeDeviceListAdapter.notifyDataSetChanged();
//                    System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB**************************");
//                    SystemClock.sleep(1000);
//                }
//            });
//            try {

//                if(mLeDeviceListAdapter.addDevice(null)){
//                    System.out.println();
//
//                }
//                mLeDeviceListAdapter.addDevice(result.getDevice());
//                mLeDeviceListAdapter.notifyDataSetChanged();;
//            }
//            catch (NullPointerException e){
//                System.out.println("################### " + e + " *******************************************");
//            }

        }
    };

//
//    private BluetoothAdapter.LeScanCallback leScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                                        try {
//                                            mLeDeviceListAdapter.addDevice(device);
//                                            mLeDeviceListAdapter.notifyDataSetChanged();
//            }
//            catch (Exception e){
//                System.out.println("################### " + e + "*******************************************");
//            }
//
//                        }
//                    });
//                }
//            };


}