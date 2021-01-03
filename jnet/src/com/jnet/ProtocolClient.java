package com.jnet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;

public class ProtocolClient implements Runnable, ProtocolEntity
{
    private Socket socket;
    private Class<?> protocol;
    private ProtocolHandler protocolHandler;

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

    @Override
    public void run()
    {
        try
        {
            ClientProtocol ann = protocol.getAnnotation(ClientProtocol.class);
            Socket socket = new Socket(ann.ip(), ann.port());
            ProtocolHandler protocolHandler = ProtocolHandler.create(protocol.getConstructor().newInstance(), null);
            protocolHandler.initNet(socket);
            protocolHandler.start();
            this.protocolHandler = protocolHandler;
            this.socket = socket;
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
}
