package com.learning.boot.error;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse (
        Instant timestamp,
        int status,
        String message,
        Map<String, String> errors
){
}
