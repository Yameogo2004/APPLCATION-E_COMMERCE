package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {

    private static final String SERVER_HOST = "localhost";
    private static final int    SERVER_PORT = 8080;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;

    // ── Connexion au serveur ───────────────────
    public boolean connectToServer() {
        try {
            socket  = new Socket(SERVER_HOST, SERVER_PORT);
            in      = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out     = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);

            // Lire le message de bienvenue du serveur
            String welcome = in.readLine();
            System.out.println(welcome);
            return true;

        } catch (Exception e) {
            System.out.println(" Impossible de se connecter au serveur : " + e.getMessage());
            return false;
        }
    }

    // ── Envoyer une requête et recevoir la réponse ──
    private String sendRequest(String request) {
        try {
            out.println(request);
            return in.readLine();
        } catch (Exception e) {
            System.out.println(" Erreur communication : " + e.getMessage());
            return null;
        }
    }

    // ── Menu principal ────────────────────────
    public void showMainMenu() {
        System.out.println("\n╔══════════════════════════╗");
        System.out.println("║      CHRIONLINE          ║");
        System.out.println("╠══════════════════════════╣");
        System.out.println("║  [1] Se connecter        ║");
        System.out.println("║  [2] S'inscrire          ║");
        System.out.println("║  [3] Quitter             ║");
        System.out.println("╚══════════════════════════╝");
        System.out.print("Votre choix : ");

        String choix = scanner.nextLine();

        switch (choix) {
            case "1" -> handleLogin();
            case "2" -> handleRegister();
            case "3" -> {
                System.out.println("Au revoir !");
                closeConnection();
            }
            default  -> {
                System.out.println("Choix invalide !");
                showMainMenu();
            }
        }
    }

    // ── Login ─────────────────────────────────
    private void handleLogin() {
        System.out.println("\n=== CONNEXION ===");
        System.out.print("Email : ");
        String email = scanner.nextLine();

        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        String response = sendRequest("LOGIN:" + email + ":" + password);

        if (response != null && response.startsWith("LOGIN_SUCCESS")) {
            String[] parts = response.split(":");
            String role = parts[2];
            System.out.println(" Connexion réussie !");

            if (role.equals("admin")) {
                showAdminMenu(parts[1]);
            } else {
                showClientMenu(parts[1]);
            }
        } else {
            System.out.println(" Email ou mot de passe incorrect.");
            showMainMenu();
        }
    }

    // ── Register ──────────────────────────────
    private void handleRegister() {
        System.out.println("\n=== INSCRIPTION ===");
        System.out.print("Nom : ");
        String nom = scanner.nextLine();

        System.out.print("Prénom : ");
        String prenom = scanner.nextLine();

        System.out.print("Email : ");
        String email = scanner.nextLine();

        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        System.out.print("Adresse : ");
        String address = scanner.nextLine();

        System.out.print("Téléphone : ");
        String phone = scanner.nextLine();

        System.out.print("Ville : ");
        String ville = scanner.nextLine();

        String response = sendRequest("REGISTER:" + nom + ":" + prenom + ":" +
                                       email + ":" + password + ":" +
                                       address + ":" + phone + ":" + ville);

        if (response != null && response.equals("REGISTER_SUCCESS")) {
            System.out.println("Inscription réussie ! Vous pouvez vous connecter.");
            showMainMenu();
        } else {
            System.out.println(" Inscription échouée. Email déjà utilisé ?");
            showMainMenu();
        }
    }

    // ── Menu Client ───────────────────────────
    private void showClientMenu(String clientId) {
        System.out.println("\n╔══════════════════════════╗");
        System.out.println("║     MENU CLIENT          ║");
        System.out.println("╠══════════════════════════╣");
        System.out.println("║  [1] Voir les produits   ║");
        System.out.println("║  [2] Gérer mon panier    ║");
        System.out.println("║  [3] Mes commandes       ║");
        System.out.println("║  [4] Se déconnecter      ║");
        System.out.println("╚══════════════════════════╝");
        System.out.print("Votre choix : ");

        String choix = scanner.nextLine();

        switch (choix) {
            case "1" -> {
                String response = sendRequest("GET_PRODUCTS");
                System.out.println(response);
                showClientMenu(clientId);
            }
            case "2" -> {
                String response = sendRequest("CART_GET:" + clientId);
                System.out.println(response);
                showClientMenu(clientId);
            }
            case "3" -> {
                System.out.println("Fonctionnalité à venir...");
                showClientMenu(clientId);
            }
            case "4" -> showMainMenu();
            default  -> {
                System.out.println("Choix invalide !");
                showClientMenu(clientId);
            }
        }
    }

    // ── Menu Admin ────────────────────────────
    private void showAdminMenu(String adminId) {
        System.out.println("\n╔══════════════════════════╗");
        System.out.println("║     MENU ADMIN           ║");
        System.out.println("╠══════════════════════════╣");
        System.out.println("║  [1] Gérer les produits  ║");
        System.out.println("║  [2] Gérer les commandes ║");
        System.out.println("║  [3] Se déconnecter      ║");
        System.out.println("╚══════════════════════════╝");
        System.out.print("Votre choix : ");

        String choix = scanner.nextLine();

        switch (choix) {
            case "1" -> {
                System.out.println("Gestion produits à venir...");
                showAdminMenu(adminId);
            }
            case "2" -> {
                System.out.println("Gestion commandes à venir...");
                showAdminMenu(adminId);
            }
            case "3" -> showMainMenu();
            default  -> {
                System.out.println("Choix invalide !");
                showAdminMenu(adminId);
            }
        }
    }

    // ── Fermer la connexion ───────────────────
    private void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (scanner != null) scanner.close();
        } catch (Exception e) {
            System.out.println("Erreur fermeture : " + e.getMessage());
        }
    }

    // ── Main ──────────────────────────────────
    public static void main(String[] args) {
        ClientApp app = new ClientApp();
        if (app.connectToServer()) {
            app.showMainMenu();
        }
    }
}