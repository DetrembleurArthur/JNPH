package com.jnet;

import java.lang.annotation.Annotation;

public interface ProtocolEntity
{
    String getEntityId();
    default Options getOptions() {return null;}

    static Options getOptions(Annotation ann)
    {
        if(ann instanceof ServerProtocol)
            return new Options((ServerProtocol) ann);
        else if(ann instanceof ClientProtocol)
            return new Options((ClientProtocol) ann);
        return null;
    }
}
