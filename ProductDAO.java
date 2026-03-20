package e_commerce;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private Connection conn;

    public ProductDAO() {
        conn = database.DatabaseConnection.getConnection(); // Singleton
    }

    // 1. Lister tous les produits
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = null;
                int catId = rs.getInt("category_id");
                if (!rs.wasNull()) {
                    category = new Category(catId, null, null); // tu peux compléter le nom et description si besoin
                }

                Product p = new Product(
                        rs.getInt("id_product"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
                p.setCategory(category);

                products.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    // 🔹 2. Trouver un produit par ID
    public Product findById(int id) {
        String sql = "SELECT * FROM products WHERE id_product = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category category = null;
                    int catId = rs.getInt("category_id");
                    if (!rs.wasNull()) {
                        category = new Category(catId, null, null);
                    }

                    Product p = new Product(
                            rs.getInt("id_product"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("image"),
                            rs.getDouble("price"),
                            rs.getInt("stock")
                    );
                    p.setCategory(category);

                    return p;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 🔹 3. Ajouter un produit
    public void save(Product p) {
        String sql = "INSERT INTO products(name, description, price, stock, image, category_id) VALUES(?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getImage());
            ps.setInt(6, p.getCategory() != null ? p.getCategory().getId() : 0);

            ps.executeUpdate();

            // Récupérer l'ID auto-généré
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setIdProduct(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔹 4. Modifier un produit
    public void update(Product p) {
        String sql = "UPDATE products SET name=?, description=?, price=?, stock=?, image=?, category_id=? WHERE id_product=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getImage());
            ps.setInt(6, p.getCategory() != null ? p.getCategory().getId() : 0);
            ps.setInt(7, p.getIdProduct());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5. Supprimer un produit
    public void delete(int id) {
        String sql = "DELETE FROM products WHERE id_product=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
