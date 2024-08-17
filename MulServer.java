import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Consumer;

/**
 * MulServer is a simple multi-threaded server that listens for client connections
 * and handles communication with each client in a separate thread.
 */
class MulServer {

    /**
     * Returns a Consumer that handles communication with a connected client.
     * 
     * @return a Consumer that takes a Socket and handles client communication.
     */
    public Consumer<Socket> getConsumer() {
        return clientSocket -> {
            try (
                // Use try-with-resources to ensure proper closing of resources
                PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                // Send a greeting message to the client
                toClient.println("Hello from the server!");

                // Read a message from the client
                String clientMessage = fromClient.readLine();
                System.out.println("Client message: " + clientMessage);

                // ... more communication logic can be added here ...

            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            }
        };
    }

    /**
     * The main method to start the server.
     * 
     * @param args command line arguments (not used).
     * @throws SocketException if there is an error setting the socket timeout.
     */
    public static void main(String[] args) throws SocketException {
        MulServer server = new MulServer();
        int port = 8011;
        ServerSocket serverSocket = null;

        try {
            // Create a ServerSocket to listen on the specified port
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Error creating server socket: " + e.getMessage(), e);
        }

        // Set a timeout for accepting client connections
        serverSocket.setSoTimeout(10000);

        // Continuously listen for client connections
        while (true) {
            try {
                System.out.println("Server is listening on port " + port);

                // Accept a client connection
                Socket acceptedSocket = serverSocket.accept();

                // Handle the client connection in a new thread
                Thread thread = new Thread(() -> server.getConsumer().accept(acceptedSocket));
                thread.start();

            } catch (IOException e) {
                System.err.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }
}