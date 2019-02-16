package com.peitron.camel;

public class CorrelationIDCallbackHandlerImpl implements CorrelationIDCallbackHandler {
    private String correlationId;

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
