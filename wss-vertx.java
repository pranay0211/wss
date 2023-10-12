import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;

public class WebSocketClientVerticle extends AbstractVerticle {

    private static final long PING_INTERVAL = 5000; // Ping interval in milliseconds (e.g., every 5 seconds)
    private static final long SESSION_TIMEOUT = 20000; // Session timeout in milliseconds (e.g., 20 seconds)

    private long lastPongReceivedTimestamp = System.currentTimeMillis();

    @Override
    public void start() {
        HttpClientOptions options = new HttpClientOptions()
                .setSsl(true) // Enable SSL/TLS
                .setTrustAll(true); // Accept any certificate (for testing only)

        HttpClient client = vertx.createHttpClient(options);

        client.webSocket(443, "example.com", "/socket", webSocket -> {
            if (webSocket.succeeded()) {
                WebSocket socket = webSocket.result();

                socket.handler(data -> {
                    lastPongReceivedTimestamp = System.currentTimeMillis(); // Update timestamp on each received message
                    System.out.println("Received message: " + data.toString("UTF-8"));
                    // Handle incoming messages from the server
                });

                // Periodically send ping messages to keep the session alive
                vertx.setPeriodic(PING_INTERVAL, timerId -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastPongReceivedTimestamp > SESSION_TIMEOUT) {
                        System.out.println("Session timed out. Closing WebSocket connection.");
                        socket.close();
                        vertx.cancelTimer(timerId); // Stop the ping-pong mechanism
                    } else {
                        socket.writePing(Buffer.buffer("Ping")); // Send ping message
                    }
                });
            } else {
                System.out.println("Failed to connect: " + webSocket.cause().getMessage());
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new WebSocketClientVerticle());
    }
}
