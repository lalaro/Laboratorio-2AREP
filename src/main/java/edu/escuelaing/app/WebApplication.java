package edu.escuelaing.app;

import java.io.IOException;
import java.net.URISyntaxException;

import static edu.escuelaing.app.HttpServer.staticfiles;
import static edu.escuelaing.app.HttpServer.get;

public class WebApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
        staticfiles("/webroot");
        get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
        get("/app/pi", (req, resp) -> {
            return String.valueOf(Math.PI);
        });
        get("/app/e", (req, resp) -> {
            return String.valueOf(Math.E);});

        HttpServer.main(args);
    }
}