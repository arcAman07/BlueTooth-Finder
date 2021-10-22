package com.example.bluetoothfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    TextView textView;
    Button search;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<String> bluetoothDevices = new ArrayList<>();
    ArrayList<String> addresses = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    int currentTime = 10;
    int devicesFound = 0; // 0 - no devices found
    CountDownTimer waitTimer;
    // 1 - devices found

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Action",action);
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                textView.setText("Finished");
                search.setEnabled(true);
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                if (!addresses.contains(address)) {
                    addresses.add(address);
                    String deviceString = "";
                    if (name == null || name.equals("")) {
                        deviceString = address + " - RSSI" + rssi + "dBm";

                    }
                    else {
                        deviceString = name + " - RSSI" + rssi + "dBm";
                    }
                    bluetoothDevices.add(deviceString);
                    devicesFound = 1;
                    arrayAdapter.notifyDataSetChanged();
                }
                Log.i("Device Found","Name: "+name+ " Address: "+address+" RSSI: "+rssi);

            }
        }
    };

    public void onClick(View view) {
        waitTimer = new CountDownTimer(currentTime*1000,1000) {
            @Override
            public void onTick(long l) {
                Log.i("Time ticking", String.valueOf(l / 1000));
                textView.setText("Searching ...");
                search.setEnabled(false);
                bluetoothAdapter.startDiscovery();
            }

            @Override
            public void onFinish() {
                if (devicesFound == 0) {
                    Toast.makeText(getApplicationContext(),"No bluetooth devices found near you",Toast.LENGTH_SHORT);
                }
                search.setEnabled(true);
            }
        };
        bluetoothDevices.clear();
        addresses.clear();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.textView);
        search = (Button) findViewById(R.id.button);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, bluetoothDevices);
        listView.setAdapter(arrayAdapter);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver,intentFilter);



    }
}