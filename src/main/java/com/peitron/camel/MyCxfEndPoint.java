package com.peitron.camel;

import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.neethi.Policy;
import org.apache.wss4j.dom.validate.JAASUsernameTokenValidator;

public class MyCxfEndPoint {

    // MyAbstractRouteBuilders myCEs is the instance of MyAbstractRouteBuilders, which is calling this method
    protected static CxfEndpoint getCxfEndpoint(String uri, boolean isProvider, boolean useAuthorization,
                                                boolean usePropagateSamlAP, MyAbstractRouteBuilder myCEs, String... token) throws Exception{

        //generate the endpoint
        final CxfEndpoint cxfEndpoint = (CxfEndpoint) myCEs.endpoint(uri);

        //  check username and password , UsernameToken must be the first token
        if (myCEs.inOSGi) {
            if (isProvider && token != null && "UsernameToken".equals(token[0])) {
                addUsernameTokenProvider(cxfEndpoint, myCEs);
            }
        }
        return cxfEndpoint;
    }

    // MyAbstractRouteBuilders myCEs is the instance of MyAbstractRouteBuilders, which is calling this method
    //pass the MyAbstractRouteBuilders instance to provide the policyProvider
    private static void addUsernameTokenProvider(CxfEndpoint cxfEndpoint, MyAbstractRouteBuilder myCEs) throws Exception{
        JAASUsernameTokenValidator validator = new JAASUsernameTokenValidator();
        validator.setContextName("karaf");
        cxfEndpoint.setProperties(java.util.Collections.<String, Object>singletonMap(
                                org.apache.cxf.ws.security.SecurityConstants.USERNAME_TOKEN_VALIDATOR,validator));
        Policy policy= null;
        if(myCEs.getCxfPolicyType().equals("policy.username"))
            policy=myCEs.policyProvider.getUsernamePolicy(cxfEndpoint.getBus());
        else if(myCEs.getCxfPolicyType().equals("policy.saml"))
            policy=myCEs.policyProvider.getSAMLPolicy(cxfEndpoint.getBus());
        else if(myCEs.getCxfPolicyType().equals("policy.saml.authz"))
            policy=myCEs.policyProvider.getSAMLAuthzPolicy(cxfEndpoint.getBus());
        else if(myCEs.getCxfPolicyType().equals("policy.saml.crypto"))
            policy=myCEs.policyProvider.getSAMLCryptoPolicy(cxfEndpoint.getBus());
        else if(myCEs.getCxfPolicyType().equals("policy.saml.authz.crypto"))
            myCEs.policyProvider.getSAMLAuthzCryptoPolicy(cxfEndpoint.getBus());
        if(policy != null)
        cxfEndpoint.getFeatures().add(
                new org.apache.cxf.ws.policy.WSPolicyFeature(myCEs.policyProvider.getUsernamePolicy(cxfEndpoint.getBus())));
        else throw new NullPointerException("the Policy is Null");
    }
}
