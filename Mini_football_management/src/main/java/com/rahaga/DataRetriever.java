package com.rahaga;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection dbConnection = new DBConnection();

    
    public Team findTeamById(Integer id) {
        Team team = null;

        String teamSql = "SELECT * FROM team WHERE id = ?";
        String playerSql = "SELECT * FROM player WHERE id_team = ?";

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement teamStmt = connection.prepareStatement(teamSql);
             PreparedStatement playerStmt = connection.prepareStatement(playerSql)) {

            teamStmt.setInt(1, id);
            ResultSet teamRs = teamStmt.executeQuery();

            if (teamRs.next()) {
                team = new Team(
                        teamRs.getInt("id"),
                        teamRs.getString("name"),
                        ContinentEnum.valueOf(teamRs.getString("continent"))
                );

                playerStmt.setInt(1, id);
                ResultSet playerRs = playerStmt.executeQuery();

                while (playerRs.next()) {
                    Player player = new Player(
                            playerRs.getInt("id"),
                            playerRs.getString("name"),
                            playerRs.getInt("age"),
                            PlayerPositionEnum.valueOf(playerRs.getString("position")),
                            team
                    );
                    team.getPlayers().add(player);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return team;
    }


    public List<Player> findPlayers(int page, int size) {
        List<Player> players = new ArrayList<>();
        int offset = (page - 1) * size;

        String sql = "SELECT * FROM player ORDER BY id LIMIT ? OFFSET ?";

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                players.add(new Player(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        PlayerPositionEnum.valueOf(rs.getString("position")),
                        null
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    
    public List<Player> createPlayers(List<Player> newPlayers) {
        String checkSql = "SELECT COUNT(*) FROM player WHERE name = ?";
        String insertSql = "INSERT INTO player(name, age, position) VALUES (?, ?, ?)";

        try (Connection connection = dbConnection.getDBConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql);
                 PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {

                for (Player player : newPlayers) {
                    checkStmt.setString(1, player.getName());
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();

                    if (rs.getInt(1) > 0) {
                        throw new RuntimeException("Player already exists: " + player.getName());
                    }

                    insertStmt.setString(1, player.getName());
                    insertStmt.setInt(2, player.getAge());
                    insertStmt.setString(3, player.getPosition().name());
                    insertStmt.executeUpdate();
                }

                connection.commit();
                return newPlayers;

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    
    public Team saveTeam(Team team) {
        String updateTeamSql = "UPDATE team SET name = ?, continent = ? WHERE id = ?";
        String updatePlayerSql = "UPDATE player SET id_team = ? WHERE id = ?";

        try (Connection connection = dbConnection.getDBConnection()) {

            try (PreparedStatement teamStmt = connection.prepareStatement(updateTeamSql);
                 PreparedStatement playerStmt = connection.prepareStatement(updatePlayerSql)) {

                teamStmt.setString(1, team.getName());
                teamStmt.setString(2, team.getContinent().name());
                teamStmt.setInt(3, team.getId());
                teamStmt.executeUpdate();

                for (Player player : team.getPlayers()) {
                    playerStmt.setInt(1, team.getId());
                    playerStmt.setInt(2, player.getId());
                    playerStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return team;
    }

 
    public List<Team> findTeamsByPlayerName(String playerName) {
        List<Team> teams = new ArrayList<>();

        String sql = """
            SELECT DISTINCT t.*
            FROM team t
            JOIN player p ON p.id_team = t.id
            WHERE p.name ILIKE ?
        """;

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + playerName + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                teams.add(new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        ContinentEnum.valueOf(rs.getString("continent"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teams;
    }

    
    public List<Player> findPlayersByCriteria(
            String playerName,
            PlayerPositionEnum position,
            String teamName,
            ContinentEnum continent,
            int page,
            int size) {

        List<Player> players = new ArrayList<>();
        int offset = (page - 1) * size;

        StringBuilder sql = new StringBuilder("""
            SELECT p.*
            FROM player p
            LEFT JOIN team t ON p.id_team = t.id
            WHERE 1=1
        """);

        if (playerName != null) sql.append(" AND p.name ILIKE ?");
        if (position != null) sql.append(" AND p.position = ?");
        if (teamName != null) sql.append(" AND t.name ILIKE ?");
        if (continent != null) sql.append(" AND t.continent = ?");

        sql.append(" ORDER BY p.id LIMIT ? OFFSET ?");

        try (Connection connection = dbConnection.getDBConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            int index = 1;

            if (playerName != null) stmt.setString(index++, "%" + playerName + "%");
            if (position != null) stmt.setString(index++, position.name());
            if (teamName != null) stmt.setString(index++, "%" + teamName + "%");
            if (continent != null) stmt.setString(index++, continent.name());

            stmt.setInt(index++, size);
            stmt.setInt(index, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                players.add(new Player(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        PlayerPositionEnum.valueOf(rs.getString("position")),
                        null
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return players;
    }
}
 
