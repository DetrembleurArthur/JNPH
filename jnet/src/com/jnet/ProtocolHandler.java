package com.jnet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;

public class ProtocolHandler extends Thread
{
    private Tunnel tunnel;
    private Object protocol;
    private ArrayList<Method> controls;
    private boolean running;
    private final ProtocolServer protocolServer;
    
    public static ProtocolHandler create(Object protocol, ProtocolServer server)
    {
    	if(server == null)
    		return new ProtocolHandler(protocol, server);
    	boolean pool = protocol.getClass().getAnnotation(ServerProtocol.class).pool();
    	if(pool)
    	{
    		return new PoolProtocolHandler(protocol, server);
    	}
    	else
    	{
    		return new OnDemandProtocolHandler(protocol, server);
    	}
    }

    public ProtocolHandler(Object protocol, ProtocolServer sup)
    {
        running = false;
        this.protocol = protocol;
        protocolServer = sup;
        initControls();
    }
    
    public String getProtocolName()
    {
    	if(protocol.getClass().isAnnotationPresent(ServerProtocol.class))
    		return "SER:"+protocol.getClass().getAnnotation(ServerProtocol.class).name() + ":"+hashCode();
    	return "CLI:" + protocol.getClass().getAnnotation(ClientProtocol.class).name() + ":"+hashCode();
    }
    
    private void initControls()
    {
    	controls = new ArrayList<>();
        for(Method method : protocol.getClass().getDeclaredMethods())
        {
            if(method.isAnnotationPresent(Control.class))
            {
                if((method.getParameterCount() == 2 && method.getParameterTypes()[0].equals(Tunnel.class) && method.getParameterTypes()[1].equals(Args.class))
            		|| (method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(Args.class))
    				|| (method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(Tunnel.class))
            		|| (method.getParameterCount() == 0))
                    controls.add(method);
                else throw new IllegalArgumentException(protocol.getClass().getAnnotation(ServerProtocol.class).name() + " : " + method.getName() + " has a bad prototype");
            }
        }
        if(controls.isEmpty())
            throw new IllegalArgumentException(protocol.getClass().getAnnotation(ServerProtocol.class).name() + " has no Controls");
    }
    
    private void connectTunnel()
    {
    	for(Field field : protocol.getClass().getDeclaredFields())
    	{
    		if(field.isAnnotationPresent(Com.class) && field.getType().equals(Tunnel.class))
    		{
    			try
    			{
					field.set(protocol, tunnel);
				} catch (IllegalArgumentException | IllegalAccessException e)
    			{
					Log.err(getProtocolName(), "tunnel injection failed");
					e.printStackTrace();
				}
    		}
    	}
    }

    public void initNet(Socket socket)
    {
        tunnel = new Tunnel(socket);
        connectTunnel();
    }

    public synchronized Tunnel getTunnel()
    {
        return tunnel;
    }

    public synchronized Object getProtocol()
    {
        return protocol;
    }

    public synchronized ArrayList<Method> getControls()
    {
        return controls;
    }

    public synchronized boolean isRunning()
    {
        return running;
    }

    @Override
    public void run()
    {
        running = true;
        while(running)
        {

        	Log.out(getProtocolName(), "is waiting query");
            Query query = tunnel.recvbuff();
            Log.out(getProtocolName(), "recieve [" + query + "]");
            if(query == null) break;
            for(Method control : controls)
            {
            	Control ann = control.getAnnotation(Control.class);
                String type = ann.type();
                if(type.equals("/"))
                    type = control.getName();
                if(query.is(type))
                {
                    try
                    {
                        switch(control.getParameterCount())
                        {
                        	case 2:control.invoke(protocol, tunnel, query.getArgs());break;
                        	case 1:
                        		control.invoke(protocol, 
                        		control.getParameterTypes()[0].equals(Tunnel.class)
                        		? tunnel : query.getArgs());
                        		break;
                        	case 0:control.invoke(protocol);break;
                        }	
                        break;
                    } catch (IllegalAccessException | InvocationTargetException e)
                    {
                        e.printStackTrace();
                        Log.err(getProtocolName(), "bad control invocation");
                    }
                    catch (Exception e)
                    {
                    	Log.err(getProtocolName(), "control exception");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

	public ProtocolServer getProtocolServer() {
		return protocolServer;
	}
}
