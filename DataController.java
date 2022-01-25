package project;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortMessageListenerWithExceptions;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.collections.FXCollections;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.collections.ObservableList;
import java.nio.ByteBuffer;

public class DataController implements SerialPortMessageListenerWithExceptions {
    private static final byte[] DELIMITER = new byte[]{'\n'};
    private final ObservableList<XYChart.Data<Number, Number>> dataPoints;

    public DataController() {
        this.dataPoints = FXCollections.observableArrayList();
    }

    public ObservableList<XYChart.Data<Number, Number>> getDataPoints() {
        return dataPoints;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        // TODO: Implement this method. Refer to the documentation for more details.
        if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_RECEIVED)
            return;
        var bytes = serialPortEvent.getReceivedData();
        int data = ByteBuffer.wrap(bytes).getInt();
        long now = System.currentTimeMillis();

        XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(now, data);
        Platform.runLater(() -> this.dataPoints.add(dataPoint));
    }

    @Override
    public void catchException(Exception e) {
        e.printStackTrace();
    }

    @Override
    public byte[] getMessageDelimiter() {
        return DELIMITER;
    }

    @Override
    public boolean delimiterIndicatesEndOfMessage() {
        return true;
    }
}