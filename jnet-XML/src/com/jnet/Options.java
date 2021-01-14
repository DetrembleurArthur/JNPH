package com.jnet;

import parsers.ObjectParser;
import parsers.SaxExtractor;
import parsers.Serveur;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
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
        put("ssl", ann.ssl());
    }

    public Options(ClientProtocol ann)
    {
        put("name", ann.name());
        put("ip", ann.ip());
        put("port", ann.port());
        put("objQuery", ann.objQuery());
        put("ssl", ann.ssl());
    }

    public Options(String name, String xml)
    {
        ArrayList<ObjectParser> objectParsers = SaxExtractor.extract(xml);
        assert objectParsers != null;
        Serveur serveur = SaxExtractor.extractServer(objectParsers, name);
        if(serveur != null)
        {
            System.out.println(">  " + serveur);
            put("name", name);
            put("ip", serveur.getIp());
            put("port", serveur.getPort());
            put("pool", serveur.isPool());
            put("objQuery", serveur.isObjQuery());
            put("maxClients", serveur.getSize());
            put("ssl", serveur.isSsl());
        }
    }
}
