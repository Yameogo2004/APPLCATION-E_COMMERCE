package model;

public class Main {
    public static void main(String[] args) {

    	Client client = new Client(
    	        "Yameogo",
    	        "Ariel",
    	        "ariel@email.com",
    	        "1234",
    	        "Rue Mounia",
    	        "0600000000",
    	        "Tetouan"
    	);

    	System.out.println(client);

    	boolean login = client.login("ariel@email.com","1234");
    	System.out.println("Login client : " + login);

    	client.viewProfile();

    	client.updateProfile("Nouvelle adresse","0611111111","Casablanca");

    	client.logout();


    	Administrateur admin = new Administrateur(
    	        "Admin",
    	        "System",
    	        "admin@email.com",
    	        "admin123"
    	);

    	System.out.println(admin);

    	boolean loginAdmin = admin.login("admin@email.com","admin123");
    	System.out.println("Login admin : " + loginAdmin);

    	admin.addProduct();
    	admin.manageOrders();
    	admin.logout();
    }
}