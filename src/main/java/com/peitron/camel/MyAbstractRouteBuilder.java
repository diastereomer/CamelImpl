package com.peitron.camel;

import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.common.header.CxfHeaderFilterStrategy;
import org.apache.camel.main.Main;

import java.util.Map;
import java.util.Properties;
import java.util.Random;


public abstract class MyAbstractRouteBuilder extends org.apache.camel.builder.RouteBuilder {
    protected boolean inOSGi;

    protected Main main;

    protected String contextStr = "default";

    protected final Properties context_param = new Properties();

    protected final MyContext context = new MyContext();

    protected final static CxfHeaderFilterStrategy consumerSoapHeaderFilter = new org.apache.camel.component.cxf.common.header.CxfHeaderFilterStrategy();

    protected int portStats = -1;

    protected String clientHost = "localhost";

    protected String pid;

    public String getCxfPolicyType() {
        return cxfPolicyType;
    }

    public void setCxfPolicyType(String cxfPolicyType) {
        this.cxfPolicyType = cxfPolicyType;
    }

    private String cxfPolicyType;

    /*policyProvider for cxf user token validation*/
    protected PolicyProvider policyProvider;

    /*set the PolicyProvider when use the cxf user token validation, when implement MyAbstractRouteBuilder and use the validation for the access of the service, policyProvider must be set a value of PolicyProviderImpl
    also must set the file path of the policy xml for policy.username, policy.saml, policy.saml.authz, policy.saml.crypto, policy.saml.authz.crypto in the policyProperties*/
    protected void setPolicyProvider(PolicyProvider policyProvider) {
        this.policyProvider = policyProvider;
    }

    /* ESB Service Activity Monitor Feature
    to use the feature in configure() method, when create the cxf endpoint, add code

    if (eventFeature != null) {
			endpoint_cCXF_1.getFeatures().add(eventFeature);
		}*/
    private org.apache.cxf.feature.Feature eventFeature;

    public void setEventFeature(org.apache.cxf.feature.Feature eventFeature) {
        this.eventFeature = eventFeature;
    }


    protected void run() throws Exception {
        main = new MyCamelMain();
        // add route
        main.addRouteBuilder(this);
        main.run();
    }

    public void stop() throws Exception {
        if (main != null) {
            main.stop();
        }
    }

    public void shutdown() throws Exception {
        if (main != null) {
            main.shutdown();
        }
    }

    protected String propertyToString(Object obj) {
        if (obj != null && obj instanceof java.util.Date) {
            return String.format("yyyy-MM-dd HH:mm:ss;%tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", obj);
        } else {
            return String.valueOf(obj);
        }
    }

    public void setArguments(Map<String, String> arguments) {
        inOSGi = true;
        final String newContext = arguments.remove("context");
        if (null != newContext) {
            contextStr = newContext;
        }
        context_param.putAll(arguments);
    }

    /*args for instance: --context=default , --context_param, ABC=123,..., stat_port=8040, client_host=localhost, pid=1234
    declare which context will be used in the route
    get context from the app parameters, the MaiApp will call this method to start the route*/
    public int runRoute(String[] args) {
        String lastStr = "";
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--context_param")) {
                lastStr = arg;
            }
            //MainApp does't pass the context_params, or the arg is not context_params
            else if (lastStr.equals("")) {
                evalParam(arg);
            }

            //evaluate the context_params
            else {
                evalParam(lastStr + " " + arg);
                lastStr = "";
            }
        }
        // get pid, if pid is null, generate a random pid
        if (pid == null) {
            Random random = new Random();
            pid = Integer.toString(random.nextInt(10)) + Integer.toString(random.nextInt(10)) + Integer.toString(random.nextInt(10)) +
                    Integer.toString(random.nextInt(10)) + Integer.toString(random.nextInt(10)) + Integer.toString(random.nextInt(10));
        }
        //call main.run()
        try {
            run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    /* evaluate the args from MainApp, which are passed to runRoute method*/
    protected void evalParam(String arg) {

        // set which context properties in resource will be used for instance set the context to default.properties
        if (arg.startsWith("--context=")) {
            contextStr = arg.substring(10);
        } else if (arg.startsWith("--context_param")) {
            String keyValue = arg.substring(16);
            int index = -1;
            if (keyValue != null && (index = keyValue.indexOf('=')) > -1) {
                context_param.put(keyValue.substring(0, index), keyValue.substring(index + 1));
            }
        } else if (arg.startsWith("--stat_port=")) {
            String portStatsStr = arg.substring(12);
            if (portStatsStr != null && !portStatsStr.equals("null")) {
                portStats = Integer.parseInt(portStatsStr);
            }
        } else if (arg.startsWith("--client_host=")) {
            clientHost = arg.substring(14);
        } else if (arg.startsWith("--pid=")) {
            pid = arg.substring(6);
        }
    }


    /*create a CXF endpoint*/
    protected CxfEndpoint getCxfEndpoint(String uri, boolean isProvider, boolean useAuthorization,
                                         boolean usePropagateSamlAP, String... token) throws Exception {
        return MyCxfEndPoint.getCxfEndpoint(uri, isProvider, useAuthorization, usePropagateSamlAP, this, token);
    }

    /* get the context from the resources properties for example contextName = resources/default will read the properties named default in resources folder*/
    protected void readContextValues(String contextName) {
        try {
            // read the context file to context variable
            java.io.InputStream inContext = MyAbstractRouteBuilder.class.getClassLoader().getResourceAsStream(contextName + ".properties");

            if (inContext != null) {
                context.load(inContext);
                inContext.close();
            } else {
                // print info and job continue to run, for case: context_param
                // is not empty.
                System.err.println("Could not find the context " + contextName);
            }

            if (!context_param.isEmpty()) {
                context.putAll(context_param);
            }

            //set the MyContext fields use the properties from the file
            context.setContextFields();
        } catch (java.io.IOException ie) {
            System.err.println("Could not load context " + contextName);
            ie.printStackTrace();
        }
    }
}
