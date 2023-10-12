import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
public class WebSocketClient {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to WebSocket Server");
        // You can send messages to the server here if needed
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        // Handle incoming messages from the server
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Connection closed: " + reason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: " + throwable.getMessage());
    }

    public static void main(String[] args) {
        String serverEndpoint = "wss://example.com/socket"; // Replace with your WebSocket server endpoint

        try {
            // Connect to the WebSocket server using WSS
            Session session = ContainerProvider.getWebSocketContainer().connectToServer(WebSocketClient.class, new URI(serverEndpoint));
            
            // Perform actions with the WebSocket session as needed

            // Close the WebSocket connection when done
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
