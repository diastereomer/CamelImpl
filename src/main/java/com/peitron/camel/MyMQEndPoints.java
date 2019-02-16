package com.peitron.camel;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.Endpoint;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.mqtt.MQTTComponent;
import org.apache.camel.component.mqtt.MQTTConfiguration;
import org.apache.camel.component.mqtt.MQTTEndpoint;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;

import javax.jms.ConnectionFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;

public class MyMQEndPoints {


    /*for pooled connection, token[0] is setExpiryTimeout, for instance 60000; token[1] is setIdleTimeout(, for instance 30000;
    token[2] is setMaxConnections, for instance 10; token[3] is setMaximumActiveSessionPerConnection, for instance 500
    */
    // get a ssl activemq
    protected static ConnectionFactory getSSLActiveMQ(String url, String username, String password, String keyStore,
                                                      String keyStorePassword, String trustStore, String trustStorePassword,
                                                      boolean isPooledConnection, int... token) throws Exception {
        ActiveMQSslConnectionFactory sslFactory = new ActiveMQSslConnectionFactory(url);
        sslFactory.setUserName(username);
        sslFactory.setPassword(password);
        sslFactory.setKeyStore(keyStore);
        sslFactory.setKeyStorePassword(keyStorePassword);
        sslFactory.setTrustStore(trustStore);
        sslFactory.setTrustStorePassword(trustStorePassword);
        if (isPooledConnection) {
            return getPooledConnection(sslFactory, token);
        } else return sslFactory;
    }

    //get an activemq
    protected static ConnectionFactory getActiveMQ(String url, boolean isAuthentication, String username,
                                                   String encryptedPassword, boolean isPooledConnection, int... token)
            throws Exception {

        if (isAuthentication && (username == null || username.trim().equals("")))
            throw new IllegalArgumentException("need username and password for authentication");

        ConnectionFactory jmsConnectionFactory;
        if (isAuthentication) {
            String decryptedPassword = EncryptUtil.decrypt(encryptedPassword);
            jmsConnectionFactory = new ActiveMQConnectionFactory(username, decryptedPassword, url);
        } else
            jmsConnectionFactory = new ActiveMQConnectionFactory(url);
        if (isPooledConnection) {
            return getPooledConnection(jmsConnectionFactory, token);
        }
        return jmsConnectionFactory;
    }

    //get a pooled connection
    protected static ConnectionFactory getPooledConnection(ConnectionFactory connectionFactory, int... token) {
        PooledConnectionFactory pooledConnectionFactory = new org.apache.activemq.pool.PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(connectionFactory);
        pooledConnectionFactory.setExpiryTimeout(Long.valueOf(token[0]));
        pooledConnectionFactory.setIdleTimeout(token[1]);
        pooledConnectionFactory.setMaxConnections(token[2]);
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(token[3]);
        return pooledConnectionFactory;
    }

    protected static JmsComponent getTransactedMQComponent(ConnectionFactory connectionFactory) {
        return JmsComponent.jmsComponentTransacted(connectionFactory);
    }

    protected static JmsComponent getActiveMQComponent(ConnectionFactory connectionFactory) {
        return JmsComponent.jmsComponent(connectionFactory);
    }

