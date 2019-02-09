package iot.sgh.mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Switch manualMode;
    private ProgressBar humidityValue;
    private TextView humidityText;
    private TextView waterFlowRateText;
    private RadioGroup waterFlowRateRadio;
    private Button togglePumpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manualMode = findViewById(R.id.manualMode);
        humidityValue = findViewById(R.id.humidityValue);
        humidityText = findViewById(R.id.humidityText);
        waterFlowRateText = findViewById(R.id.waterFlowRateText);
        waterFlowRateRadio = findViewById(R.id.waterFlowRateRadio);
        togglePumpButton = findViewById(R.id.togglePumpButton);

        toggleWidgets(false);
        manualMode.setOnCheckedChangeListener((buttonView, isChecked) -> toggleWidgets(isChecked));
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
}
