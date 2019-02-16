package com.peitron.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;

public class MyInterceptStrategy implements org.apache.camel.spi.InterceptStrategy {
    public Processor wrapProcessorInInterceptors(CamelContext context, final ProcessorDefinition<?> node, final Processor target, Processor nextTarget) throws Exception {
        return new org.apache.camel.processor.DelegateAsyncProcessor(target) {
            public boolean process(org.apache.camel.Exchange exchange, org.apache.camel.AsyncCallback callback) {
                final String connection = targetNodeToConnectionMap
                        .get(node.getId());
                if (null != connection) {
                    runStat.updateStatOnConnection(
                            targetNodeToConnectionMap
                                    .get(node.getId()),
                            routines.system.RunStat.RUNNING,
                            1);
                }
                return super
                        .process(exchange, callback);
            }
        };
    }
}
}