    //get a IBM Websphere MQ
    /* get the value from the interface com.ibm.msg.client.wmq.WMQConstants
    * WMQConstants.WMQ_CM_BINDINGS
    * WMQConstants.WMQ_CM_CLIENT
    * WMQConstants.WMQ_CM_DIRECT_TCPIP
    * WMQConstants.WMQ_CM_DIRECT_HTTP */
    protected static ConnectionFactory getIBMMQ(String host, int port, int WMQConstants_TransportType,
                                                String queueManager, boolean isAuthentication, String username,
                                                String encryptedPassword, String clientChannel, boolean isPooledConnection, boolean isSSL) throws Exception {
        if (isAuthentication && isSSL)
            throw new IllegalArgumentException("can not have authentication and SSL together");
        if (isAuthentication && (username == null || username.trim().equals("")))
            throw new IllegalArgumentException("need username and password for authentication");
        ConnectionFactory connectionFactory = new MQQueueConnectionFactory();
        ((MQConnectionFactory) connectionFactory).setHostName(host);
        try {
            ((MQConnectionFactory) connectionFactory).setPort(port);
            // ((MQConnectionFactory)jmsConnectionFactory).setCCSID(866);
            ((MQConnectionFactory) connectionFactory).setTransportType(WMQConstants_TransportType);
            ((MQConnectionFactory) connectionFactory).setQueueManager(queueManager);
            if (WMQConstants_TransportType == WMQConstants.WMQ_CM_CLIENT) {
                if (clientChannel == null || clientChannel.trim().equals(""))
                    throw new IllegalArgumentException("need clientChannel to initiate");
                ((MQConnectionFactory) connectionFactory).setChannel(clientChannel);
            }
        } catch (javax.jms.JMSException e) {
            e.printStackTrace();
        }

        if (isAuthentication) {
            UserCredentialsConnectionFactoryAdapter connectionFactoryAdapter = new UserCredentialsConnectionFactoryAdapter();
            connectionFactoryAdapter.setUsername(username);
            connectionFactoryAdapter.setPassword(EncryptUtil.decrypt(encryptedPassword));
            connectionFactoryAdapter.setTargetConnectionFactory(connectionFactory);
            return connectionFactory;
        }
        else if(isSSL) {
            //set system value
            System.setProperty("javax.net.ssl.trustStore", "<location of trustStore>");
            System.setProperty("javax.net.ssl.keyStore", "<location of keyStore>");
            System.setProperty("javax.net.ssl.keyStorePassword", "<password>");
            //get the value
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            String keyStore = System.getProperty("javax.net.ssl.keyStore");
            String keyStoreType = System.getProperty("javax.net.ssl.keyStoreType", KeyStore.getDefaultType());
            String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword", "");
            KeyManager[] kms = null;
            if (keyStore != null) {
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                KeyStore ks = KeyStore.getInstance(keyStoreType);
                if (keyStore != null && !keyStore.equals("NONE")) {
                    FileInputStream fs = new FileInputStream(keyStore);
                    ks.load(fs, keyStorePassword.toCharArray());
                    if (fs != null)
                        fs.close();
                    char[] password = null;
                    if (keyStorePassword.length() > 0)
                        password = keyStorePassword.toCharArray();
                    kmf.init(ks, password);
                    kms = kmf.getKeyManagers();
                }
                sslcontext.init(kms, null, null);
                ((MQConnectionFactory) connectionFactory).setSSLSocketFactory(sslcontext.getSocketFactory());
            }
        }
        return connectionFactory;
    }


