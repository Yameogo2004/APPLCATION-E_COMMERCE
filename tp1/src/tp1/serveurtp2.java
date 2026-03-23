package tp1;

import java.net.*;

public class serveurtp2 {
    public static void main(String[] args) throws Exception {

        DatagramSocket serverSocket = new DatagramSocket(9778);

        byte[] donneeRecue = new byte[1024];
        byte[] donneeEnvoyee;

        System.out.println("Serveur UDP démarré sur le port 9778...");

        while (true) {

            // 1. Attendre un premier message du client
            DatagramPacket paquetRecu = new DatagramPacket(donneeRecue, donneeRecue.length);
            serverSocket.receive(paquetRecu);

            String messageClient = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
            InetAddress adresseIP = paquetRecu.getAddress();
            int port = paquetRecu.getPort();

            System.out.println("Client connecté : " + messageClient);

            // 2. Envoyer le menu
            String menu = "Choisissez une opération : +  -  *  /";
            donneeEnvoyee = menu.getBytes();
            DatagramPacket paquetMenu = new DatagramPacket(donneeEnvoyee, donneeEnvoyee.length, adresseIP, port);
            serverSocket.send(paquetMenu);

            // 3. Recevoir le choix du client
            paquetRecu = new DatagramPacket(new byte[1024], 1024);
            serverSocket.receive(paquetRecu);
            String operation = new String(paquetRecu.getData(), 0, paquetRecu.getLength()).trim();

            System.out.println("Opération choisie : " + operation);

            // 4. Demander les deux nombres
            String demandeNombres = "Envoyez deux nombres séparés par un espace";
            donneeEnvoyee = demandeNombres.getBytes();
            DatagramPacket paquetDemande = new DatagramPacket(donneeEnvoyee, donneeEnvoyee.length, adresseIP, port);
            serverSocket.send(paquetDemande);

            // 5. Recevoir les deux nombres
            paquetRecu = new DatagramPacket(new byte[1024], 1024);
            serverSocket.receive(paquetRecu);
            String nombresRecus = new String(paquetRecu.getData(), 0, paquetRecu.getLength()).trim();

            System.out.println("Nombres reçus : " + nombresRecus);

            String resultat;

            try {
                String[] parties = nombresRecus.split(" ");
                double a = Double.parseDouble(parties[0]);
                double b = Double.parseDouble(parties[1]);
                double res = 0;

                switch (operation) {
                    case "+":
                        res = a + b;
                        resultat = "Résultat : " + res;
                        break;
                    case "-":
                        res = a - b;
                        resultat = "Résultat : " + res;
                        break;
                    case "*":
                        res = a * b;
                        resultat = "Résultat : " + res;
                        break;
                    case "/":
                        if (b == 0) {
                            resultat = "Erreur : division par zéro impossible";
                        } else {
                            res = a / b;
                            resultat = "Résultat : " + res;
                        }
                        break;
                    default:
                        resultat = "Erreur : opération invalide";
                }

            } catch (Exception e) {
                resultat = "Erreur : nombres invalides";
            }

            // 6. Envoyer le résultat au client
            donneeEnvoyee = resultat.getBytes();
            DatagramPacket paquetResultat = new DatagramPacket(donneeEnvoyee, donneeEnvoyee.length, adresseIP, port);
            serverSocket.send(paquetResultat);
        }
    }
}