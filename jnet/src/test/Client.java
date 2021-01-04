package test;

import com.jnet.*;

import javax.swing.*;


@ClientProtocol(objQuery = true, name = "Taaaaest")
public class Client
{
	public static void main(String[] args)
	{
		ProtocolMaster master = ProtocolMaster.launch(Client.class);
	}

	@Control
	public void alert(String message)
	{
		JOptionPane.showMessageDialog(null, "Alert: " + message);
	}
}
