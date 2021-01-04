package test;

import com.jnet.*;

import javax.swing.*;


@ClientProtocol(objQuery = true, name = "Test")
public class Client
{
	public static void main(String[] args)
	{
		ProtocolMaster master = ProtocolMaster.launch(Client.class);
		ProtocolClient cli = master.getProtocolClient(Client.class);
		ProtocolHandler handler = cli.getProtocolHandler();

		handler.send(Query.normal("number").pack(Double.parseDouble(JOptionPane.showInputDialog("Enter a number"))));
	}

	@Control
	public void number(Double num)
	{
		JOptionPane.showMessageDialog(null, String.valueOf(num));
	}
}
