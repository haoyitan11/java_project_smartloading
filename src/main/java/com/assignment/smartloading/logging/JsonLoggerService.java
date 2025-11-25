package com.assignment.smartloading.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JsonLoggerService {

    private final ObjectWriter writer =
            new ObjectMapper().writerWithDefaultPrettyPrinter();

    private static final String CLICK_LOG =
            "D:/Intelij Java Project/smartloading/log/user_clicks.log";

    private static final String LIKE_LOG =
            "D:/Intelij Java Project/smartloading/log/user_likes.log";

    /* ======================================================
       GENERIC ORDERED LOGGER
    ====================================================== */
    private void write(String path, Map<String, Object> data) {
        try (FileWriter fw = new FileWriter(path, true)) {

            Map<String, Object> ordered = new LinkedHashMap<>();
            ordered.put("logId", UUID.randomUUID().toString());
            ordered.put("timestamp",
                    ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            ordered.put("userId", data.getOrDefault("userId", "unknown"));

            data.keySet().stream()
                    .filter(k -> !k.equals("userId"))
                    .sorted()
                    .forEach(k -> ordered.put(k, data.get(k)));

            fw.write(writer.writeValueAsString(ordered) + System.lineSeparator());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ======================================================
       CLICK LOGGER
    ====================================================== */
    public void logClick(String userId, String productId, String category) {
        write(CLICK_LOG, Map.of(
                "event", "click",
                "userId", userId,
                "productId", productId,
                "category", category
        ));
    }

    /* ======================================================
       LIKE LOGGER
    ====================================================== */
    public void logLike(String userId, String productId, boolean liked) {
        write(LIKE_LOG, Map.of(
                "event", liked ? "like" : "unlike",
                "userId", userId,
                "productId", productId
        ));
    }
}
