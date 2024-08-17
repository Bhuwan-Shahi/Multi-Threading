import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MulClient {

    // This method returns a Runnable that represents a client task
    public Runnable runnable() {
        return () -> {
            int port = 8011; // Port number to connect to the server
            try {
                InetAddress address = InetAddress.getByName("localhost"); // Server address
                try (Socket socket = new Socket(address, port);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    // Send a message to the server
                    out.println("Hello from the client");

                    // Read the response from the server
                    String line = in.readLine();
                    System.out.println("Response from the server: " + line);
                }
            } catch (UnknownHostException e) {
                System.err.println("Could not find host: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error connecting to server: " + e.getMessage());
            }
        };
    }

    public static void main(String[] args) {
        MulClient mulClient = new MulClient();
        ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust the pool size as needed

        // Submit 10,000 client tasks to the executor service
        for (int i = 0; i < 100000; i++) {
            executorService.submit(mulClient.runnable());
        }

        // Shutdown the executor service
        executorService.shutdown();
    }
}