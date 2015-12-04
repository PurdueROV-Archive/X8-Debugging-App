package org.pieee.rovwiki.ieeerovdebugger2015;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;//ID for Bluetooth Intent (which is used to identify which Intent is run)
    private BluetoothAdapter myBluetooth;//BluetoothAdapter object that holds everything
    private TextView[] listOfTextViews;
    private Spinner[] spinners;
    private ArrayAdapter<String> adapter;


    @Override//This is the first function that is called when the app starts up
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//draws the GUI on screen based on the contents of the XML file activity_main
        initTextview();
        initBluetooth();//initialize blueTooth
    }

    private void initTextview()
    {
        listOfTextViews = new TextView[1];
        listOfTextViews[0] = (TextView) findViewById(R.id.title);
        spinners = new Spinner[1];
        spinners[0] = (Spinner) findViewById(R.id.bluetoothConnectionsList);
        String[] bluetoothDevices = new String[]{"Select Device", "Device A", "Device B", "Device C"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bluetoothDevices);        
        spinners[0].setAdapter(adapter);
    }
    private void initBluetooth()
    {
        //should bother them about Bluetooth if they leave app and turn off bluetooth and come back
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {//device doesn't support BlueTooth
            listOfTextViews[0].setText("Device doesn't support BlueTooth");
            return;//I don't know what else to do here
        }
        if(!myBluetooth.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            this.listBluetoothDevices();
        }
    }

    private void listBluetoothDevices()
    {
        //this is called after initBlueTooth succeeds, we're now going to scan for all Bluetooth devices and allow the person to pick which
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bluetoothDevices);
        listOfTextViews[0].setText("Bluetooth works!");
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();//looks for paired devices
        //paired devices are are a list of devices that you already connected to before and have the info needed to connect immediately
        if(pairedDevices.size()>0)
        {
            for(BluetoothDevice device: pairedDevices)
                adapter.add(device.getName() + "\n" + device.getAddress());
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryReceiver, filter);
        myBluetooth.startDiscovery();//starts discovering devices//this is an asychnronous method


    }

    //private inner class for receiving the names and addresses of discovered Bluetooth devices after calling .startDiscovery()
    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                adapter.add(device.getName() + "\n" + device.getAddress());
            }
            myBluetooth.cancelDiscovery();//have to cancel discovery to deallocate resources
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //we have to implement onActivityResult
        //onActivityResult is called whenever you call startActivityForResult, where it plugs in the requestCode, resultCode, and data for you
        //so, it is up to you to do what you need to do
        if(requestCode == REQUEST_ENABLE_BT)
        {
            if(resultCode== RESULT_OK)
            {
                this.listBluetoothDevices();//continue with normal onCreate stuff
            }
            if(resultCode == RESULT_CANCELED)
            {
                this.initBluetooth();//yeah, this app's going to be annoying and keep requesting until you accept haha!
            }
        }
    }

}
