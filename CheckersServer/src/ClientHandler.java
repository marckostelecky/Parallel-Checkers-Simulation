import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

	private Socket client;
	String players[] = new String[10];

	// Constructor class
	// takes socket as input
	public ClientHandler(Socket client) {
		this.client = client;
	}

	public void run() {
		try {
			System.out.println("Thread started with name:"
					+ Thread.currentThread().getName());
			readResponse();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void returnMessage(String[] player) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				client.getOutputStream());
		out.writeObject(player);
		out.flush();
		out.close();
	}

	private void readResponse() throws IOException, InterruptedException,
			ClassNotFoundException {
		ObjectInputStream stdIn = new ObjectInputStream(client.getInputStream());
		String[] clientInput;

		clientInput = (String[]) stdIn.readObject();
		if (clientInput[0].equals("CONNECT")) {
			System.out.println("Connecting to server");
			String player[] = ServerStart.appendPlayer(client.getInetAddress()
					.toString());
			returnMessage(player);
		} else if (clientInput[0].equals("PLAYERS")){
			String player[] = ServerStart.getPlayers();
			returnMessage(player);
		} else if (clientInput[0].equals("NEW")){
			
		}
	}
}
