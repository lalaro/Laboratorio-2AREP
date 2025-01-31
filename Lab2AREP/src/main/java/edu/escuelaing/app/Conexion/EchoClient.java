package edu.escuelaing.app.Conexion;

import java.io.*;
import java.net.*;

public class EchoClient {
    public static void main(String[] args) throws IOException {
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // Crea un socket para conectarse al servidor en el host "127.0.0.1" (localhost) y el puerto 35000
            echoSocket = new Socket("127.0.0.1", 35000);

            // Crea un PrintWriter para enviar datos al servidor
            out = new PrintWriter(echoSocket.getOutputStream(), true);

            // Crea un BufferedReader para leer los datos enviados por el servidor
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host!");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: localhost.");
            System.exit(1);
        }

        // Crea un BufferedReader para leer la entrada del usuario desde la consola
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;

        // Bucle infinito para leer la entrada del usuario y enviarla al servidor
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            System.out.println("echo: " + in.readLine());
        }

        // Cierra todos los recursos
        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }
}