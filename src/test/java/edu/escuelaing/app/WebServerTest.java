package edu.escuelaing.app;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import edu.escuelaing.app.Conexion.EchoServer;

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
}
