package com.jnet;

import java.util.ArrayList;

public class Args extends ArrayList<Object>
{
	private int it = 0;

	public <T> T getc(int i)
	{
		return (T) get(i);
	}
	
	public <T> T get()
	{
		if(it < size())
			return getc(it++);
		return null;
	}

	public void reset()
	{
		setIt(0);
	}
	
	public int getIt()
	{
		return it;
	}

	public void setIt(int it)
	{
		this.it = it;
	}
}
