package com.rahaga;



import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;




public class DataRetriever {



    public Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                select dish.id as dish_id, dish.name as dish_name, dish_type, dish.selling_price as dish_price
                from dish
                where dish.id = ?;
            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                dish.setPrice(resultSet.getObject("dish_price") == null ? null : resultSet.getDouble("dish_price"));
                dish.setDishIngredientList(getLinkedDishIngredients(resultSet.getInt("dish_id")));
                return dish;
            }

            throw new RuntimeException("Dish not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public Dish saveDish(Dish toSave) {
        String upsertDishSql = """
            INSERT INTO dish (id, selling_price, name, dish_type)
            VALUES (?, ?, ?, ?::dish_type)
            ON CONFLICT (id) DO UPDATE
            SET name = EXCLUDED.name,
                dish_type = EXCLUDED.dish_type,
                selling_price = EXCLUDED.selling_price
            RETURNING id
        """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            Integer dishId;

            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                ps.setInt(1, toSave.getId() != null ? toSave.getId() : getNextSerialValue(conn, "dish", "id"));
                if (toSave.getPrice() != null) ps.setDouble(2, toSave.getPrice());
                else ps.setNull(2, Types.DOUBLE);
                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getDishType().name());

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }

            detachIngredients(conn, dishId, toSave.getIngredients());
            attachIngredients(conn, dishId, toSave.getDishIngredientList());

            conn.commit();
            return findDishById(dishId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) return List.of();

        List<Ingredient> savedIngredients = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();

        try {
            conn.setAutoCommit(false);
            String insertSql = """
                INSERT INTO ingredient (id, name, category, price)
                VALUES (?, ?, ?::ingredient_category, ?)
                RETURNING id
            """;

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : newIngredients) {
                    ps.setInt(1, ingredient.getId() != null ? ingredient.getId() : getNextSerialValue(conn, "ingredient", "id"));
                    ps.setString(2, ingredient.getName());
                    ps.setString(3, ingredient.getCategory().name());
                    ps.setDouble(4, ingredient.getPrice());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        ingredient.setId(rs.getInt(1));
                        savedIngredients.add(ingredient);
                    }
                }
                conn.commit();
                return savedIngredients;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }



    public Order findOrderByReference(String reference) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();

        try {
            PreparedStatement ps = connection.prepareStatement("""
                SELECT id, reference, order_type, order_status
                FROM "order"
                WHERE reference = ?
            """);

            ps.setString(1, reference);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setReference(rs.getString("reference"));
                order.setOrderType(OrderTypeEnum.valueOf(rs.getString("order_type")));
                order.setOrderStatus(OrderStatusEnum.valueOf(rs.getString("order_status")));
                return order;
            }

            throw new RuntimeException("Order not found: " + reference);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public Order saveOrder(Order orderToSave) {

        if (orderToSave.getId() != null) {
            Order existing = findOrderByReference(orderToSave.getReference());
            if (existing.getOrderStatus() == OrderStatusEnum.DELIVERED) {
                throw new IllegalStateException("Une commande livrée ne peut plus être modifiée.");
            }
        }

        String sql = """
            INSERT INTO "order" (id, reference, order_type, order_status)
            VALUES (?, ?, ?::order_type_enum, ?::order_status_enum)
            ON CONFLICT (id) DO UPDATE
            SET order_type = EXCLUDED.order_type,
                order_status = EXCLUDED.order_status
            RETURNING id
        """;

        try (Connection conn = new DBConnection().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, orderToSave.getId() != null ? orderToSave.getId() : getNextSerialValue(conn, "order", "id"));
            ps.setString(2, orderToSave.getReference());
            ps.setString(3, orderToSave.getOrderType().name());
            ps.setString(4, orderToSave.getOrderStatus().name());

            ps.executeQuery();
            return findOrderByReference(orderToSave.getReference());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private void detachIngredients(Connection conn, Integer dishId, List<Ingredient> ingredients) throws SQLException {
        if (ingredients == null || ingredients.isEmpty()) {
            try (PreparedStatement ps = conn.prepareStatement("delete from dishingredient where id_dish = ?")) {
                ps.setInt(1, dishId);
                ps.executeUpdate();
            }
            return;
        }

        String inClause = ingredients.stream().map(i -> "?").collect(Collectors.joining(","));
        String sql = "delete from dishingredient where id_dish = ? and id_ingredient not in (" + inClause + ")";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            int idx = 2;
            for (Ingredient i : ingredients) ps.setInt(idx++, i.getId());
            ps.executeUpdate();
        }
    }

    private void attachIngredients(Connection conn, Integer dishId, List<DishIngredient> dishIngredients) throws SQLException {
        if (dishIngredients == null || dishIngredients.isEmpty()) return;

        String sql = """
            insert into dishingredient (id_dish, id_ingredient, quantity_required, unit)
            values (?,?,?,?::unit_type)
            on conflict do update
            set quantity_required= EXCLUDED.quantity_required,
                unit= EXCLUDED.unit;
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient di : dishIngredients) {
                ps.setInt(1, di.getId_dish());
                ps.setInt(2, di.getId_ingredient());
                ps.setDouble(3, di.getQuantity_required());
                ps.setString(4, di.getUnit().name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<DishIngredient> getLinkedDishIngredients(Integer idDish) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<DishIngredient> dishIngredients = new ArrayList<>();

        String sql = "select id, id_dish, id_ingredient, quantity_required, unit from dishingredient where id_dish = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idDish);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                dishIngredients.add(new DishIngredient(
                        rs.getInt("id"),
                        rs.getInt("id_dish"),
                        rs.getInt("id_ingredient"),
                        rs.getDouble("quantity_required"),
                        UnitType.valueOf(rs.getString("unit"))
                ));
            }
            return dishIngredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    private String getSerialSequenceName(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "SELECT pg_get_serial_sequence(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString(1) : null;
        }
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName) throws SQLException {
        String seq = getSerialSequenceName(conn, tableName, columnName);
        if (seq == null) throw new IllegalArgumentException("No sequence for " + tableName + "." + columnName);
        updateSequenceNextValue(conn, tableName, columnName, seq);

        try (PreparedStatement ps = conn.prepareStatement("SELECT nextval(?)")) {
            ps.setString(1, seq);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String sql = String.format("SELECT setval('%s', (SELECT COALESCE(MAX(%s),0) FROM %s))",
                sequenceName, columnName, tableName);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeQuery();
        }
    }
}