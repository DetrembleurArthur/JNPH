package com.jnet;

import java.time.LocalDateTime;
import java.util.Date;

public final class Log
{
	public static void out(String who, String msg)
	{
		System.out.println("[" + who + "] " + LocalDateTime.now() + " => " + msg);
	}
	
	public static void err(String who, String msg)
	{
		System.err.println("[" + who + "] " + LocalDateTime.now() + " => " + msg);
	}
}
