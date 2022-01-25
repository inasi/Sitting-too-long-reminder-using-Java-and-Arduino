package project;
import project.DataController;
import javafx.collections.FXCollections;
import sample.SerialPortService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import sample.DataController;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    private final static int MAX_VALUE = 1 << 10;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        var controller = new DataController(); 
        var serialPort = SerialPortService.getSerialPort("COM4");
        var outputStream = serialPort.getOutputStream();
        var button = new Button("A button");
        var label = new Label();
        var pane = new BorderPane();

        var slider = new Slider();
        slider.setMin(0.0);
        slider.setMax(100.0);

        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                outputStream.write(newValue.byteValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        label.textProperty().bind(slider.valueProperty().asString("%.0f"));
        button.setOnMouseReleased(value -> {
            try {
                outputStream.write(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        button.setOnMousePressed(value -> {
            try {
                outputStream.write(255);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        pane.setTop(slider);
        pane.setPadding(new Insets(0, 20, 0, 20));

        var scene = new Scene(pane, 300, 300);

        stage.setScene(scene);
        stage.show();
        pane.setRight(button);
        pane.setLeft(label);

        serialPort.addDataListener(controller);
        
        stage.setTitle("Graph");
        var now = System.currentTimeMillis();
        var xAxis = new NumberAxis("time", now, now + 50000, 10000);
        var yAxis = new NumberAxis("y", 0, MAX_VALUE, 10);

        var series = new XYChart.Series<>(controller.getDataPoints()); 
        var lineChart = new LineChart<>(xAxis, yAxis, FXCollections.singletonObservableList(series));
        lineChart.setTitle("Motion Graph");
        pane.setCenter(lineChart);
        stage.setScene(scene);
        stage.show();

    }
}

