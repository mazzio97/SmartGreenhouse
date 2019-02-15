package iot.sgh.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class SmartGreenHouseClient extends AbstractSocketClient {

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
    
    private Stack<String> irrigations = new Stack<>();
    
	 public void initialize() throws IOException {
	 	switchLabelVisibility();
	 	setUpChart();
	 	updateDateHour();
        historyButton.setOnMouseClicked((e) -> {
            Dialog<Void> dialog = new Dialog<>();
            ListView<String> lv = new ListView<>();
            lv.setMinSize(400, 500);
            lv.setMaxSize(400, 500);
            dialog.setTitle("Irrigations");
            dialog.setHeaderText("Executed irrigations history");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            dialog.getDialogPane().setContent(lv);
            lv.getItems().addAll(irrigations);
            dialog.showAndWait();
        });
        new SocketClientReceiver("RECEIVER", 7070).start();
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
		 dateLabel.setText(String.format("%02d", Calendar.getInstance().get(Calendar.DATE)) + "/" +
		                   String.format("%02d", Calendar.getInstance().get(Calendar.MONTH)) + "/" +
				 		   String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
		 hourLabel.setText(String.format("%02d", Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" +
		                   String.format("%02d", Calendar.getInstance().get(Calendar.MINUTE)));
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
	
    private class SocketClientReceiver extends AbstractSocketServer {
        private static final String ONLY_NUMBERS = "[^0-9]";
        private static final String PUMP_KEYWORD = "pump";
        private static final String MODE_KEYWORD = "status";
        private static final String IRRIG_KEYWORD = "irrig";
        private final Map<String, Consumer<String>> socketReceiverTasks = new HashMap<>();
        
        public SocketClientReceiver(String name, int port) throws IOException {
            super(name, port);
            socketReceiverTasks.put(PUMP_KEYWORD, togglePumpTask());
            socketReceiverTasks.put(MODE_KEYWORD, changeModeTask());
            socketReceiverTasks.put(IRRIG_KEYWORD, storeIrrigTask());
        }
        
        @Override
        protected void job() throws Exception {
            super.job();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg = in.lines().collect(Collectors.joining("\n"));
            socketReceiverTasks.entrySet().stream()
                                          .filter(e -> msg.contains(e.getKey()))
                                          .findAny()
                                          .ifPresent(e -> e.getValue().accept(msg.replaceFirst(e.getKey(), "")));
        }
        
        private final Consumer<String> togglePumpTask() {
            return (msg) -> {
                int flow = Integer.parseInt(msg.replaceAll(ONLY_NUMBERS, ""));
                Platform.runLater(() -> {
                    if (flow > 0) {
                        waterFlowLabel.setVisible(true);
                        waterFlowValueLabel.setVisible(true);
                        waterFlowValueLabel.setText(String.valueOf(flow));
                    } else {
                        waterFlowLabel.setVisible(false);
                        waterFlowValueLabel.setVisible(false);
                    }
                });
            };
        }
        
        private final Consumer<String> changeModeTask() {
            return (msg) -> Platform.runLater(() -> modeLabel.setText(msg.toUpperCase()));
        }
        
        private final Consumer<String> storeIrrigTask() {
            return (msg) -> irrigations.add(msg);
        }
    }
}
