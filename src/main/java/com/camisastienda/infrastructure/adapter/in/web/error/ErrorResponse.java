package com.camisastienda.infrastructure.adapter.in.web.error;

import java.time.Instant;
import java.util.Map;

/** Formato JSON estándar de error de la API. */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {
}
