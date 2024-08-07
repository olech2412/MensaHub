package de.olech2412.mensahub;

import de.olech2412.mensahub.request.Request;
import de.olech2412.mensahub.request.RequestPath;
import lombok.Getter;
import lombok.Setter;

import java.net.Proxy;
import java.util.List;

@Getter
public class APIConfiguration {

    @Setter
    private String baseUrl = "http://localhost:5000"; // Beispiel-URL für die lokale Flask-API

    private List<Request> requests;

    @Setter
    private Proxy proxy;

    public APIConfiguration() {
        buildRequestList();
    }

    private void buildRequestList() {
        requests = List.of(
                new Request.RequestBuilder()
                        .setApiEndpoint(RequestPath.PREDICT)
                        .build()
        );
    }
}