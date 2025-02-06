package edu.escuelaing.app;

import java.io.IOException;
import java.net.URISyntaxException;
import static edu.escuelaing.app.HttpServer.staticfiles;
import static edu.escuelaing.app.HttpServer.get;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WebApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
        staticfiles("/archivesPractice");

        get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
        get("/message", (req, resp) -> "Este es mi Web Server");
        get("/hi", (req, res) -> "hello world!");

        get("/app/pi", (req, resp) -> String.valueOf(Math.PI));
        get("/app/e", (req, resp) -> String.valueOf(Math.E));

        HttpServer.main(args);
    }
}
