package com.jnet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ProtocolHandler extends Thread implements ProtocolEntity
{
    protected Tunnel tunnel;
    protected final Object protocol;
    protected ArrayList<Method> controls;
    protected AtomicBoolean running = new AtomicBoolean(false);
    protected final Options options;
    
    public static ProtocolHandler create(Object protocol, ProtocolServer server)
    {
    	if(server == null)
    		return ProtocolHandlerClient.create(protocol);
    	return ProtocolHandlerServer.create(protocol, server);
    }

    public ProtocolHandler(Object protocol)
    {
        this.protocol = protocol;
        options = ProtocolEntity.getOptions(protocol.getClass());
        initControls();
    }
    
    public String getProtocolName()
    {
    	return options.getProperty("name");
    }

    public boolean isInObjectQueryMode()
    {
        return (boolean) options.get("objQuery");
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
    }
    
    private void injections()
    {
    	for(Field field : protocol.getClass().getDeclaredFields())
    	{
    		if(field.isAnnotationPresent(Com.class))
    		{
    			try
    			{
    				if(field.getType().equals(Tunnel.class))
    					field.set(protocol, tunnel);
    				else if(field.getType().equals(ProtocolHandler.class))
    					field.set(protocol, this);
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
        injections();
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
        return running.get();
    }

    public Query recv()
    {
        Query query;
        if(isInObjectQueryMode())
            query = tunnel.recvobj();
        else
            query = tunnel.recvbuff();
        Log.out(this, "recieve [" + query + "]");
        return query;
    }

    public synchronized void send(Query query)
    {
        if(isInObjectQueryMode())
            tunnel.sendobj(query);
        else
            tunnel.sendbuff(query);
        Log.out(this, "send [" + query + "]");
    }
    
    protected abstract void handshake();

    @Override
    public void run()
    {
		handshake();
		while(running.get())
        {
        	Log.out(this, "is waiting query");
        	Query query = recv();
            if(query == null) break;
            for(Method control : controls)
            {
                if(manageControl(query, control))
                    break;
            }
            manageQueryModes(query);
        }
    	Log.out(this, "is down");
    }

    protected abstract void manageQueryModes(Query query);

    protected boolean manageControl(Query query, Method control)
    {
        Control ann = control.getAnnotation(Control.class);
        String type = ann.type();
        if(type.equals("/"))
            type = control.getName();
        if(query.is(type))
        {
            try
            {
                Object result = invokeControl(control, query);
                Log.out(this, "control result: " + result);
                if(result != null)
                    manageControlResult(type, result);
                return true;
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
        return false;
    }

	protected Object invokeControl(Method control, Query query) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		switch(control.getParameterCount())
        {
            case 0:
            	return control.invoke(protocol);
        	case 1:
        	    if(control.getParameterTypes()[0].equals(Args.class))
                {
        	    	return control.invoke(protocol, query.getArgs());
                }
            default:
            	Object[] args = isInObjectQueryMode() ? query.getArgs().toArray() : query.getArgs().castToPrimitive(control.getParameterTypes()).toArray();
                if(args.length != control.getParameterCount())
                {
                	args = Arrays.copyOf(args, control.getParameterCount());
                }
                return control.invoke(protocol, args);
        }
	}
	
	protected void manageControlResult(String type, Object result)
	{
		if(result != null)
		{
			if(result instanceof Query)
			{
				send((Query) result);
			}
			else if(result instanceof String)
			{
				send(Query.normal((String) result));
			}
			else
			{
				if(result == Query.SUCCESS)
					send(Query.success(type));
				else if(result == Query.FAILED)
					send(Query.failed(type));
				else
				    send(Query.normal(type).pack(result));
			}
		}
	}
	
	public ProtocolHandler waitStarting()
	{
		while(!running.get());
		return this;
	}

    @Override
    public Options getOptions()
    {
        return options;
    }
}
