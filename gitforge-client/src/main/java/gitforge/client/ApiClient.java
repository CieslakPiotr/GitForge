package gitforge.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Calls the GitForge server's REST API, e.g. POST http://localhost:8080/api/repos.
 * Uses Java's built-in HttpClient, so no HTTP library is needed.
 *
 * @param baseUrl server address, e.g. "http://localhost:8080"
 */
record ApiClient(String baseUrl) {

    /** One shared HTTP client for all calls. Gives up connecting after 5 seconds if the server is down. */
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /** Creates a repository: POST /api/repos with body {"name":"<name>"}. */
    HttpResponse<String> initRepo(String name) throws IOException, InterruptedException {
        return post("/api/repos", "{\"name\":" + json(name) + "}");
    }

    /** Sends a POST with a JSON body and returns the response (status code + body text). */
    private HttpResponse<String> post(String path, String jsonBody) throws IOException, InterruptedException {
        // replaceAll removes a trailing "/" from the base URL, so "http://host/" + "/api" doesn't become "//api".
        var request = HttpRequest.newBuilder(URI.create(baseUrl.replaceAll("/+$", "") + path))
                .timeout(Duration.ofSeconds(10))                  // give up if the server doesn't answer in 10 s
                .header("Content-Type", "application/json")       // tells Spring the body is JSON
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return HTTP.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /** Quotes a string as JSON, so any name the user types produces valid JSON: a"b becomes "a\"b". */
    static String json(String value) {
        var sb = new StringBuilder("\"");
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");     // quote -> \"
                case '\\' -> sb.append("\\\\");    // backslash -> \\
                default -> {
                    // Control characters (tab, newline, ...) must be written as \\u0009 etc. in JSON.
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.append('"').toString();
    }
}
