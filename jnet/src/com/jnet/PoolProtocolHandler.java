package com.jnet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;

public class PoolProtocolHandler extends ProtocolHandler
{
	private boolean pooling = false;
	
    public PoolProtocolHandler(Object protocol, ProtocolServer sup)
    {
		super(protocol, sup);
	}

	private void wakeup()
    {
    	try
        {
            synchronized (getProtocolServer().getPoolMonitor())
            {
                while (getProtocolServer().getSocket() == null)
                {
                	Log.out(this, " is waiting");
                    getProtocolServer().getPoolMonitor().wait();
                    Log.out(this, " is unlocked");
                }
                initNet(getProtocolServer().getSocket());
                getProtocolServer().setSocket(null);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
    	pooling = true;
    	while(pooling)
    	{
    		try
    		{
	    		wakeup();
	    		super.run();
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    }

    @Override
    public String getEntityId()
    {
        return "POOL:" + super.getEntityId();
    }
}
