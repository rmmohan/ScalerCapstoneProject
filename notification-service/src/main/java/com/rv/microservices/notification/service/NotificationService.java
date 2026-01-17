package com.rv.microservices.notification.service;

import com.rv.microservices.order.event.OrderPlacedEvent;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    private static final Tracer tracer = GlobalOpenTelemetry.get()
            .getTracer("com.rv.microservices", "1.0.0");

    private static final TextMapGetter<ConsumerRecord<?, ?>> getter = new TextMapGetter<>() {

        @Override
        public Iterable<String> keys(ConsumerRecord<?, ?> carrier) {
            return () -> java.util.Arrays.stream(carrier.headers().toArray())
                    .map(h -> h.key()).iterator();
        }

        @Override
        public String get(ConsumerRecord<?, ?> carrier, String key) {
            var header = carrier.headers().lastHeader(key);
            return header != null ? new String(header.value()) : null;
        }
    };

    @KafkaListener(
            topics = "order-placed",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        // Extract the parent context from headers
        Context parentContext = GlobalOpenTelemetry.get()
                .getPropagators()
                .getTextMapPropagator()
                .extract(Context.current(), record, getter);

        // Start a span with the extracted trace_id
        Span span = tracer.spanBuilder("kafka-consumer-process")
                .setParent(parentContext)
                .setSpanKind(SpanKind.CONSUMER)
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            log.info("Starting span for message from order-placed topic");
            OrderPlacedEvent orderPlacedEvent = (OrderPlacedEvent) record.value();
            log.info("Got Message from order-placed topic {}", orderPlacedEvent);
            process(orderPlacedEvent, ack);
            log.info("Finished processing message from order-placed topic {}", orderPlacedEvent);
        } catch (Exception e) {
            log.error("Exception occurred while processing message from order-placed topic", e);
            span.recordException(e);
            throw e;
        } finally {
            log.info("Ending span for message from order-placed topic");
            span.end();
        }
    }

    public void process(OrderPlacedEvent orderPlacedEvent, Acknowledgment ack){
        log.info("Got Message from order-placed topic {}", orderPlacedEvent);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("springshop@email.com");
            messageHelper.setTo(orderPlacedEvent.getEmail().toString());
            messageHelper.setSubject(
                String.format(
                    "Your Order with OrderNumber %s is placed successfully",
                    orderPlacedEvent.getOrderNumber()
                )
            );
            messageHelper.setText(
                String.format(
                    """
                        Hi %s,%s

                        Your order with order number %s is now placed successfully.

                        Best Regards
                        Spring Shop
                    """,
                    orderPlacedEvent.getFirstName().toString(),
                    orderPlacedEvent.getLastName().toString(),
                    orderPlacedEvent.getOrderNumber()
                )
            );
        };
        try {
            javaMailSender.send(messagePreparator);
            log.info("Order Notifcation email sent!!");
            ack.acknowledge();
        } catch (MailException e) {
            log.error("Exception occurred when sending mail", e);
            throw new RuntimeException("Exception occurred when sending mail to springshop@email.com", e);
        }
    }
}