package test1;

import database.DatabaseConnection;
import service.AuthService;
import model.User;
import dao.UserDAO;

public class TestAuth {

    public static void main(String[] args) {

        AuthService authService = new AuthService();
        UserDAO userDAO = new UserDAO();

        System.out.println("==============================================");
        System.out.println("   TEST AUTHENTIFICATION — ChriOnline");
        System.out.println("==============================================\n");

        // ── TEST 1 : Connexion à la base de données ──
        System.out.println("── Test 1 : Connexion BDD ──");
        if (DatabaseConnection.getConnection() != null) {
            System.out.println("✅ BDD connectée\n");
        } else {
            System.out.println("❌ Echec connexion BDD — arrêt des tests");
            return;
        }

        // ── TEST 2 : Inscription d'un nouveau client ──
        System.out.println("── Test 2 : Inscription ──");
        String testEmail = "test_" + System.currentTimeMillis() + "@chrionline.ma";
        boolean registered = authService.register(
            "Alami",
            "Youssef",
            testEmail,
            "MonMotDePasse123",
            "12 Rue Hassan II",
            "0661234567",
            "Tétouan"
        );
        if (registered) {
            System.out.println("✅ Inscription réussie — email : " + testEmail + "\n");
        } else {
            System.out.println("❌ Inscription échouée\n");
            return;
        }

        // ── TEST 3 : Inscription avec email déjà utilisé ──
        System.out.println("── Test 3 : Email déjà utilisé ──");
        boolean duplicate = authService.register(
            "Alami", "Youssef", testEmail,
            "AutreMotDePasse", "Adresse", "0600000000", "Fès"
        );
        if (!duplicate) {
            System.out.println("✅ Doublon correctement rejeté\n");
        } else {
            System.out.println("❌ Doublon accepté — problème emailExists()\n");
        }

        // ── TEST 4 : Login correct ──
        System.out.println("── Test 4 : Login correct ──");
        User user = authService.login(testEmail, "MonMotDePasse123");
        if (user != null) {
            System.out.println("✅ Login réussi !");
            System.out.println("   ID   : " + user.getId());
            System.out.println("   Nom  : " + user.getNom());
            System.out.println("   Role : " + user.getRole() + "\n");
        } else {
            System.out.println("❌ Login échoué — vérifier AuthService.checkPassword()\n");
            return;
        }

        // ── TEST 5 : Login avec mauvais mot de passe ──
        System.out.println("── Test 5 : Mauvais mot de passe ──");
        User badLogin = authService.login(testEmail, "MauvaisMotDePasse");
        if (badLogin == null) {
            System.out.println("✅ Mauvais mot de passe correctement rejeté\n");
        } else {
            System.out.println("❌ Mauvais mot de passe accepté — problème sécurité !\n");
        }

        // ── TEST 6 : Login avec email inexistant ──
        System.out.println("── Test 6 : Email inexistant ──");
        User unknownLogin = authService.login("inconnu@chrionline.ma", "nimporte");
        if (unknownLogin == null) {
            System.out.println("✅ Email inconnu correctement rejeté\n");
        } else {
            System.out.println("❌ Email inconnu accepté — problème UserDAO\n");
        }

        // ── TEST 7 : Transaction — vérifier que le client est bien dans les 2 tables ──
        System.out.println("── Test 7 : Transaction BDD (users + clients) ──");
        User saved = userDAO.findByEmail(testEmail);
        if (saved != null && saved.getId() > 0) {
            System.out.println("✅ Utilisateur trouvé dans les deux tables");
            System.out.println("   ID en BDD : " + saved.getId() + "\n");
        } else {
            System.out.println("❌ Utilisateur introuvable — problème transaction\n");
        }

        System.out.println("==============================================");
        System.out.println("   TESTS TERMINÉS");
        System.out.println("==============================================");

        DatabaseConnection.closeConnection();
    }
}