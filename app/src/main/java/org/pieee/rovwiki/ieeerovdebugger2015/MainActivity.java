package org.pieee.rovwiki.ieeerovdebugger2015;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;//ID for Bluetooth Intent (which is used to identify which Intent is run)
    private BluetoothAdapter myBluetooth;//BluetoothAdapter object that holds everything
    private TextView[] listOfTextViews;
    private Spinner[] spinners;

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bluetoothDevices);
        spinners[0].setAdapter(adapter);
    }
    private void initBluetooth()
    {
        //should bother them aboutBluettoth if they leave app and turn off bluetooth and come back
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
        listOfTextViews[0].setText("Bluetooth works!");


    }

    @Override
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
