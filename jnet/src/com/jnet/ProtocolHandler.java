package com.jnet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;

public class ProtocolHandler extends Thread implements ProtocolEntity
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
    		return protocol.getClass().getAnnotation(ServerProtocol.class).name();
    	return protocol.getClass().getAnnotation(ClientProtocol.class).name();
    }
    
    private void initControls()
    {
    	controls = new ArrayList<>();
        for(Method method : protocol.getClass().getDeclaredMethods())
        {
            if(method.isAnnotationPresent(Control.class))
            {
                controls.add(method);
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
					Log.err(this, "tunnel injection failed");
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

        	Log.out(this, "is waiting query");
            Query query = tunnel.recvbuff();
            Log.out(this, "recieve [" + query + "]");
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
                            case 0:control.invoke(protocol);break;
                        	case 1:
                        	    if(control.getParameterTypes()[0].equals(Args.class))
                                {
                                    control.invoke(protocol, query.getArgs());
                                    break;
                                }
                        	    else if(control.getParameterTypes()[0].equals(Tunnel.class))
                                {
                                    control.invoke(protocol, tunnel);
                                    break;
                                }
                            default:
                                control.invoke(protocol, query.getArgs().castToPrimitive(control.getParameterTypes()).toArray());
                        }
                        break;
                    } catch (IllegalAccessException | InvocationTargetException e)
                    {
                        e.printStackTrace();
                        Log.err(this, "bad control invocation");
                    }
                    catch (Exception e)
                    {
                    	Log.err(this, "control exception");
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.out(this, "is down");
    }

	public ProtocolServer getProtocolServer() {
		return protocolServer;
	}

    @Override
    public String getEntityId()
    {
        return "PH(" + (protocol.getClass().isAnnotationPresent(ServerProtocol.class) ? "S" : "C") + "):" + tunnel.getSocket().getInetAddress().getHostAddress() + ":" + tunnel.getSocket().getPort() + ":" + getProtocolName();
    }
}
