package com.jnet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Log
{
	public static void out(String who, String msg)
	{
		System.out.println("[" + who + "] " + LocalDateTime.now().toString().replaceFirst("T", " at ") + " => " + msg);
	}
	
	public static void err(String who, String msg)
	{
		System.err.println("[" + who + "] " + LocalDateTime.now().toString().replaceFirst("T", " at ") + " => " + msg);
	}

	public static void out(ProtocolEntity who, String msg)
	{
		Log.out(who.getEntityId(), msg);
	}

	public static void err(ProtocolEntity who, String msg)
	{
		Log.err(who.getEntityId(), msg);
	}
}
