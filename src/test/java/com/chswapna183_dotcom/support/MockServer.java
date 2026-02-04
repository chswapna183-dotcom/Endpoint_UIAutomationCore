package com.chswapna183_dotcom.support;

import com.chswapna183_dotcom.api.payload.UserPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public final class MockServer {
    private static final Object LOCK = new Object();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final AtomicInteger REF_COUNT = new AtomicInteger(0);
    private static final AtomicInteger USER_ID_SEQUENCE = new AtomicInteger(1000);
    private static final Map<String, UserPayload> USERS = new ConcurrentHashMap<>();

    private static HttpServer server;
    private static int port;

    private MockServer() {
    }

    public static void start() {
        synchronized (LOCK) {
            if (server != null) {
                REF_COUNT.incrementAndGet();
                return;
            }

            try {
                server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to start mock server", e);
            }

            port = server.getAddress().getPort();
            server.setExecutor(Executors.newCachedThreadPool());

            server.createContext("/api/users", MockServer::handleUsersApi);
            server.createContext("/login", MockServer::handleLogin);
            server.createContext("/secure", MockServer::handleSecure);
            server.createContext("/logout", MockServer::handleLogout);

            server.start();
            REF_COUNT.set(1);
        }
    }

    public static void stop() {
        synchronized (LOCK) {
            if (server == null) {
                return;
            }

            int remaining = REF_COUNT.decrementAndGet();
            if (remaining > 0) {
                return;
            }

            server.stop(0);
            server = null;
            USERS.clear();
        }
    }

    public static String getBaseUrl() {
        synchronized (LOCK) {
            if (server == null) {
                throw new IllegalStateException("Mock server is not started");
            }
            return "http://localhost:" + port;
        }
    }

    private static void handleUsersApi(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath(); // /api/users or /api/users/{id}

        String id = extractOptionalId(path, "/api/users");
        if (id == null) {
            handleUsersCollection(exchange, method);
            return;
        }
        handleUserById(exchange, method, id);
    }

    private static void handleUsersCollection(HttpExchange exchange, String method) throws IOException {
        if ("POST".equalsIgnoreCase(method)) {
            String body = readBody(exchange);
            UserPayload incoming = MAPPER.readValue(body, UserPayload.class);

            String id = String.valueOf(USER_ID_SEQUENCE.incrementAndGet());
            UserPayload created = new UserPayload(
                    id,
                    incoming.getName(),
                    incoming.getJob(),
                    Instant.now().toString(),
                    null
            );

            USERS.put(id, created);
            sendJson(exchange, 201, created);
            return;
        }

        if ("GET".equalsIgnoreCase(method)) {
            Map<String, Object> response = new HashMap<>();
            response.put("total", USERS.size());
            response.put("data", USERS.values());
            sendJson(exchange, 200, response);
            return;
        }

        sendText(exchange, 405, "Method Not Allowed");
    }

    private static void handleUserById(HttpExchange exchange, String method, String id) throws IOException {
        if ("GET".equalsIgnoreCase(method)) {
            UserPayload user = USERS.get(id);
            if (user == null) {
                sendText(exchange, 404, "User not found");
                return;
            }
            sendJson(exchange, 200, user);
            return;
        }

        if ("PUT".equalsIgnoreCase(method)) {
            String body = readBody(exchange);
            UserPayload incoming = MAPPER.readValue(body, UserPayload.class);

            UserPayload updated = new UserPayload(
                    id,
                    incoming.getName(),
                    incoming.getJob(),
                    USERS.get(id) != null ? USERS.get(id).getCreatedAt() : null,
                    Instant.now().toString()
            );

            USERS.put(id, updated);
            sendJson(exchange, 200, updated);
            return;
        }

        if ("DELETE".equalsIgnoreCase(method)) {
            USERS.remove(id);
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        sendText(exchange, 405, "Method Not Allowed");
    }

    private static void handleLogin(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            sendHtml(exchange, 200, loginHtml(null));
            return;
        }

        if ("POST".equalsIgnoreCase(method)) {
            String formBody = readBody(exchange);
            Map<String, String> form = parseUrlEncodedForm(formBody);

            String username = form.getOrDefault("username", "");
            String password = form.getOrDefault("password", "");

            if (Objects.equals("tomsmith", username) && Objects.equals("SuperSecretPassword!", password)) {
                String session = UUID.randomUUID().toString();
                Headers headers = exchange.getResponseHeaders();
                headers.add("Set-Cookie", "SESSION=" + session + "; Path=/");
                headers.add("Location", "/secure");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();
                return;
            }

            sendHtml(exchange, 200, loginHtml("Your username is invalid!"));
            return;
        }

        sendText(exchange, 405, "Method Not Allowed");
    }

    private static void handleSecure(HttpExchange exchange) throws IOException {
        if (!hasSessionCookie(exchange.getRequestHeaders())) {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
            return;
        }

        sendHtml(exchange, 200, secureHtml("You logged into a secure area!"));
    }

    private static void handleLogout(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Set-Cookie", "SESSION=; Path=/; Max-Age=0");
        headers.add("Location", "/login");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private static boolean hasSessionCookie(Headers headers) {
        String cookie = headers.getFirst("Cookie");
        return cookie != null && cookie.contains("SESSION=");
    }

    private static String loginHtml(String message) {
        String flash = "";
        if (message != null && !message.isBlank()) {
            flash = "<div id=\"flash\">" + escapeHtml(message) + " <a class=\"close\" href=\"#\">×</a></div>";
        }
        return """
                <!DOCTYPE html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8">
                    <title>Login Page</title>
                  </head>
                  <body>
                    <div id="content">
                      <h2>Login Page</h2>
                      %s
                      <form action="/login" method="post">
                        <label for="username">Username</label>
                        <input id="username" name="username" type="text"/>
                        <label for="password">Password</label>
                        <input id="password" name="password" type="password"/>
                        <button type="submit">Login</button>
                      </form>
                    </div>
                  </body>
                </html>
                """.formatted(flash);
    }

    private static String secureHtml(String message) {
        String flash = "";
        if (message != null && !message.isBlank()) {
            flash = "<div id=\"flash\">" + escapeHtml(message) + " <a class=\"close\" href=\"#\">×</a></div>";
        }
        return """
                <!DOCTYPE html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8">
                    <title>Secure Area</title>
                  </head>
                  <body>
                    <div id="content">
                      <h2>Secure Area</h2>
                      %s
                      <a class="button secondary radius" href="/logout">Logout</a>
                    </div>
                  </body>
                </html>
                """.formatted(flash);
    }

    private static String extractOptionalId(String path, String base) {
        if (path == null || !path.startsWith(base)) {
            return null;
        }
        if (path.equals(base)) {
            return null;
        }
        if (!path.startsWith(base + "/")) {
            return null;
        }
        String id = path.substring((base + "/").length()).trim();
        return id.isEmpty() ? null : id;
    }

    private static Map<String, String> parseUrlEncodedForm(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isBlank()) {
            return map;
        }
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            if (pair.isBlank()) {
                continue;
            }
            String[] kv = pair.split("=", 2);
            String key = urlDecode(kv[0]);
            String value = kv.length > 1 ? urlDecode(kv[1]) : "";
            map.put(key, value);
        }
        return map;
    }

    private static String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] bytes = MAPPER.writeValueAsBytes(body);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendHtml(HttpExchange exchange, int statusCode, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

