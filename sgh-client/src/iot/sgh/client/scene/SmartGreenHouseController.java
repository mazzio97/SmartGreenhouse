package iot.sgh.client.scene;

import java.util.Calendar;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SmartGreenHouseController {
	private static final int UPPER_X_AXIS_BOUND = 60;
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
        historyButton.setOnMouseClicked((e) -> {
        	Integer i = Calendar.getInstance().get(Calendar.SECOND);
        	Double n = Math.random() * 100;
        	if (!series.getData().isEmpty() && i < series.getData().get(series.getData().size() - 1).getXValue().intValue()) {
        		createSeries();
        	}
        	series.getData().add(new Data<Integer, Double>(i, n));
        	chart.getData().retainAll();
        	chart.getData().addAll(series);
        });
    }
	 
	 private void switchLabelVisibility() {
		 this.waterFlowLabel.setVisible(this.waterFlowLabel.isVisible() ? false : true);
		 this.waterFlowValueLabel.setVisible(this.waterFlowValueLabel.isVisible() ? false : true);
	 }
	 
	 private void setUpChart() {
		 	chart.setTitle("Humidity variation");
		 	yAxis.setLabel("% Humidity");
		 	xAxis.setAutoRanging(false);
		 	xAxis.setUpperBound(0);
		 	xAxis.setUpperBound(UPPER_X_AXIS_BOUND);
		 	xAxis.setTickUnit(2);
			yAxis.setAutoRanging(false);
			createSeries();

	        chart.setAnimated(false);
	 }
	 
	 private void createSeries() {
		 series = new XYChart.Series<Integer, Double>();
		 series.setName("Humidity");
	 }
}
