import javafx.application.Application;
import org.example.DatabaseManager;
import org.example.HudApplication;
import org.example.LogServer;
import org.example.UdpListener;

public class Main {
    public static void main(String[] args) {

        DatabaseManager.logAppLaunch();

        LogServer.startServer();

        new Thread(() -> Application.launch(HudApplication.class, args)).start();

        UdpListener listener = new UdpListener(telemetryPoint -> {
            if (HudApplication.getInstance() != null) {
                HudApplication.getInstance().updateTelemetry(telemetryPoint);
            }
        });

        listener.start();
    }
}