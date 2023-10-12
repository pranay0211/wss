import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.buffer.Buffer;

public class WebSocketClientVerticle extends AbstractVerticle {

    @Override
    public void start() {
        HttpClientOptions options = new HttpClientOptions()
                .setSsl(true)
                .setTrustAll(true);

        HttpClient client = vertx.createHttpClient(options);

        client.webSocket(443, "example.com", "/socket", webSocket -> {
            if (webSocket.succeeded()) {
                WebSocket socket = webSocket.result();

                socket.handler(data -> {
                    System.out.println("Received message: " + data.toString("UTF-8"));
                });

                socket.exceptionHandler(e -> {
                    System.out.println("WebSocket error: " + e.getMessage());
                    // Handle connection errors here
                });

                socket.closeHandler(close -> {
                    System.out.println("WebSocket closed with status code: " + close.statusCode() + ", reason: " + close.reason());
                    // Handle connection closure here
                });

                // Periodically send ping messages to keep the session alive
                vertx.setPeriodic(5000, timerId -> {
                    socket.writePing(Buffer.buffer("Ping"));
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
