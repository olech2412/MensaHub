package de.olech2412.mensahub.request;

import lombok.Getter;
import lombok.Setter;

/**
 * This class stores information about all possible requests
 * It stores the base url and the class that represents the response
 */
@Setter
@Getter
public class Request {

    // The endpoint of the api
    private String apiEndpoint;

    // get the endpoint of the api
    private Request() {
    }


    public static class RequestBuilder {

        // The endpoint of the api
        private RequestPath apiEndpoint;

        public RequestBuilder() {
        }

        // set the endpoint of the api
        public RequestBuilder setApiEndpoint(RequestPath apiEndpoint) {
            this.apiEndpoint = apiEndpoint;
            return this;
        }

        // build the request
        public Request build() {
            Request request = new Request();
            request.apiEndpoint = this.apiEndpoint.getPath();
            return request;
        }
    }

}
