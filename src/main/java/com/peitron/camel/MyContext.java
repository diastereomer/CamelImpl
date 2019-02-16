package com.peitron.camel;

//add the context variables the route need to the below class
public class MyContext extends java.util.Properties {
    protected void synchronizeContext() {
        if (ESB_WS_URL != null) {
            this.setProperty("ESB_WS_URL", ESB_WS_URL.toString());
        }
        if (WINS_WS_URL != null) {
            this.setProperty("WINS_WS_URL", WINS_WS_URL.toString());
        }

        if ( CONTEXT_SQL_Script!= null) {
            this.setProperty("CONTEXT_SQL_Script",CONTEXT_SQL_Script.toString());
        }

        this.setProperty("maxTry", String.valueOf(maxTry));

        if ( ActiveMQServer!= null) {
            this.setProperty("ActiveMQServer", ActiveMQServer.toString());
        }

        if ( CC_AUDIT_Queue!= null) {
            this.setProperty("CC_AUDIT_Queue", CC_AUDIT_Queue.toString());
        }
    }

    protected String ESB_WS_URL;

    protected String getESB_WS_URL() {
        return this.ESB_WS_URL;
    }

    protected String WINS_WS_URL;

    protected String getWINS_WS_URL() {
        return this.WINS_WS_URL;
    }

    protected String CONTEXT_SQL_Script;

    protected String getCONTEXT_SQL_Script() {
        return this.CONTEXT_SQL_Script;
    }

    protected int maxTry;

    protected int getMaxTry() {
        return this.maxTry;
    }

    protected String ActiveMQServer;

    protected String getActiveMQServer() {
        return this.ActiveMQServer;
    }

    protected String CC_AUDIT_Queue;

    protected String CC_AUDIT_Queue() {
        return this.CC_AUDIT_Queue;
    }

    protected void setContextFields () {
        this.ESB_WS_URL = (String) this.getProperty("ESB_WS_URL");
        this.WINS_WS_URL = (String) this.getProperty("WINS_WS_URL");
        this.CONTEXT_SQL_Script = (String) this
                .getProperty("CONTEXT_SQL_Script");
        try {
            this.maxTry =Integer.parseInt(this.getProperty("maxTry"));
        } catch (NumberFormatException e) {
            this.maxTry = 0;
        }
        this.ActiveMQServer = (String) this.getProperty("ActiveMQServer");
        this.CC_AUDIT_Queue = (String) this.getProperty("CC_AUDIT_Queue");
    }
}

