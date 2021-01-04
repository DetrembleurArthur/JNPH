package com.jnet;

import java.util.Properties;

public class Options extends Properties
{
    public Options(ServerProtocol ann)
    {
        put("name", ann.name());
        put("ip", ann.ip());
        put("port", ann.port());
        put("pool", ann.pool());
        put("objQuery", ann.objQuery());
        put("maxClients", ann.maxClients());
    }

    public Options(ClientProtocol ann)
    {
        put("name", ann.name());
        put("ip", ann.ip());
        put("port", ann.port());
        put("objQuery", ann.objQuery());
    }
}
