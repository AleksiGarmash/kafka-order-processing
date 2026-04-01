package com.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class OrderStatusEvent {

    private final String status;
    private final String date;

    @JsonCreator
    public OrderStatusEvent(
            @JsonProperty("product") String status,
            @JsonProperty("quantity") String date) {
        this.status = status;
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "OrderStatusEvent{" +
                "status='" + status + '\'' +
                ", date=" + date +
                '}';
    }
}
