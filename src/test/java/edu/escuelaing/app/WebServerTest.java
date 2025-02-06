package edu.escuelaing.app;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import edu.escuelaing.app.Conexion.EchoServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.*;
import java.net.*;

public class WebServerTest {
    private static Thread serverThread;
    private static int port;

    @BeforeAll
    public static void setUpServer() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }


        serverThread = new Thread(() -> {
            try {
                EchoServer.start(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDownServer() {
        try {
            EchoServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
    }

    @Test
    public void testHelloResponse() throws IOException {
        try (Socket socket = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET /hello?name=John HTTP/1.1");
            out.println();

            String response = in.readLine();
            assertTrue(response.contains("Respuesta: GET /hello?name=John"),
                    "La respuesta debe contener 'Respuesta: GET /hello?name=John'");
        }
    }

    @Test
    public void testNotFoundResponse() throws IOException {
        String url = "http://localhost:" + port + "/notfound";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        con.disconnect();
    }

    @Test
    public void testEmptyQueryString() {
        String queryString = "";

        Request request = new Request(queryString);

        assertNull(request.getValues("name"));
        assertNull(request.getValues("age"));
        assertNull(request.getValues("city"));
    }

    @Test
    public void testDuplicateParameters() {
        String queryString = "name=John&name=Alice&age=25";
        Request request = new Request(queryString);
        assertEquals("Alice", request.getValues("name"));
        assertEquals("25", request.getValues("age"));
    }

    @Test
    public void testGenerateFormResponse() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DefaultResponse.generateFormResponse(outputStream);
        String response = outputStream.toString();
        assertTrue(response.startsWith("HTTP/1.1 200 OK"),
                "La respuesta debe comenzar con 'HTTP/1.1 200 OK'");
        assertTrue(response.contains("Content-Type: text/html"),
                "La respuesta debe contener 'Content-Type: text/html'");
        assertTrue(response.contains("<!DOCTYPE html>"),
                "Debe contener la declaración '<!DOCTYPE html>'");
        assertTrue(response.contains("<html>"),
                "Debe contener la etiqueta '<html>'");
        assertTrue(response.contains("</html>"),
                "Debe contener la etiqueta '</html>'");
        assertTrue(response.contains("<form action=\"/hello\">"),
                "Debe contener un formulario GET dirigido a '/hello'");
        assertTrue(response.contains("<form action=\"/hellopost\">"),
                "Debe contener un formulario POST dirigido a '/hellopost'");
        assertTrue(response.contains("function loadGetMsg()"),
                "Debe contener la función JavaScript 'loadGetMsg()'");
        assertTrue(response.contains("function loadPostMsg(name)"),
                "Debe contener la función JavaScript 'loadPostMsg(name)'");
    }

    @Test
    public void testSingleParameter() {
        String queryString = "name=John";
        Request request = new Request(queryString);
        assertEquals("John", request.getValues("name"));
    }

    @Test
    public void testSpecialCharactersInParameters() {
        String queryString = "name=John Doe&message=Hello%20World";
        Request request = new Request(queryString);
        assertEquals("John Doe", request.getValues("name"));
        assertEquals("Hello%20World", request.getValues("message"));
    }


    @Test
    public void testMultipleParameters() {
        Request request = new Request("name=John&age=25&city=Bogota");
        assertEquals("John", request.getValues("name"), "El valor de 'name' debe ser 'John'");
        assertEquals("25", request.getValues("age"), "El valor de 'age' debe ser '25'");
        assertEquals("Bogota", request.getValues("city"), "El valor de 'city' debe ser 'Bogota'");
    }


    @Test
    public void testNullQueryString() {
        Request request = new Request(null);
        assertNull(request.getValues("name"), "El valor de 'name' debe ser null");
    }

    @Test
    public void testGenerateFormResponse2() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DefaultResponse.generateFormResponse(outputStream);
        String response = outputStream.toString();
        assertTrue(response.startsWith("HTTP/1.1 200 OK"), "Debe iniciar con código 200 OK");
        assertTrue(response.contains("Content-Type: text/html"), "Debe tener tipo de contenido HTML");
        assertTrue(response.contains("<html>") && response.contains("</html>"), "Debe contener estructura HTML básica");
        assertTrue(response.contains("<form action=\"/hello\">"), "Debe contener formulario GET");
        assertTrue(response.contains("<form action=\"/hellopost\">"), "Debe contener formulario POST");
    }


}
