package com.peitron.camel;

import org.apache.camel.CamelContext;
import com.peitron.camel.MyCxfEndPoint;

import java.util.HashMap;
import java.util.Map;


/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends MyAbstractRouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */


    //the cxf endpoint needs to use business correlation, just enable and use the below correlationIDCallbackHandler
    //if there are many cxf endpoint using business correlations, just create more correlationIDCallbackHandler
    protected final CorrelationIDCallbackHandler correlationIDCallbackHandler_cCXF_1 = new CorrelationIDCallbackHandlerImpl();

    @Override
    public void configure() {
        /*get the context to the registry*/
        final/* org.apache.camel.model.Model */ CamelContext camelContext = getContext();
        final org.apache.camel.impl.SimpleRegistry registry = new org.apache.camel.impl.SimpleRegistry();
        final org.apache.camel.impl.CompositeRegistry compositeRegistry = new org.apache.camel.impl.CompositeRegistry();
        compositeRegistry.addRegistry(camelContext.getRegistry());
        compositeRegistry.addRegistry(registry);
        ((org.apache.camel.impl.DefaultCamelContext) camelContext)
                .setRegistry(compositeRegistry);

        // read context values
        readContextValues(contextStr);

        // add the customized context variable
        Map<String, String> stateCodes = new HashMap<String, String>();
        Map<String, String> codeStates = new HashMap<String, String>();
        context.put("stateCodes", stateCodes);
        context.put("codeStates", codeStates);

        consumerSoapHeaderFilter.setRelayHeaders(false);
        registry.put("CXF_PAYLOAD_HEADER_FILTER", consumerSoapHeaderFilter);
        // CXF endpoint for cCXF_1
        org.apache.camel.component.cxf.CxfEndpoint endpoint_cCXF_1;
        try {
            endpoint_cCXF_1 = getCxfEndpoint(
                    "cxf://"
                            + "http://localhost:8050/services/policysearch"
                            + "?dataFormat=PAYLOAD"
                            + "&allowStreaming=false"
                            + "&wsdlURL="
                            + getClass().getResource("/PolicySearch0.6c.wsdl").toString()
                            + "&serviceName="
                            + "{http://camel.peitron.com/}policySearchWebServiceImplService"
                            + "&endpointName="
                            + "{http://camel.peitron.com/}policySearchWebServiceImplPort",
                    true, false, false, (String[]) null);
        } catch (Exception e) {
            log.error(e.getMessage());
            endpoint_cCXF_1 = new org.apache.camel.component.cxf.CxfEndpoint();
        }
        // http://jira.talendforge.org/browse/TESB-3850
        // !"true".equals(useRegistry) -
        // https://jira.talendforge.org/browse/TESB-10725
        // CXF endpoint for cCXF_2
        org.apache.camel.component.cxf.CxfEndpoint endpoint_cCXF_2;
        try {
            endpoint_cCXF_2 = getCxfEndpoint(
                    "cxf://"
                            + "http://192.168.1.5:10021/web/services/WINS_Policy_SearchService/WINS_Policy_Search"
                            + "?dataFormat=PAYLOAD"
                            + "&allowStreaming=false"
                            + "&wsdlURL="
                            + getClass().getResource("/PolicyRetrieval.wsdl")
                            .toString()
                            + "&serviceName="
                            + "{http://wins_policy_search.wsbeans.iseries/}WINS_Policy_Search"
                            + "&endpointName="
                            + "{http://wins_policy_search.wsbeans.iseries/}WINS_Policy_SearchServicesPort"
                            + "&defaultOperationNamespace="
                            + javax.xml.namespace.QName
                            .valueOf(
                                    "{http://wins_policy_search.wsbeans.iseries/}cwrappiws1")
                            .getNamespaceURI()
                            + "&defaultOperationName="
                            + javax.xml.namespace.QName
                            .valueOf(
                                    "{http://wins_policy_search.wsbeans.iseries/}cwrappiws1")
                            .getLocalPart()
                            + "&headerFilterStrategy=#CXF_PAYLOAD_HEADER_FILTER"
                            + "&properties.id=" + "cCXF_2", false, false, false,
                    (String[]) null);
        } catch (Exception e) {
            log.error(e.getMessage());
            endpoint_cCXF_2 = new org.apache.camel.component.cxf.CxfEndpoint();
        }
        // http://jira.talendforge.org/browse/TESB-3850
        // !"true".equals(useRegistry) -
        // https://jira.talendforge.org/browse/TESB-10725
        from(endpoint_cCXF_1).routeId("Sketch_Board6_cCXF_1")
                .to("seda:request").id("Sketch_Board6_cSEDA_1");
        from("seda:request" + "?concurrentConsumers=" + 4)
                .routeId("Sketch_Board6_cSEDA_2")
                .to("xslt://testXSL.xsl")
                .id("Sketch_Board6_cMessagingEndpoint_1")

                .removeHeaders("*")
                .id("Sketch_Board6_cJavaDSLProcessor_1")
                .log(org.apache.camel.LoggingLevel.OFF, "Sketch_Board6.cLog_1",
                        "${in.headers} \n ${in.body}")

                .id("Sketch_Board6_cLog_1")
                .to(endpoint_cCXF_2)
                .id("Sketch_Board6_cCXF_2")
                .log(org.apache.camel.LoggingLevel.OFF, "Sketch_Board6.cLog_2",
                        "${in.body}")

                .id("Sketch_Board6_cLog_2")
                .to("xslt://testXSL1.xsl")
                .id("Sketch_Board6_cMessagingEndpoint_2")
                .log(org.apache.camel.LoggingLevel.WARN, "Sketch_Board6.cLog_3",
                        "${in.body}")

                .id("Sketch_Board6_cLog_3");
    }

}
