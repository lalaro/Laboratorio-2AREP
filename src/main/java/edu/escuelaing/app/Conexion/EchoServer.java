package edu.escuelaing.app.Conexion;

import java.io.*;
import java.net.*;

public class EchoServer {

    private static ServerSocket serverSocket;
    private static boolean running = true;

    public static void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server is listening on port " + port);

        while (running) {
            try (Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: " + inputLine);
                    String outputLine = "Respuesta: " + inputLine;
                    out.println(outputLine);

                    if ("Bye.".equalsIgnoreCase(inputLine.trim())) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client connection: " + e.getMessage());
            }
        }
    }

    public static void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 35000; // Puerto por defecto
        if (args.length > 0) {
            port = Integer.parseInt(args[0]); // Permite especificar un puerto personalizado
        }
        start(port);
    }
}