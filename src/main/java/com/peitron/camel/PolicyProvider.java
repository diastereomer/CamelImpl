package com.peitron.camel;

import org.apache.cxf.Bus;
import org.apache.neethi.Policy;

public interface PolicyProvider {
    //need to modify the server to use the below fields
    String ID_POLICY_USERNAME_TOKEN = "com.cameltest.token.policy";
    String ID_POLICY_SAML_TOKEN = "com.cameltest.saml.policy";
    String ID_POLICY_SAML_AUTHZ = "com.cameltest.saml.authz.policy";
    String ID_POLICY_SAML_TOKEN_CRYPTO = "com.cameltest.saml.crypto.policy";
    String ID_POLICY_SAML_AUTHZ_CRYPTO = "com.cameltest.saml.authz.crypto.policy";

    Policy getUsernamePolicy(Bus cxf);

    Policy getSAMLPolicy(Bus cxf);

    Policy getSAMLAuthzPolicy(Bus cxf);

    Policy getSAMLCryptoPolicy(Bus cxf);

    Policy getSAMLAuthzCryptoPolicy(Bus cxf);

    void register(Bus cxf);

}


