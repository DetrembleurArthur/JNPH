package com.jnet;

import java.lang.annotation.Annotation;

public interface ProtocolEntity
{
    String getEntityId();
    default Options getOptions() {return null;}

    static Options getOptions(Class<?> pclass)
    {
        if(pclass.isAnnotationPresent(ServerProtocol.class))
            return new Options(pclass.getAnnotation(ServerProtocol.class));
        if(pclass.isAnnotationPresent(ClientProtocol.class))
            return new Options(pclass.getAnnotation(ClientProtocol.class));
        return null;
    }
}
