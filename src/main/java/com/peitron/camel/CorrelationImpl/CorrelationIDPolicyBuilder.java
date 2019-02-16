package com.peitron.camel.CorrelationImpl;

import org.apache.neethi.Assertion;
import org.apache.neethi.AssertionBuilderFactory;
import org.apache.neethi.builders.AssertionBuilder;

import org.w3c.dom.Element;
import javax.xml.namespace.QName;

public class CorrelationIDPolicyBuilder implements AssertionBuilder<Element>{
    public static final String NAMESPACE = "http://com.cameltest.pmv1/policy/assertion";

    public static final String CORRELATION_ID_NAME = "CorrelationID";

    public static final QName CORRELATION_ID = new QName(NAMESPACE, CORRELATION_ID_NAME);

    @Override
    public Assertion build(Element element, AssertionBuilderFactory factory)
            throws IllegalArgumentException {
        return new CorrelationIDAssertion(element);
    }

    @Override
    public QName[] getKnownElements() {
        return new QName[]{CORRELATION_ID};
    }
}
