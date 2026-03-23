package tp1;
import java.io.*;
import java.net.*;
public class client {
	public static void main (String arvg[]) throws Exception
	{
	String phrase = "école nationale des sciences appliquées de tétouan"; 
	String phraseModifiee; 
	while (true) {
	BufferedReader entreeDepuisUtilisateur = 
	new BufferedReader (new InputStreamReader (System.in) );
	
	Socket SocketClient = new Socket ("localhost", 7016); 
	DataOutputStream sortieversServeur = 
	    	new DataOutputStream	(SocketClient.getOutputStream());
	
	BufferedReader entreeDepuisServeur =
		new BufferedReader (new InputStreamReader (SocketClient.getInputStream()));
	phrase = entreeDepuisUtilisateur.readLine();
    sortieversServeur.writeBytes(phrase + '\n');   
    phraseModifiee = entreeDepuisServeur.readLine();
    System.out.println ("RECU DU SERVEUR: " + phraseModifiee);
    SocketClient.close();
    }
	}
}