package com.peitron.camel;

import org.apache.camel.CamelContext;

public class MyCamelMain extends org.apache.camel.main.Main {
    public MyCamelMain() {
        super();
    }

    // override the createContext method to call spring bean
    @Override
    protected CamelContext createContext() {
        // get the spring bean in Beans.xml
        final org.apache.camel.impl.DefaultCamelContext camelContext = new org.apache.camel.spring.SpringCamelContext(
                new org.springframework.context.support.ClassPathXmlApplicationContext(
                        "/Beans.xml"));
        //give a context name
        camelContext.setName("retest");

        // add jmx notifier from the spring jmx bean.
        java.util.Collection<org.apache.camel.management.JmxNotificationEventNotifier> jmxEventNotifiers = camelContext
                .getRegistry().findByType(org.apache.camel.management.JmxNotificationEventNotifier.class);
        if (jmxEventNotifiers != null && !jmxEventNotifiers.isEmpty()) {
            camelContext.getManagementStrategy().addEventNotifier(
                    jmxEventNotifiers.iterator().next());
        }
        return camelContext;
    }
}
