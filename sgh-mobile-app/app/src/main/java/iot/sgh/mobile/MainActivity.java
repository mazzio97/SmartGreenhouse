package iot.sgh.mobile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.UUID;

import iot.sgh.mobile.utils.BluetoothReceiver;
import iot.sgh.mobile.utils.C;
import unibo.btlib.BluetoothChannel;
import unibo.btlib.ConnectionTask;
import unibo.btlib.RealBluetoothChannel;
import unibo.btlib.ConnectToBluetoothServerTask;
import unibo.btlib.BluetoothUtils;
import unibo.btlib.exceptions.BluetoothDeviceNotFound;


public class MainActivity extends AppCompatActivity {

    private Switch manualMode;
    private ProgressBar humidityValue;
    private TextView humidityText;
    private TextView waterFlowRateText;
    private RadioGroup waterFlowRateRadio;
    private Button togglePumpButton;
    private int flowAmount;
    private boolean pumpOpened;

    private BluetoothChannel btChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter != null && !btAdapter.isEnabled()){
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), C.bluetooth.ENABLE_BT_REQUEST);
        }
        initUI();

        flowAmount = 0;
        pumpOpened = false;
    }

    private void initUI() {
        manualMode = findViewById(R.id.manualMode);
        humidityValue = findViewById(R.id.humidityValue);
        humidityText = findViewById(R.id.humidityText);
        waterFlowRateText = findViewById(R.id.waterFlowRateText);
        waterFlowRateRadio = findViewById(R.id.waterFlowRateRadio);
        togglePumpButton = findViewById(R.id.togglePumpButton);

        toggleWidgets(false);

        manualMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    connectToBTServer();
                    toggleWidgets(true);


                } catch (BluetoothDeviceNotFound bluetoothDeviceNotFound) {
                    bluetoothDeviceNotFound.printStackTrace();
                }
            } else {
                btChannel.sendMessage("statusOFF");
                btChannel.close();
                toggleWidgets(false);
            }
        });

        waterFlowRateRadio.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case 0:
                    flowAmount = 25;
                    break;
                case 1:
                    flowAmount = 100;
                    break;
                case 2:
                    flowAmount = 250;
                    break;
                default:
                    flowAmount = 0;
                    break;
            }
        });

        togglePumpButton.setOnClickListener((e) -> {
            if (!pumpOpened) {
                btChannel.sendMessage("sup" + flowAmount);
            } else {
                btChannel.sendMessage("sup0");
            }
        });
    }

    private void toggleWidgets(boolean enable) {
        humidityValue.setVisibility(enable ? View.VISIBLE : View.GONE);
        humidityText.setVisibility(enable ? View.VISIBLE : View.GONE);
        waterFlowRateText.setEnabled(enable);
        for (int i = 0; i < waterFlowRateRadio.getChildCount(); i++) {
            waterFlowRateRadio.getChildAt(i).setEnabled(enable);
        }
        togglePumpButton.setEnabled(enable);
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        if(requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_OK){
            Log.d(C.APP_LOG_TAG, "Bluetooth enabled!");
        }

        if(requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_CANCELED){
            Log.d(C.APP_LOG_TAG, "Bluetooth not enabled!");
        }
    }

    private void connectToBTServer() throws BluetoothDeviceNotFound {
        final BluetoothDevice serverDevice = BluetoothUtils.getPairedDeviceByName(C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME);
        final UUID uuid = BluetoothUtils.generateUuidFromString(C.bluetooth.BT_SERVER_UUID);

        AsyncTask<Void, Void, Integer> execute = new ConnectToBluetoothServerTask(serverDevice, uuid, new ConnectionTask.EventListener() {
            @Override
            public void onConnectionActive(final BluetoothChannel channel) {
                channel.sendMessage("statusON");
                Log.d(C.APP_LOG_TAG, String.format("Status : connected to server on device %s", serverDevice.getName()));

                btChannel = channel;
                btChannel.registerListener(new RealBluetoothChannel.Listener() {
                    @Override
                    public void onMessageReceived(String receivedMessage) {
                        humidityValue.setProgress(convertToInt(receivedMessage));
                        Log.d(C.APP_LOG_TAG, "***" + receivedMessage + "***");
                        humidityText.setText(receivedMessage);
                        Log.d(C.APP_LOG_TAG, String.format("> [RECEIVED from %s] %s\n", btChannel.getRemoteDeviceName(), convertToInt(receivedMessage)));
                    }

                    @Override
                    public void onMessageSent(String sentMessage) {
                        Log.d(C.APP_LOG_TAG, String.format("> [SENT to %s] %s\n", btChannel.getRemoteDeviceName(), sentMessage));
                    }
                });
            }

            @Override
            public void onConnectionCanceled() {
                Log.d(C.APP_LOG_TAG, String.format("Status : unable to connect, device %s not found!", C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME));
            }
        }).execute();
    }

    // Don't know why Integer.parseInt of a correct number String throws NumberFormatException
    private int convertToInt(String s) {
        int res = 0;
        int msgLength = s.length();
        for (int i = msgLength - 2; i >= 0 ; i--) {
            char c = s.charAt(i);
            res += ((c - 48) * Math.pow(10, msgLength - i - 2));
        }
        return res;
    }
}
