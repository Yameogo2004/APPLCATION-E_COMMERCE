package test1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestServerClient {
    public static void main(String[] args) {
        try (
            Socket socket = new Socket("localhost", 5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println("Serveur dit : " + in.readLine());

            out.println("PING");
            System.out.println("Réponse : " + in.readLine());

            out.println("CART_ADD:1:1:2");
            System.out.println("Réponse : " + in.readLine());

            out.println("CART_GET:1");
            System.out.println("Réponse : " + in.readLine());

            out.println("CART_REMOVE:1:1");
            System.out.println("Réponse : " + in.readLine());

            out.println("CART_GET:1");
            System.out.println("Réponse : " + in.readLine());

        } catch (Exception e) {
            System.out.println("Erreur client test : " + e.getMessage());
        }
    }
}