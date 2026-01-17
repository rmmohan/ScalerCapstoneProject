package com.rv.microservices.order.config.kafka;

import io.opentelemetry.api.trace.Span;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class KafkaTracingProducerInterceptor implements ProducerInterceptor<String, Object> {

    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> record) {
        Span span = Span.current(); // Get the currently active span from the implicit context
        if (span.getSpanContext().isValid()) {
            String traceId = span.getSpanContext().getTraceId();

            if (traceId != null) {                record.headers().add(
                        "trace_id",
                        traceId.getBytes(StandardCharsets.UTF_8)
                );
            }
        }

        return record;
    }

    @Override public void close() {}
    @Override public void configure(Map<String, ?> configs) {}
}