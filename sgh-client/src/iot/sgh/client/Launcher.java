package iot.sgh.client;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application{
    private static final String URL = "/iot/sgh/layout/SmartGreenHouse.fxml";
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setTitle("Smart Green House");
        loadStage(Launcher.class.getResource(URL));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        launch();
    }

    public static Scene loadStage(URL url) {
        Scene scene = null;
        try {
            scene = new Scene(FXMLLoader.load(url));
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return scene;
    }
}
