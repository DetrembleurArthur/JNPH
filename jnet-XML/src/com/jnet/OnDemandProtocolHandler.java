package com.jnet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;

public class OnDemandProtocolHandler extends ProtocolHandlerServer
{
	private boolean pooling = false;
	
    public OnDemandProtocolHandler(Object protocol, ProtocolServer sup)
    {
		super(protocol, sup);
		options = sup.getOptions();
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
    }

	@Override
	public String getEntityId()
	{
		return "ONDE:" + super.getEntityId();
	}

	public boolean isPooling() {
		return pooling;
	}

	public void setPooling(boolean pooling) {
		this.pooling = pooling;
	}
}
