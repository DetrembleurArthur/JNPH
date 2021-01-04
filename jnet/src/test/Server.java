package test;

import com.jnet.*;

@ServerProtocol(name = "Test", objQuery = true)
public class Server {

	public static void main(String[] args)
	{
		ProtocolMaster.launch(Server.class);
	}

	@Com
	public ProtocolHandler ph;

	@Control
	public Query number(Double num)
	{
		return Query.normal("number").pack(Math.sqrt(num));
	}
}
