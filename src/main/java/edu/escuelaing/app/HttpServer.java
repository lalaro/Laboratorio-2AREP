package edu.escuelaing.app;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.HashMap;

public class HttpServer {
    private static Map<String, BiFunction<String, String, String>> servicios= new HashMap();

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            OutputStream out = clientSocket.getOutputStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );
            String inputLine, outputLine;

            boolean isFirstLine = true;
            String file = "";

            while ((inputLine = in.readLine()) != null) {
                if (isFirstLine) {
                    file = inputLine.split(" ")[1];
                    isFirstLine = false;
                }

                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }

            if (servicios.containsKey(file)) {
                String response = processRequest(file, "");
                out.write(response.getBytes());
            } else if (file.equals("/") || file.equals("/paginanew")) {
                serveStaticFiles("/archive1.html", out);
            } else {
                serveStaticFiles(file, out);
            }


            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    public static void get(String route, BiFunction<String, String, String> f){
        servicios.put(route, f);
    }

    public static void staticfiles(String path){

    }


    private static void serveStaticFiles(String filePath, OutputStream out) throws IOException {
        File requestedFile = new File("src/main/resources/archivesPractice" + filePath);
        String outputLine = "";
        if (requestedFile.exists() && requestedFile.isFile()) {
            String contentType = determineContentType(filePath);
            String header = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "\r\n";
            out.write(header.getBytes());

            try (FileInputStream fileInputStream = new FileInputStream(requestedFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } else {
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"+
                    "\r\n" +
                    "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "    <title>Form Example</title>"
                    + "    <meta charset=\"UTF-8\">"
                    + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "</head>"
                    + "<body>"
                    + "    <h1>Form with GET</h1>"
                    + "    <form action=\"/hello\">"
                    + "        <label for=\"name\">Name:</label><br>"
                    + "        <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>"
                    + "        <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">"
                    + "    </form> "
                    + "    <div id=\"getrespmsg\"></div>"
                    + ""
                    + "    <script>"
                    + "        function loadGetMsg() {"
                    + "            let nameVar = document.getElementById(\"name\").value;"
                    + "            const xhttp = new XMLHttpRequest();"
                    + "            xhttp.onload = function() {"
                    + "                document.getElementById(\"getrespmsg\").innerHTML ="
                    + "                this.responseText;"
                    + "            }"
                    + "            xhttp.open(\"GET\", \"/hello?name=\"+nameVar);"
                    + "            xhttp.send();"
                    + "        }"
                    + "    </script>"
                    + ""
                    + "    <h1>Form with POST</h1>"
                    + "    <form action=\"/hellopost\">"
                    + "        <label for=\"postname\">Name:</label><br>"
                    + "        <input type=\"text\" id=\"postname\" name=\"name\" value=\"John\"><br><br>"
                    + "        <input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">"
                    + "    </form>"
                    + ""
                    + "    <div id=\"postrespmsg\"></div>"
                    + ""
                    + "    <script>"
                    + "        function loadPostMsg(name){"
                    + "            let url = \"/hellopost?name=\" + name.value;"
                    + ""
                    + "            fetch (url, {method: 'POST'})"
                    + "                .then(x => x.text())"
                    + "                .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);"
                    + "        }"
                    + "    </script>"
                    + "</body>"
                    + "</html>";
            out.write(outputLine.getBytes());
        }
    }

    private static String determineContentType(String file) {
        if (file.endsWith(".png")) {
            return "png";
        } else if (file.endsWith(".jpg") || file.endsWith(".jpeg")) {
            return "jpeg";
        } else if (file.endsWith(".html")) {
            return "html";
        } else if (file.endsWith(".css")) {
            return "css";
        } else if (file.endsWith(".js")) {
            return "javascript";
        } else {
            return "octet-stream";
        }
    }


    /*private static String helloRestService(String path, String query){
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n"
                //+ "{\"name\": \"John\", \"age\":30, \"car\":null}";
                //+ "{\"PI\":"+ servunico.get() +"}";
                + "{\"PI\":"+ servunico.apply(",") +"}";
        return response;
    }*/

    private static String processRequest(String path, String query) {
        System.out.println("Query: " + query);
        BiFunction<String, String, String> servicio = servicios.get(path);

        if (servicio != null) {
            String responseBody = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n"
                    + "{\"result\": \"" + servicio.apply(path, query) + "\"}"; // Corrected JSON
            return responseBody;
        } else {
            return "HTTP/1.1 404 Not Found\r\n\r\n{\"error\": \"Service not found\"}"; // 404 response
        }
    }
}
