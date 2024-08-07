package de.olech2412.mensahub;

import com.google.gson.*;
import de.olech2412.mensahub.models.addons.predictions.PredictionRequest;
import de.olech2412.mensahub.models.addons.predictions.PredictionResult;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.api.APIError;
import de.olech2412.mensahub.models.result.errors.api.APIErrors;
import de.olech2412.mensahub.request.Request;
import de.olech2412.mensahub.request.RequestPath;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class CollaborativeFilteringAPIAdapter {

    private final APIConfiguration apiConfiguration;
    private final Gson gson;

    public CollaborativeFilteringAPIAdapter(APIConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
        this.gson = new Gson();
    }

    public Result<List<Result<PredictionResult, APIError>>, APIError> predict(List<PredictionRequest> requestData) throws IOException {
        Request request = getRequest(RequestPath.PREDICT);
        Result<JsonArray, APIError> result = performRequest(request, gson.toJson(requestData));

        if (!result.isSuccess()) {
            log.error("Error while predicting ratings: {}", result.getError().message());
            return Result.error(result.getError());
        } else {
            JsonArray jsonArray = result.getData();
            List<Result<PredictionResult, APIError>> predictions = new ArrayList<>();

            for (JsonElement element : jsonArray) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.has("predicted_rating")) {
                    PredictionResult predictionResult = gson.fromJson(obj, PredictionResult.class);
                    predictions.add(Result.success(predictionResult));
                } else {
                    APIError apiError = new APIError(obj.get("error").getAsString(), APIErrors.UNKNOWN);
                    predictions.add(Result.error(apiError));
                }
            }

            return Result.success(predictions);
        }
    }

    private Request getRequest(RequestPath requestPath) {
        for (Request request : apiConfiguration.getRequests()) {
            if (request.getApiEndpoint().equals(requestPath.getPath())) {
                return new Request.RequestBuilder().setApiEndpoint(requestPath).build();
            }
        }
        throw new IllegalArgumentException("Request not found");
    }

    private Result<JsonArray, APIError> performRequest(Request request, String jsonPayload) throws IOException {
        URL url = new URL(apiConfiguration.getBaseUrl() + request.getApiEndpoint());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);
        con.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = con.getResponseCode();
        InputStream responseStream = (status == 200) ? con.getInputStream() : con.getErrorStream();
        String responseString = IOUtils.toString(responseStream, StandardCharsets.UTF_8);

        if (status != 200) {
            return Result.error(new APIError(responseString, APIErrors.UNKNOWN));
        }

        JsonArray responseObject = JsonParser.parseString(responseString).getAsJsonArray();
        return Result.success(responseObject);
    }

    public boolean isAPIAvailable() {
        try {
            URL url = new URL(apiConfiguration.getBaseUrl() + RequestPath.HEALTH);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int status = con.getResponseCode();
            return (status == 200);
        } catch (IOException e) {
            log.error("Error while checking API availability: {}", e.getMessage());
            return false;
        }
    }
}