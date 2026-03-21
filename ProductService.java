package e_commerce;

import java.util.List;

public class ProductService {


	    private ProductDAO productDAO;

	    public ProductService() {
	        this.productDAO = new ProductDAO();
	    }

	    // 📌 Liste produits
	    public List<Product> getAllProducts() {
	        return productDAO.findAll();
	    }

	    // 📌 Détail produit
	    public Product getProductById(int id) {
	        Product p = productDAO.findById(id);

	        if (p == null) {
	            throw new RuntimeException("Produit introuvable !");
	        }

	        return p;
	    }

	    // 📌 Ajouter produit (admin)
	    public void addProduct(Product product) {
	        if (product.getName() == null || product.getName().isEmpty()) {
	            throw new RuntimeException("Nom produit obligatoire !");
	        }

	        if (product.getPrice() <= 0) {
	            throw new RuntimeException("Prix invalide !");
	        }

	        productDAO.save(product);
	    }

	    // 📌 Modifier produit
	    public void updateProduct(Product product) {
	        if (product.getIdProduct() <= 0) {
	            throw new RuntimeException("ID produit invalide !");
	        }

	        productDAO.update(product);
	    }

	    // 📌 Supprimer produit
	    public void deleteProduct(int id) {
	        if (id <= 0) {
	            throw new RuntimeException("ID invalide !");
	        }

	        productDAO.delete(id);
	    }
	}


