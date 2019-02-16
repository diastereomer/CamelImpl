package com.peitron.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.util.FileUtil;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.ResourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyXsltUriResolver implements URIResolver {
    private static final Logger LOG = LoggerFactory.getLogger(MyXsltUriResolver.class);

    public MyXsltUriResolver() {
        super();
        camelContext = null;
        baseScheme = "classpath:";
        context = null;
    }

    public static void setCamelContext(CamelContext camelContext) {
        MyXsltUriResolver.camelContext = camelContext;
        System.out.println("**********************setCamelcontext***********************");
    }

    public static CamelContext getCamelContext() {
        return camelContext;
    }

    private static CamelContext camelContext;

    public static String getBaseScheme() {
        return baseScheme;
    }

    public static void setBaseScheme(String baseScheme) {
        MyXsltUriResolver.baseScheme = baseScheme;
    }

    private static String baseScheme;

    public static Properties getContext() {
        return context;
    }

    public static void setContext(Properties context) {
        MyXsltUriResolver.context = context;
        System.out.println("**********************setcontext***********************");
    }

    private static Properties context;

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        if (ObjectHelper.isEmpty(href)) {
            throw new TransformerException("include href is empty");
        }

        String scheme = ResourceHelper.getScheme(href);

        if (ResourceHelper.hasScheme(href)) {
            baseScheme = scheme;
        }

        LOG.trace("Resolving URI with href: {} and base: {}", href, base);

        if (context != null) {
            if (context.get("XSLT_Source") instanceof Source && context.get("XSLT_Source") != null && context.get("XSLT_Source") != "") {
                return (Source) (context.get("XSLT_Source"));
            }
        }

        if (scheme != null && camelContext != null) {
            // need to compact paths for file/classpath as it can be relative paths using .. to go backwards
            if ("file:".equals(scheme)) {
                // compact path use file OS separator
                href = FileUtil.compactPath(href);
            } else if ("classpath:".equals(scheme)) {
                // for classpath always use /
                href = FileUtil.compactPath(href, '/');
            }
            LOG.debug("Resolving URI from {}: {}", scheme, href);
            InputStream is;
            try {
                is = ResourceHelper.resolveMandatoryResourceAsInputStream(camelContext.getClassResolver(), href);
            } catch (IOException e) {
                throw new TransformerException(e);
            }
            context.put("XSLT_Source", new StreamSource(is));
            return (Source) (context.get("XSLT_Source"));
        }
        if (camelContext == null)
            return null;
        String path = baseScheme + href;
        return resolve(path, base);
    }
}

