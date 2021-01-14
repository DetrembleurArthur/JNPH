package com.jnet;

import java.time.LocalDateTime;

public final class Log
{
	public static void out(String who, String msg)
	{
		System.out.println("\033[96m[" + who + "]\033[0m \033[94m" + LocalDateTime.now().toString().replaceFirst("T", " at ") + "\033[0m => " + msg);
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
