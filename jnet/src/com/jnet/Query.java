package com.jnet;

import java.io.Serializable;
import java.util.ArrayList;

public class Query implements Serializable
{
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

    public Query(String buffer)
    {
        args = new Args();
        load(buffer);
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
        String[] tokens = buffer.split("#@#");
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

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder(type);
        for(Object obj : args)
            buffer.append("#@#").append(obj.toString());
        return buffer.toString();
    }
}
