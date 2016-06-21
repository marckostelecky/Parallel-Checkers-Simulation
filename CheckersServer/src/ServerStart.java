import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;


public class ServerStart {
	private ServerSocket serverSocket;
	private int port;
	static String players[] = new String[10];
	JButton boardSquares[];
	String player1;
	String player2;
	
	
	public ServerStart(int port){
		this.port = port;
	}
	
	public void start() throws IOException {
		System.out.println("Starting the socket server at port:" + port);
		serverSocket = new ServerSocket(port);
		Socket client = null;
		while (true) {
			System.out.println("Waiting for clients...");
			client = serverSocket.accept();
			System.out.println("The following client has connected:" + client.getInetAddress().getCanonicalHostName());
			// A client has connected to this server. Send welcome message
			Thread thread = new Thread(new ClientHandler(client));
			thread.start();
		}
	}
	
	public  static String[] appendPlayer(String player){
		players[findIndex()] = player;
		return players;
	}
	
	public static String[] getPlayers(){
		return players;
	}
	
	public static int findIndex(){
		int result = -1;
		for (int i = 0; i < players.length; i++){
			if (players[i] == null){
				result = i;
				break;
			}
		}
		return result;
	}
    public static void main(String[] args) throws IOException {
		// Setting a default port number.
		int portNumber = 9991;

		try {
			// initializing the Socket Server
			ServerStart socketServer = new ServerStart(portNumber);
			socketServer.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
