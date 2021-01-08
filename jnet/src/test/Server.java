package test;


import com.jnet.Control;
import com.jnet.ProtocolMaster;
import com.jnet.ServerProtocol;

@ServerProtocol(objQuery = true, name = "Test", port = 50000, pool = true, ssl = true, ip = "192.168.1.56")
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
