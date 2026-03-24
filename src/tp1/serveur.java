package tp1;

import java.io.*;
import java.net.*;

public class serveur {

	public static void main(String arvg[]) throws Exception
	{

		String phraseClient;
		String phraseMajuscule;
		ServerSocket conn = new ServerSocket(7016);
		while(true) {
			Socket comm = conn.accept();
			BufferedReader entreeDepuisClient = 
					new BufferedReader(new InputStreamReader (comm.getInputStream()));
			DataOutputStream sortieVersClient =
					new DataOutputStream (comm.getOutputStream());
			phraseClient = entreeDepuisClient.readLine();
			phraseMajuscule = phraseClient.toUpperCase() + '\n';
			sortieVersClient.writeBytes(phraseMajuscule);
		}
	}

}


