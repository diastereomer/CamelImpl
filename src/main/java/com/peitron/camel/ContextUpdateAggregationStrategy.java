package com.peitron.camel;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class ContextUpdateAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // TODO Auto-generated method stub
        newExchange.getIn().setBody("<result>successfully updated the context</result>");
        return newExchange;
    }
}
