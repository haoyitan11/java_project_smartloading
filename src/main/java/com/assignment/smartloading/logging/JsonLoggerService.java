package com.assignment.smartloading.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class JsonLoggerService {

    private static final String CLICK_LOG_PATH =
            "D:/Intelij Java Project/smartloading/log/user_actions.log";

    private static final String LIKE_LOG_PATH =
            "D:/Intelij Java Project/smartloading/log/user_likes.log";

    private final ObjectWriter writer =
            new ObjectMapper().writerWithDefaultPrettyPrinter();


    /* ============================================================
       GENERIC LOGGER (now supports any event)
    ============================================================ */
    public void logEvent(String path, Map<String, Object> data) {

        try (FileWriter fw = new FileWriter(path, true)) {

            Map<String, Object> ordered = new LinkedHashMap<>();
            ordered.put("logId", UUID.randomUUID().toString());
            ordered.put("timestamp", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            // Write userId early
            ordered.put("userId", data.getOrDefault("userId", "unknown"));

            // Append remaining values alphabetically
            data.keySet().stream()
                    .filter(k -> !k.equals("userId"))
                    .sorted()
                    .forEach(k -> ordered.put(k, data.get(k)));

            fw.write(writer.writeValueAsString(ordered) + System.lineSeparator());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* ============================================================
       SPECIALIZED LOG METHODS
    ============================================================ */
    public void logClick(String userId, String productId, String category) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("event", "product_click");
        map.put("userId", userId);
        map.put("productId", productId);
        map.put("category", category);

        logEvent(CLICK_LOG_PATH, map);
    }

    public void logLike(String userId, String productId, boolean liked) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("event", "product_like_toggle");
        map.put("userId", userId);
        map.put("productId", productId);
        map.put("action", liked ? "LIKE" : "UNLIKE");

        logEvent(LIKE_LOG_PATH, map);
    }
}
