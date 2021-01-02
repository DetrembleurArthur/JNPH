package com.jnet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;

public class ProtocolClient implements Runnable
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
            System.err.println("client connected");
            ProtocolHandler protocolHandler = ProtocolHandler.create(protocol.getConstructor().newInstance(), null);
            protocolHandler.initNet(socket);
            protocolHandler.start();
            this.protocolHandler = protocolHandler;
            System.out.println("client listen on " + socket.getInetAddress().getHostAddress());
        } catch (IOException | NoSuchMethodException e)
        {
            System.err.println(protocol.getCanonicalName() + " client handler can not be connected to the server");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e)
        {
            System.err.println(protocol.getCanonicalName() + " client handler can not be instantiate");
        }
    }
}
