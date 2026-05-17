package com.Mybuddy.Myb.Event;

import org.springframework.context.ApplicationEvent;
import lombok.Getter;

@Getter
public class PaymentWebhookEvent extends ApplicationEvent {

    private String paymentId;
    private String topic;

    public PaymentWebhookEvent(Object source, String paymentId, String topic){
        super(source);
        this.paymentId = paymentId;
        this.topic = topic;
    }
}