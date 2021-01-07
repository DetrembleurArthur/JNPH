package com.jnet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class ProtocolClient implements Runnable, ProtocolEntity
{
    private Socket socket;
    private final Class<?> protocol;
    private ProtocolHandler protocolHandler;
    private final Options options;

    public ProtocolClient(Class<?> protocol)
    {
        if(!protocol.isAnnotationPresent(ClientProtocol.class))
        {
            throw new IllegalArgumentException(protocol.getCanonicalName() + " is not annotated as 'Protocol'");
        }
        else
        {
            this.protocol = protocol;
        }
        options = ProtocolEntity.getOptions(protocol);
    }


    public synchronized Socket getSocket()
    {
        return socket;
    }

    public synchronized Class<?> getProtocol()
    {
        return protocol;
    }

    public synchronized ProtocolHandler getProtocolHandler()
    {
        return protocolHandler;
    }
    
    public synchronized ProtocolHandler getSafeProtocolHandler()
    {
        return protocolHandler.waitStarting();
    }

    @Override
    public void run()
    {
        try
        {
            Socket socket;
            ClientProtocol ann = protocol.getAnnotation(ClientProtocol.class);
            if(protocol.getAnnotation(ClientProtocol.class).ssl())
            {
                socket = Utils.generateClientSSL(
                        "D:\\Users\\mb624\\Documents\\GitHub\\complement_reseau_ApplicBateau\\makecert\\client_keystore",
                        "client_keystore",
                        "client_keystore",
                        protocol.getAnnotation(ClientProtocol.class).port(), protocol.getAnnotation(ClientProtocol.class).ip());
                this.socket = socket;
                Log.out(this, "SSL activated");
            }
            else
            {
                socket = new Socket(ann.ip(), ann.port());
                this.socket = socket;
            }
            ProtocolHandler protocolHandler = ProtocolHandler.create(protocol.getConstructor().newInstance(), null);
            protocolHandler.initNet(socket);
            protocolHandler.start();
            this.protocolHandler = protocolHandler;

            Log.out(this, "client started on " + socket.getInetAddress().getHostAddress());
        } catch (IOException | NoSuchMethodException e)
        {
            e.printStackTrace();
           Log.err(this, protocol.getCanonicalName() + " client handler can not be connected to the server");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e)
        {
            e.printStackTrace();
            Log.err(this, protocol.getCanonicalName() + " client handler can not be instantiate");
        }
    }

    @Override
    public String getEntityId()
    {
        return "PC:" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    @Override
    public Options getOptions()
    {
        return options;
    }
}
