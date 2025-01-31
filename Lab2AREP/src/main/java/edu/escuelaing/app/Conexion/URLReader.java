package edu.escuelaing.app.Conexion;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.awt.Desktop;
public class URLReader {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese una URL: ");
        String urlString = scanner.nextLine();

        URL url = new URL(urlString);
        URLConnection urlConnection = url.openConnection();

        StringBuilder contenido = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error al leer desde la URL: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultado.html"))) {
            writer.write(contenido.toString());
            System.out.println("Datos guardados en resultado.html");
        } catch (IOException e) {
            System.err.println("Error al guardar los datos en el archivo: " + e.getMessage());
        }
        try {
            Desktop.getDesktop().browse(new File("resultado.html").toURI());
        } catch (IOException e) {
            System.err.println("No se pudo abrir el archivo en el navegador: " + e.getMessage());
        }
    }
}