    //get a MQTT
    /*for MQTT Token[0] is for ConnectAttemptsMax for instance -1; Token[1] is for ReconnectAttemptsMax for instance -1;
    Token[2] is for ReconnectDelay for instance 10; Token[3] is for ConnectWaitInSeconds for instance 10;
    Token[4] is for DisconnectWaitInSeconds for instance 5;Token[5] is for SendWaitInSeconds for instance 5;*/
    protected static MQTTComponent getMQTTComponent(String url, String port, String qualityOfService, boolean isAuthentication,
                                                    String username, String encryptedPassword, boolean isSSL,
                                                    String encryptedSSLTSPassword, String trustStoreLocation,
                                                    String encryptedSSLKSPassword, String keyStoreLocation, int... token) throws Exception {

        if (isAuthentication && isSSL)
            throw new IllegalArgumentException("can not have authentication and SSL together");
        if (isAuthentication && (username == null || username.trim().equals("")))
            throw new IllegalArgumentException("need username and password for authentication");
        if (isSSL && (trustStoreLocation == null || trustStoreLocation.trim().equals("") || keyStoreLocation == null || keyStoreLocation.trim().equals("")))
            throw new IllegalArgumentException("need TrustStore and KeyStore for SSL");

        return new MQTTComponent() {

            private boolean isAuthentication;
            private String username;
            private String encryptedPassword;
            private boolean isSSL;
            private String url;
            private String port;
            private String qualityOfService;
            private int[] settings;
            private String encryptedSSLTSPassword;
            private String trustStoreLocation;
            private String encryptedSSLKSPassword;
            private String keyStoreLocation;


            @Override
            protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters)
                    throws Exception {
                MQTTEndpoint endpoint = (MQTTEndpoint) super.createEndpoint(uri, remaining, parameters);
                MQTTConfiguration config = endpoint.getConfiguration();

                config.setConnectAttemptsMax(settings[0]);
                config.setReconnectAttemptsMax(settings[1]);
                config.setReconnectDelay(settings[2]);
                config.setQualityOfService(qualityOfService);
                config.setConnectWaitInSeconds(settings[3]);
                config.setDisconnectWaitInSeconds(settings[4]);
                config.setSendWaitInSeconds(settings[5]);
                if (isAuthentication) {
                    config.setUserName(username);
                    config.setPassword(EncryptUtil.decrypt(encryptedPassword));
                    config.setHost("tcp://" + url + ":" + port);
                    return endpoint;
                } else if (isSSL) {
                    // set trust store
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    FileInputStream fis = new FileInputStream(trustStoreLocation);
                    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                    final String decryptedSSLTSPassword = EncryptUtil.decrypt(encryptedSSLTSPassword);
                    ks.load(fis, decryptedSSLTSPassword.toCharArray());
                    fis.close();
                    tmf.init(ks);
                    //set key store
                    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                    FileInputStream kfis = new FileInputStream(keyStoreLocation);
                    KeyStore kks = KeyStore.getInstance(KeyStore.getDefaultType());
                    String decryptedSSLKSPassword = EncryptUtil.decrypt(encryptedSSLKSPassword);
                    kks.load(kfis, decryptedSSLKSPassword.toCharArray());
                    kfis.close();
                    kmf.init(kks, decryptedSSLKSPassword.toCharArray());

                    SSLContext sslContext = SSLContext.getInstance("SSL");
                    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
                    config.setSslContext(sslContext);
                    config.setHost("ssl://" + url + ":" + port);
                    return endpoint;
                }
                config.setHost("tcp://" + url + ":" + port);
                return endpoint;
            }

            protected MQTTComponent init(String url, String port, String qualityOfService, boolean isAuthentication,
                                         String username, String encryptedPassword, boolean isSSL,
                                         String encryptedSSLTSPassword, String trustStoreLocation,
                                         String encryptedSSLKSPassword, String keyStoreLocation, int... token) {

                this.isSSL = isSSL;
                this.url = url;
                this.port = port;
                this.isAuthentication = isAuthentication;
                this.username = username;
                this.encryptedPassword = encryptedPassword;
                this.qualityOfService = qualityOfService;
                this.settings = token;
                this.trustStoreLocation = trustStoreLocation;
                this.keyStoreLocation = keyStoreLocation;
                this.encryptedSSLTSPassword = encryptedSSLTSPassword;
                this.encryptedSSLKSPassword = encryptedSSLKSPassword;
                return this;
            }
        }.init(url, port, qualityOfService, isAuthentication, username, encryptedPassword, isSSL,
                encryptedSSLTSPassword, trustStoreLocation, encryptedSSLKSPassword, keyStoreLocation, token);
    }

    //get AMQP
    protected AMQPComponent getAMQPComponent(String host, String port, boolean isAuthentication, String username,
                                             String encryptedPassword, boolean isSSL) throws Exception {
        String url;
        if (isAuthentication && isSSL)
            throw new IllegalArgumentException("can not have authentication and SSL together");
        if (isAuthentication && (username == null || username.trim().equals("")))
            throw new IllegalArgumentException("need username and password for authentication");
        if (isAuthentication)
            url = "amqp://" + username + ":" + EncryptUtil.decrypt(encryptedPassword) + "@" + host + ":" + port;
        else if (isSSL)
            url = "amqps://" + host + ":" + port;
        else
            url = "amqp://" + host + ":" + port;
        return new AMQPComponent(
                org.apache.qpid.amqp_1_0.jms.impl.ConnectionFactoryImpl
                        .createFromURL(url));
    }
}