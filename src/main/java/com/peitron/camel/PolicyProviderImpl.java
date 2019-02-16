package com.peitron.camel;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyRegistry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PolicyProviderImpl implements PolicyProvider {

    private Properties policyProperties;

    private Policy policyUsername;
    private Policy policySAML;
    private Policy policySAMLAuthz;
    private Policy policySAMLCrypto;
    private Policy policySAMLAuthzCrypto;

    //policyProperties has all of the credential files names in resources folder.
    public void setPolicyProperties(Properties policyProperties) {
        this.policyProperties = policyProperties;
    }

    public Policy getUsernamePolicy(Bus cxf) {
        if (null == policyUsername) {
            policyUsername = loadPolicy(policyProperties.getProperty("policy.username"), cxf);
        }
        return policyUsername;
    }

    public Policy getSAMLPolicy(Bus cxf) {
        if (null == policySAML) {
            policySAML = loadPolicy(policyProperties.getProperty("policy.saml"), cxf);
        }
        return policySAML;
    }

    public Policy getSAMLAuthzPolicy(Bus cxf) {
        if (null == policySAMLAuthz) {
            policySAMLAuthz = loadPolicy(policyProperties.getProperty("policy.saml.authz"), cxf);
        }
        return policySAMLAuthz;
    }

    public Policy getSAMLCryptoPolicy(Bus cxf) {
        if (null == policySAMLCrypto) {
            policySAMLCrypto = loadPolicy(policyProperties.getProperty("policy.saml.crypto"), cxf);
        }
        return policySAMLCrypto;
    }

    public Policy getSAMLAuthzCryptoPolicy(Bus cxf) {
        if (null == policySAMLAuthzCrypto) {
            policySAMLAuthzCrypto = loadPolicy(policyProperties.getProperty("policy.saml.authz.crypto"), cxf);
        }
        return policySAMLAuthzCrypto;
    }

    /*registry names are from the interface
    String ID_POLICY_USERNAME_TOKEN = "com.cameltest.token.policy";
    String ID_POLICY_SAML_TOKEN = "com.cameltest.saml.policy";
    String ID_POLICY_SAML_AUTHZ = "com.cameltest.saml.authz.policy";
    String ID_POLICY_SAML_TOKEN_CRYPTO = "com.cameltest.saml.crypto.policy";
    String ID_POLICY_SAML_AUTHZ_CRYPTO = "com.cameltest.saml.authz.crypto.policy";*/
    public void register(Bus cxf) {
        final PolicyRegistry policyRegistry =
                cxf.getExtension(PolicyEngine.class).getRegistry();
        policyRegistry.register(PolicyProvider.ID_POLICY_USERNAME_TOKEN,
                getUsernamePolicy(cxf));
        policyRegistry.register(PolicyProvider.ID_POLICY_SAML_TOKEN,
                getSAMLPolicy(cxf));
        policyRegistry.register(PolicyProvider.ID_POLICY_SAML_AUTHZ,
                getSAMLAuthzPolicy(cxf));
        policyRegistry.register(PolicyProvider.ID_POLICY_SAML_TOKEN_CRYPTO,
                getSAMLCryptoPolicy(cxf));
        policyRegistry.register(PolicyProvider.ID_POLICY_SAML_AUTHZ_CRYPTO,
                getSAMLAuthzCryptoPolicy(cxf));
    }

    //load the policy to the cxf component
    private Policy loadPolicy(String location, Bus cxf) {
        InputStream is = null;
        try {
            is = new FileInputStream(location);
            return cxf.getExtension(PolicyBuilder.class).getPolicy(is);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load policy", e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    // just ignore
                }
            }
        }
    }

}