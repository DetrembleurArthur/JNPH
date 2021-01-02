package com.jnet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;

public class OnDemandProtocolHandler extends ProtocolHandler
{
	private boolean pooling = false;
	
    public OnDemandProtocolHandler(Object protocol, ProtocolServer sup)
    {
		super(protocol, sup);
	}

    @Override
    public void run()
    {
		try
		{
    		super.run();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        getProtocolServer().getProtocolHandlers().remove(this);
        Log.out(getProtocolName(), "is down");
    }
}
