package iot.sgh.mobile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;

import iot.sgh.mobile.utils.C;
import unibo.btlib.BluetoothChannel;
import unibo.btlib.ConnectionTask;
import unibo.btlib.RealBluetoothChannel;
import unibo.btlib.ConnectToBluetoothServerTask;
import unibo.btlib.BluetoothUtils;
import unibo.btlib.exceptions.BluetoothDeviceNotFound;


public class MainActivity extends AppCompatActivity {

    private static final String SERVER_IP = "192.168.1.13";
    private static final int SERVER_PORT = 6060;
    private static final String STATUS_TAG = "status";
    private static final String SUPPLY_TAG = "sup";

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

        if(btAdapter != null && !btAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), C.bluetooth.ENABLE_BT_REQUEST);
        }
        initUI();

        new Thread(() -> {
            while(true) {
                new HumidityRetriever().execute();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        flowAmount = 25;
        pumpOpened = false;
    }

    private class HumidityRetriever extends AsyncTask<Void, Void, Void> {
        private Socket socket;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String hum = in.readLine();
                humidityValue.setProgress(Integer.parseInt(hum));
                humidityText.setText(String.format("%s %s%%", getString(R.string.humidity_level), hum));
                socket.close();

            } catch (Exception e) {
                System.err.println(Thread.interrupted());
            }
            return null;
        }
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
                } catch (BluetoothDeviceNotFound bluetoothDeviceNotFound) {
                    bluetoothDeviceNotFound.printStackTrace();
                }
            } else {
                btChannel.sendMessage(STATUS_TAG + "OFF");
                btChannel.close();
                toggleWidgets(false);
            }
        });

        waterFlowRateRadio.setOnCheckedChangeListener((group, checkedId) -> {
            switch (group.indexOfChild(findViewById(checkedId))) {
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
                btChannel.sendMessage(SUPPLY_TAG + flowAmount);
                togglePumpButton.setText(R.string.close_pump);
            } else {
                btChannel.sendMessage(SUPPLY_TAG + "0");
                togglePumpButton.setText(R.string.open_pump);
            }
            pumpOpened = !pumpOpened;
        });
    }

    private void toggleWidgets(boolean enable) {
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
                channel.sendMessage(STATUS_TAG + "ON");
                Log.d(C.APP_LOG_TAG, String.format("Status : connected to server on device %s", serverDevice.getName()));

                btChannel = channel;
                btChannel.registerListener(new RealBluetoothChannel.Listener() {
                    @Override
                    public void onMessageReceived(String receivedMessage) {
                        receivedMessage = receivedMessage.trim();
                        if (receivedMessage.equals("CONNECTED")) {
                            toggleWidgets(true);
                        }
                        else if (receivedMessage.equals("DISCONNECTED")) {
                            toggleWidgets(false);
                        }
                        Log.d(C.APP_LOG_TAG, String.format("> [RECEIVED from %s] %s\n", btChannel.getRemoteDeviceName(), receivedMessage));
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

}
