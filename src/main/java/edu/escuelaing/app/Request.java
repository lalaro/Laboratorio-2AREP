package edu.escuelaing.app;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private Map<String, String> parameters;

    public Request(String queryString) {
        parameters = new HashMap<>();
        // Aquí procesas el queryString para llenar el mapa de parámetros
        if (queryString != null) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
        }
    }

    public String getValues(String key) {
        return parameters.get(key);
    }
}
