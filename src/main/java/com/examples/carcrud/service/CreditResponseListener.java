package com.examples.carcrud.service;

import com.examples.carcrud.config.RabbitMQConfig;
import com.examples.carcrud.dto.CreditResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CreditResponseListener {

    private final CreditRequestPublisher creditRequestPublisher;

    public CreditResponseListener(CreditRequestPublisher creditRequestPublisher) {
        this.creditRequestPublisher = creditRequestPublisher;
    }

    @RabbitListener(queues = RabbitMQConfig.CREDIT_RESPONSE_QUEUE)
    public void handleBankResponse(CreditResponse response) {
        System.out.println("[Rental Service] Received bank response: approved=" + response.isApproved()
                + " for user=" + response.getUsername()
                + " message=" + response.getMessage());
        creditRequestPublisher.completeRequest(response);
    }
}
