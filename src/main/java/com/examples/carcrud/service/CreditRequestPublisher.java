package com.examples.carcrud.service;

import com.examples.carcrud.config.RabbitMQConfig;
import com.examples.carcrud.dto.CreditRequest;
import com.examples.carcrud.dto.CreditResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class CreditRequestPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<CreditResponse>> pendingRequests = new ConcurrentHashMap<>();

    public CreditRequestPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Sends a credit verification request to the bank and waits for the response.
     */
    public CreditResponse verifyCreditAndWait(String username, BigDecimal amount, String operationType) {
        String correlationId = UUID.randomUUID().toString();
        CreditRequest request = new CreditRequest(correlationId, username, amount, operationType);

        CompletableFuture<CreditResponse> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        rabbitTemplate.convertAndSend(RabbitMQConfig.CREDIT_REQUEST_QUEUE, request);

        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            pendingRequests.remove(correlationId);
            throw new RuntimeException("Bank credit verification timed out or failed");
        }
    }

    /**
     * Called by the CreditResponseListener when a response arrives from the bank.
     */
    public void completeRequest(CreditResponse response) {
        CompletableFuture<CreditResponse> future = pendingRequests.remove(response.getCorrelationId());
        if (future != null) {
            future.complete(response);
        }
    }
}
