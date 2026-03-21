package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientX {  // <- renommé pour ton rôle X

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner = new Scanner(System.in);

    public ClientX(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    // ── Menu principal ─────────────────────────
    public void menu() throws IOException {
        while (true) {
            System.out.println("\n===== MENU CLIENT (Rôle X) =====");
            System.out.println("1. Produits / Détail");
            System.out.println("2. Créer commande (CHECKOUT) [placeholder]");
            System.out.println("3. Payer commande (PAYMENT) [placeholder]");
            System.out.println("0. Quitter");
            System.out.print("Choix : ");
            String choix = scanner.nextLine();

            switch (choix) {
                case "1" -> menuProduits();
                case "2" -> System.out.println("Création de commande en attente du CartService...");
                case "3" -> System.out.println("Paiement en attente du PaymentService...");
                case "0" -> {
                    close();
                    return;
                }
                default -> System.out.println("Choix invalide !");
            }
        }
    }

    // ── Menu Produits / Détail ─────────────────
    private void menuProduits() throws IOException {
        out.println("GET_PRODUCTS");
        System.out.println("\nListe des produits :");

        String line;
        while (!(line = in.readLine()).equals("END_PRODUCTS")) {
            String[] parts = line.split(";");
            System.out.println("ID: " + parts[0] + " | Nom: " + parts[1] + " | Prix: " + parts[2]);
        }

        System.out.println("\nVoulez-vous voir le détail d'un produit ? (oui/non)");
        String reponse = scanner.nextLine();
        if (reponse.equalsIgnoreCase("oui")) {
            System.out.print("Entrez l'ID du produit : ");
            String productId = scanner.nextLine();
            out.println("GET_PRODUCT;" + productId);

            String detail = in.readLine();
            if (detail.startsWith("ERROR")) {
                System.out.println(detail);
            } else {
                String[] p = detail.split(";");
                System.out.println("\n===== Détail produit =====");
                System.out.println("Produit : " + p[1]);
                System.out.println("Prix : " + p[2]);
                System.out.println("Description : " + p[3]);
                System.out.println("Stock : " + p[4]);
            }
        }
    }

    public void close() throws IOException {
        System.out.println("Déconnexion...");
        socket.close();
    }

    // ── Main ─────────────────────────────
    public static void main(String[] args) {
        try {
            ClientX client = new ClientX("localhost", 8080);
            client.menu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
