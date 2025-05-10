import java.io.IOException;
import tage.networking.IGameConnection.ProtocolType;

public class NetworkingServer 
{
	private GameAIServerUDP UDPServer;
	private NPCcontroller npcCtrl;
	private GameServerUDP thisUDPServer;
	private GameServerTCP thisTCPServer;

	public NetworkingServer(int serverPort) 
	{	
		npcCtrl = new NPCcontroller();
		// start networking server
		
		try
		{ UDPServer = new GameAIServerUDP(serverPort, npcCtrl); }
		catch (IOException e)
		{ 	System.out.println("server didn't start"); e.printStackTrace(); }
		npcCtrl.start(UDPServer);
	}

	public static void main(String[] args) 
	{	if(args.length == 1)
		{	NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0])); //, args[1]
		}
	}

}
