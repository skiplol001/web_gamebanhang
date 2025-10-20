package model;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
// import model.PlayerData; - Đã có ở đầu, không cần import lại

public class DatabaseStorage {
    private DataSource dataSource;

    public DatabaseStorage(DataSource dataSource) {
        this.dataSource = dataSource;
        initPlayerTable();
    }

    private void initPlayerTable() {
        // Chuyển từ Text Block sang chuỗi nối chuỗi (String Concatenation)
        String sql = 
            "CREATE TABLE IF NOT EXISTS player_data (" +
            "    player_id VARCHAR(50) PRIMARY KEY," +
            "    money INT DEFAULT 1000," +
            "    mental_points INT DEFAULT 100," +
            "    current_day INT DEFAULT 1," +
            "    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")";
        
        // Chuyển từ Text Block sang chuỗi nối chuỗi (String Concatenation)
        String inventorySql = 
            "CREATE TABLE IF NOT EXISTS player_inventory (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    player_id VARCHAR(50)," +
            "    item_name VARCHAR(100)," +
            "    quantity INT," +
            "    FOREIGN KEY (player_id) REFERENCES player_data(player_id)," +
            "    UNIQUE KEY unique_player_item (player_id, item_name)" +
            ")";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            stmt.execute(inventorySql);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khởi tạo player tables: " + e.getMessage(), e);
        }
    }

    public static PlayerData loadPlayerData(String playerId, DataSource dataSource) {
        String playerSql = "SELECT * FROM player_data WHERE player_id = ?";
        String inventorySql = "SELECT item_name, quantity FROM player_inventory WHERE player_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement playerStmt = conn.prepareStatement(playerSql);
             PreparedStatement inventoryStmt = conn.prepareStatement(inventorySql)) {
            
            playerStmt.setString(1, playerId);
            ResultSet playerRs = playerStmt.executeQuery();
            
            PlayerData playerData;
            if (playerRs.next()) {
                playerData = new PlayerData();
                playerData.money = playerRs.getInt("money");
                playerData.mentalPoints = playerRs.getInt("mental_points");
                playerData.currentDay = playerRs.getInt("current_day");
            } else {
                playerData = createNewPlayer(playerId, dataSource);
            }
            
            inventoryStmt.setString(1, playerId);
            ResultSet inventoryRs = inventoryStmt.executeQuery();
            
            Map<String, Integer> inventory = new HashMap<>();
            while (inventoryRs.next()) {
                inventory.put(inventoryRs.getString("item_name"), inventoryRs.getInt("quantity"));
            }
            playerData.inventory = inventory;
            
            return playerData;
            
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi load player data: " + e.getMessage(), e);
        }
    }

    private static PlayerData createNewPlayer(String playerId, DataSource dataSource) {
        String sql = "INSERT INTO player_data (player_id, money, mental_points, current_day) VALUES (?, 1000, 100, 1)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerId);
            stmt.executeUpdate();
            
            PlayerData playerData = new PlayerData();
            playerData.money = 1000;
            playerData.mentalPoints = 100;
            playerData.currentDay = 1;
            playerData.inventory = new HashMap<>();
            return playerData;
            
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tạo player mới: " + e.getMessage(), e);
        }
    }

    public static void savePlayerData(String playerId, PlayerData playerData, DataSource dataSource) {
        // Chuyển từ Text Block sang chuỗi nối chuỗi (String Concatenation)
        String updatePlayerSql = 
            "INSERT INTO player_data (player_id, money, mental_points, current_day) " + 
            "VALUES (?, ?, ?, ?) " + 
            "ON DUPLICATE KEY UPDATE " + 
            "money = VALUES(money), " + 
            "mental_points = VALUES(mental_points), " + 
            "current_day = VALUES(current_day)";
        
        String deleteInventorySql = "DELETE FROM player_inventory WHERE player_id = ?";
        String insertInventorySql = "INSERT INTO player_inventory (player_id, item_name, quantity) VALUES (?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement updatePlayerStmt = conn.prepareStatement(updatePlayerSql);
             PreparedStatement deleteInventoryStmt = conn.prepareStatement(deleteInventorySql);
             PreparedStatement insertInventoryStmt = conn.prepareStatement(insertInventorySql)) {
            
            conn.setAutoCommit(false);
            
            updatePlayerStmt.setString(1, playerId);
            updatePlayerStmt.setInt(2, playerData.money);
            updatePlayerStmt.setInt(3, playerData.mentalPoints);
            updatePlayerStmt.setInt(4, playerData.currentDay);
            updatePlayerStmt.executeUpdate();
            
            deleteInventoryStmt.setString(1, playerId);
            deleteInventoryStmt.executeUpdate();
            
            for (Map.Entry<String, Integer> entry : playerData.inventory.entrySet()) {
                if (entry.getValue() > 0) {
                    insertInventoryStmt.setString(1, playerId);
                    insertInventoryStmt.setString(2, entry.getKey());
                    insertInventoryStmt.setInt(3, entry.getValue());
                    insertInventoryStmt.addBatch();
                }
            }
            insertInventoryStmt.executeBatch();
            
            conn.commit();
            
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi save player data: " + e.getMessage(), e);
        }
    }
}