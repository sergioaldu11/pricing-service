package com.inditex.pricing;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import tools.jackson.databind.json.JsonMapper;

class PackagedApplicationIT {
    @Test
    @Timeout(60)
    void executableJarStartsWithItsOwnDependenciesAndSampleDatabase() throws Exception {
        Path java = Path.of(System.getProperty("java.home"), "bin", "java");
        Path jar = Path.of(System.getProperty("application.jar")).toAbsolutePath();
        Path log = Path.of("target", "packaged-application.log");
        Process process =
                new ProcessBuilder(java.toString(), "-jar", jar.toString(), "--server.port=0")
                        .redirectErrorStream(true)
                        .redirectOutput(log.toFile())
                        .start();
        try {
            int port = awaitPort(process, log);
            try (var client =
                    HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
                var request =
                        HttpRequest.newBuilder(
                                        URI.create(
                                                "http://localhost:"
                                                        + port
                                                        + "/api/prices?brandId=1&productId=35455&applicationDate=2020-06-14T16:00:00"))
                                .timeout(Duration.ofSeconds(10))
                                .GET()
                                .build();
                var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode(), response.body());
                var json = JsonMapper.builder().build().readTree(response.body());
                assertEquals(2, json.path("priceList").asInt());
                assertEquals("EUR", json.path("currency").asString());
                assertEquals(25.45, json.path("price").asDouble());
            }
        } finally {
            process.destroy();
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                assertTrue(
                        process.waitFor(5, TimeUnit.SECONDS),
                        "Packaged application did not terminate");
            }
        }
    }

    private int awaitPort(Process process, Path log) throws Exception {
        var pattern = Pattern.compile("Tomcat started on port (\\d+)");
        long deadline = System.nanoTime() + Duration.ofSeconds(40).toNanos();
        while (System.nanoTime() < deadline) {
            String output = Files.readString(log);
            assertTrue(process.isAlive(), output);
            var match = pattern.matcher(output);
            if (match.find()) return Integer.parseInt(match.group(1));
            Thread.sleep(100);
        }
        throw new AssertionError("Application startup timed out: " + Files.readString(log));
    }
}
