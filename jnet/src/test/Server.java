package test;


import com.jnet.Control;
import com.jnet.ProtocolMaster;
import com.jnet.ServerProtocol;

@ServerProtocol(objQuery = false, name = "Test", port = 6000, pool = true, ssl = true)
public class Server
{
    public static void main(String[] args)
    {
        ProtocolMaster.launch(Server.class);
    }

    @Control
    public String ping()
    {
        return "pong";
    }
}
