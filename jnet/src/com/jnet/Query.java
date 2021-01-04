package com.jnet;

import java.io.Serializable;
import java.util.ArrayList;

public class Query implements Serializable
{
    public enum Mode
    {
        NORMAL,
        BROADCAST
    }
    
    public static final Object SUCCESS = new Object();
    public static final Object FAILED = new Object();

    private Mode mode;
    private String type;
    private Args args;

    public static Query normal(String buffer)
    {
    	return new Query(buffer);
    }
    
    public static Query success(String buffer)
    {
    	return new Query(buffer).success();
    }
    
    public static Query failed(String buffer)
    {
    	return new Query(buffer).failed();
    }
    
    public Query()
    {
        this("undefined");
    }

    public Query(String buffer, boolean moded)
    {
        args = new Args();
        if(moded)
        {
            load(buffer);
        }
        else
        {
            load(buffer, Mode.NORMAL);
        }
    }

    public Query(String buffer)
    {
        this(buffer, Mode.NORMAL);
    }

    public Query(String buffer, Mode mode)
    {
        args = new Args();
        load(buffer, mode);
    }

    public Query pack(Object arg)
    {
        args.add(arg);
        return this;
    }

    public Query packall(ArrayList<?> objects)
    {
        args.addAll(objects);
        return this;
    }

    public boolean is(String type)
    {
        return getType().toLowerCase().equals(type.toLowerCase());
    }

    public void load(String buffer)
    {
        String[] tokens = buffer.split("€€€");
        if(tokens.length > 0)
        {
            type = tokens[0];
            if(tokens.length > 1)
            {
                mode = Mode.valueOf(tokens[1]);
                if(tokens.length > 2)
                {
                    for(int i = 2; i < tokens.length; i++)
                    {
                        args.add(tokens[i]);
                    }
                }
            }
        }
        System.err.println(mode);
    }

    public void load(String buffer, Mode mode)
    {
        String[] tokens = buffer.split("€€€");
        if(tokens.length > 0)
        {
            type = tokens[0];
            if(tokens.length > 1)
            {
                for(int i = 1; i < tokens.length; i++)
                {
                    args.add(tokens[i]);
                }
            }
        }
        this.mode = mode;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Args getArgs()
    {
        return args;
    }

    public void setArgs(Args args)
    {
        this.args = args;
    }
    
    public Query success()
    {
    	type += "_success";
    	return this;
    }
    
    public Query failed()
    {
    	type += "_failed";
    	return this;
    }

    public Query mode(Mode mode)
    {
        this.mode = mode;
        return this;
    }

    public Mode getMode()
    {
        return mode;
    }

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder(type);
        buffer.append("€€€").append(mode);
        for(Object obj : args)
            buffer.append("€€€").append(obj.toString().replace("€€€", "€"));
        return buffer.toString();
    }
}
