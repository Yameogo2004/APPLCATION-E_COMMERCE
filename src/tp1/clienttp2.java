package tp1;

import java.io.*;
import java.net.*;

public class clienttp2 {
    public static void main(String[] args) throws Exception {

        BufferedReader entreeUtilisateur = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress adresseIP = InetAddress.getByName("localhost");

        byte[] donneeEnvoyee;
        byte[] donneeRecue = new byte[1024];

        // 1. Envoyer un message initial au serveur
        String messageInitial = "Bonjour serveur";
        donneeEnvoyee = messageInitial.getBytes();

        DatagramPacket paquetEnvoye = new DatagramPacket(
                donneeEnvoyee,
                donneeEnvoyee.length,
                adresseIP,
                9886
        );
        clientSocket.send(paquetEnvoye);

        // 2. Recevoir le menu
        DatagramPacket paquetRecu = new DatagramPacket(donneeRecue, donneeRecue.length);
        clientSocket.receive(paquetRecu);
        String menu = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
        System.out.println("SERVEUR : " + menu);

        // 3. Saisir l’opération
        System.out.print("Votre choix : ");
        String operation = entreeUtilisateur.readLine();
        donneeEnvoyee = operation.getBytes();

        paquetEnvoye = new DatagramPacket(
                donneeEnvoyee,
                donneeEnvoyee.length,
                adresseIP,
                9886
        );
        clientSocket.send(paquetEnvoye);

        // 4. Recevoir la demande des nombres
        paquetRecu = new DatagramPacket(new byte[1024], 1024);
        clientSocket.receive(paquetRecu);
        String demande = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
        System.out.println("SERVEUR : " + demande);

        // 5. Saisir les deux nombres
        System.out.print("Entrez le premier nombre : ");
        String n1 = entreeUtilisateur.readLine();

        System.out.print("Entrez le deuxième nombre : ");
        String n2 = entreeUtilisateur.readLine();

        String nombres = n1 + " " + n2;
        donneeEnvoyee = nombres.getBytes();

        paquetEnvoye = new DatagramPacket(
                donneeEnvoyee,
                donneeEnvoyee.length,
                adresseIP,
                9886
        );
        clientSocket.send(paquetEnvoye);

        // 6. Recevoir le résultat
        paquetRecu = new DatagramPacket(new byte[1024], 1024);
        clientSocket.receive(paquetRecu);
        String resultat = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
        System.out.println("SERVEUR : " + resultat);

        clientSocket.close();
    }
}