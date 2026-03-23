package service;

import dao.OtpDAO;
import java.security.SecureRandom;

public class OtpService {

    private OtpDAO otpDAO;
    private EmailService emailService;

    public OtpService() {
        this.otpDAO = new OtpDAO();
        this.emailService = new EmailService();
    }

    // Générer un code à 6 chiffres
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // entre 100000 et 999999
        return String.valueOf(code);
    }

    // Envoyer un OTP à l'email donné
    public boolean sendOtp(String email) {
        String code = generateCode();

        // Sauvegarder en BDD
        boolean saved = otpDAO.saveOtp(email, code);
        if (!saved) {
            System.out.println("Erreur sauvegarde OTP en BDD");
            return false;
        }

        // Envoyer par email
        boolean sent = emailService.sendOtpEmail(email, code);
        if (!sent) {
            System.out.println("Erreur envoi email OTP");
            return false;
        }

        System.out.println("OTP envoyé à : " + email);
        return true;
    }

    // Vérifier le code saisi par l'utilisateur
    public boolean verifyOtp(String email, String code) {
        if (email == null || email.isEmpty() ||
            code == null || code.isEmpty()) {
            return false;
        }

        boolean valid = otpDAO.verifyOtp(email, code);

        if (valid) {
            // Activer le compte
            otpDAO.activateAccount(email);
            System.out.println("Compte activé : " + email);
        } else {
            System.out.println("Code OTP invalide ou expiré pour : " + email);
        }

        return valid;
    }
}