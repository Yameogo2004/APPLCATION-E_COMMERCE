package test1;

import Client.ClientSocketService;

public class TestServerClient {

    public static void main(String[] args) {

        ClientSocketService client = new ClientSocketService();

        System.out.println("==============================================");
        System.out.println("   TEST TCP — ChriOnline");
        System.out.println("==============================================\n");

        // ── TEST 1 : Connexion au serveur ──
        System.out.println("── Test 1 : Connexion serveur ──");
        boolean connected = client.connect();
        if (connected) {
            System.out.println("✅ Connecté au serveur\n");
        } else {
            System.out.println("❌ Serveur inaccessible — lance Server.java d'abord !");
            return;
        }

        // ── TEST 2 : REGISTER via TCP ──
        System.out.println("── Test 2 : REGISTER via TCP ──");
        String testEmail = "tcp_" + System.currentTimeMillis() + "@chrionline.ma";
        String response = client.register(
            "Benjelloun",
            "Omar",
            testEmail,
            "MotDePasse456",
            "5 Avenue Mohammed V",
            "0655555555",
            "Tétouan"
        );
        System.out.println("   Réponse serveur : " + response);
        if ("REGISTER_SUCCESS".equals(response)) {
            System.out.println("✅ REGISTER OK\n");
        } else {
            System.out.println("❌ REGISTER échoué\n");
            client.close();
            return;
        }

        // ── TEST 3 : LOGIN correct via TCP ──
        System.out.println("── Test 3 : LOGIN correct via TCP ──");
        String loginResponse = client.login(testEmail, "MotDePasse456");
        System.out.println("   Réponse serveur : " + loginResponse);
        if (loginResponse != null && loginResponse.startsWith("LOGIN_SUCCESS")) {
            String[] parts = loginResponse.split(":", 4);
            System.out.println("✅ LOGIN OK !");
            System.out.println("   ID   : " + parts[1]);
            System.out.println("   Role : " + parts[2]);
            System.out.println("   Nom  : " + parts[3] + "\n");
        } else {
            System.out.println("❌ LOGIN échoué\n");
            client.close();
            return;
        }

        // ── TEST 4 : LOGIN mauvais mot de passe ──
        System.out.println("── Test 4 : LOGIN mauvais mot de passe ──");
        String badResponse = client.login(testEmail, "MauvaisMotDePasse");
        System.out.println("   Réponse serveur : " + badResponse);
        if ("ERROR:LOGIN_FAILED".equals(badResponse)) {
            System.out.println("✅ Mauvais mot de passe rejeté\n");
        } else {
            System.out.println("❌ Problème sécurité !\n");
        }

        // ── TEST 5 : LOGIN email inexistant ──
        System.out.println("── Test 5 : LOGIN email inexistant ──");
        String unknownResponse = client.login("fantome@chrionline.ma", "nimporte");
        System.out.println("   Réponse serveur : " + unknownResponse);
        if ("ERROR:LOGIN_FAILED".equals(unknownResponse)) {
            System.out.println("✅ Email inconnu rejeté\n");
        } else {
            System.out.println("❌ Problème détection email\n");
        }

        // ── TEST 6 : REGISTER email déjà utilisé ──
        System.out.println("── Test 6 : REGISTER email déjà utilisé ──");
        String dupResponse = client.register(
            "Benjelloun", "Omar", testEmail,
            "AutrePass", "Adresse", "0600000000", "Fès"
        );
        System.out.println("   Réponse serveur : " + dupResponse);
        if ("ERROR:REGISTER_FAILED".equals(dupResponse)) {
            System.out.println("✅ Doublon rejeté\n");
        } else {
            System.out.println("❌ Doublon accepté !\n");
        }

        System.out.println("==============================================");
        System.out.println("   TESTS TCP TERMINÉS");
        System.out.println("==============================================");

        client.close();
    }
}