package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Category;
import model.Product;

public class ProductDAO {

    private Connection conn;

    public ProductDAO() {
        conn = database.DatabaseConnection.getConnection();
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();

        String sql = """
            SELECT p.id_product, p.name, p.description, p.image, p.price, p.stock,
                   c.id AS category_id, c.name AS category_name, c.description AS category_description
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category category = null;

                int catId = rs.getInt("category_id");
                if (!rs.wasNull()) {
                    category = new Category(
                            catId,
                            rs.getString("category_name"),
                            rs.getString("category_description")
                    );
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

    public Product findById(int id) {
        String sql = """
            SELECT p.id_product, p.name, p.description, p.image, p.price, p.stock,
                   c.id AS category_id, c.name AS category_name, c.description AS category_description
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE p.id_product = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category category = null;

                    int catId = rs.getInt("category_id");
                    if (!rs.wasNull()) {
                        category = new Category(
                                catId,
                                rs.getString("category_name"),
                                rs.getString("category_description")
                        );
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

    public void save(Product p) {
        String sql = "INSERT INTO products(name, description, image, price, stock, category_id) VALUES(?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getImage());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStock());

            if (p.getCategory() != null) {
                ps.setInt(6, p.getCategory().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setIdProduct(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Product p) {
        String sql = "UPDATE products SET name=?, description=?, image=?, price=?, stock=?, category_id=? WHERE id_product=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getImage());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStock());

            if (p.getCategory() != null) {
                ps.setInt(6, p.getCategory().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setInt(7, p.getIdProduct());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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