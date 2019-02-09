package iot.sgh.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Calendar;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SmartGreenHouseController extends AbstractSocketClient {

	private static final int UPPER_X_AXIS_BOUND = 60;
	@FXML
    private Label dateLabel;
	@FXML
    private Label hourLabel;
    @FXML
    private Label modeLabel;
    @FXML
    private Label waterFlowLabel;
    @FXML
    private Label waterFlowValueLabel;
    @FXML
    private Button historyButton;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private LineChart<Integer, Double> chart;
    
    private XYChart.Series<Integer, Double> series;
    
	
	 public void initialize() {
	 	switchLabelVisibility();
	 	setUpChart();
	 	updateDateHour();
        historyButton.setOnMouseClicked((e) -> {
        });
        this.start();
    }
	 
	 private void switchLabelVisibility() {
		 this.waterFlowLabel.setVisible(this.waterFlowLabel.isVisible() ? false : true);
		 this.waterFlowValueLabel.setVisible(this.waterFlowValueLabel.isVisible() ? false : true);
	 }
	 
	 private void setUpChart() {
		 	chart.setTitle("Humidity variation");
		 	xAxis.setLabel("Seconds");
		 	xAxis.setAutoRanging(false);
		 	xAxis.setUpperBound(0);
		 	xAxis.setUpperBound(UPPER_X_AXIS_BOUND);
		 	xAxis.setTickUnit(2);
		 	yAxis.setLabel("% Humidity");
			yAxis.setAutoRanging(false);
			createSeries();
			chart.setLegendVisible(false);
	        chart.setAnimated(false);
	 }
	 
	 private void createSeries() {
		 series = new XYChart.Series<Integer, Double>();;
	 }
	 private void updateDateHour() {
		 dateLabel.setText(String.valueOf(Calendar.getInstance().get(Calendar.DATE)) + " : " +
			 			   String.valueOf(Calendar.getInstance().get(Calendar.MONTH)) + " : " +
				 		   String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
		 hourLabel.setText(String.valueOf(Calendar.getInstance().get(Calendar.HOUR)) + " : " +
				 		   String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)));
	 }

	@Override
	void job(Socket socket)  throws IOException, InterruptedException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String receivedMsg = in.readLine();
		Platform.runLater(() -> {
			Double h = Double.parseDouble(receivedMsg.substring(0, receivedMsg.indexOf(":")));
			Integer i = Integer.parseInt(receivedMsg.substring(receivedMsg.indexOf(":") + 1, receivedMsg.length()));
			if (!series.getData().isEmpty() && i < series.getData().get(series.getData().size() - 1).getXValue().intValue()) {
				createSeries();
				updateDateHour();
			}
			series.getData().add(new Data<Integer, Double>(i, h));
			chart.getData().retainAll();
			chart.getData().addAll(series);				
		});
	}
}
