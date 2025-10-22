package model;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DatabaseStorage {
    
    public static PlayerData loadPlayerData(String playerId, javax.sql.DataSource dataSource) {
        // Chuyển playerId sang int để phù hợp với database
        int playerIdInt;
        try {
            playerIdInt = Integer.parseInt(playerId);
        } catch (NumberFormatException e) {
            // Nếu không phải số, mặc định dùng 1
            playerIdInt = 1;
            System.out.println("Chuyển playerId '" + playerId + "' thành: " + playerIdInt);
        }
        
        String playerSql = "SELECT Tien, Diem_Tinh_Than, So_Ngay_Choi FROM Nguoi_Choi WHERE ID_Nguoi_Choi = ?";
        String inventorySql = "SELECT Ten_SP, So_Luong FROM Kho_Do WHERE ID_Nguoi_Choi = ?";
        String unlockedSql = "SELECT Ten_SP FROM Vat_Pham_Mo_Khoa WHERE ID_Nguoi_Choi = ?";
        
        try (Connection conn = dataSource.getConnection()) {
            
            // 1. Load Player Data
            PlayerData playerData = null;
            try (PreparedStatement playerStmt = conn.prepareStatement(playerSql)) {
                playerStmt.setInt(1, playerIdInt);
                ResultSet playerRs = playerStmt.executeQuery();
                
                if (playerRs.next()) {
                    playerData = new PlayerData();
                    playerData.money = playerRs.getInt("Tien");
                    playerData.mentalPoints = playerRs.getInt("Diem_Tinh_Than");
                    playerData.currentDay = playerRs.getInt("So_Ngay_Choi");
                    System.out.println("Loaded player data for ID: " + playerIdInt);
                } else {
                    System.out.println("Player not found, creating new with ID: " + playerIdInt);
                    playerData = createNewPlayer(playerIdInt, dataSource);
                }
            }
            
            // 2. Load Inventory
            Map<String, Integer> inventory = new HashMap<>();
            try (PreparedStatement inventoryStmt = conn.prepareStatement(inventorySql)) {
                inventoryStmt.setInt(1, playerIdInt);
                ResultSet inventoryRs = inventoryStmt.executeQuery();
                
                while (inventoryRs.next()) {
                    inventory.put(inventoryRs.getString("Ten_SP"), inventoryRs.getInt("So_Luong"));
                }
            }
            playerData.inventory = inventory;
            
            // 3. Load Unlocked Items
            Set<String> unlockedItems = new HashSet<>();
            try (PreparedStatement unlockedStmt = conn.prepareStatement(unlockedSql)) {
                unlockedStmt.setInt(1, playerIdInt);
                ResultSet unlockedRs = unlockedStmt.executeQuery();
                
                while (unlockedRs.next()) {
                    unlockedItems.add(unlockedRs.getString("Ten_SP"));
                }
            }
            playerData.unlockedItems = unlockedItems;
            
            return playerData;
            
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi load player data: " + e.getMessage(), e);
        }
    }

    private static PlayerData createNewPlayer(int playerId, javax.sql.DataSource dataSource) {
        String sql = "INSERT INTO Nguoi_Choi (ID_Nguoi_Choi, Tien, Diem_Tinh_Than, So_Ngay_Choi) VALUES (?, 1000, 100, 1)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            stmt.executeUpdate();
            
            PlayerData playerData = new PlayerData();
            return playerData;
            
        } catch (SQLException e) {
            // Nếu lỗi (có thể ID đã tồn tại), thử load lại
            try {
                return loadExistingPlayer(playerId, dataSource);
            } catch (Exception ex) {
                throw new RuntimeException("Lỗi tạo player mới: " + e.getMessage(), e);
            }
        }
    }
    
    private static PlayerData loadExistingPlayer(int playerId, javax.sql.DataSource dataSource) {
        String sql = "SELECT Tien, Diem_Tinh_Than, So_Ngay_Choi FROM Nguoi_Choi WHERE ID_Nguoi_Choi = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                PlayerData playerData = new PlayerData();
                playerData.money = rs.getInt("Tien");
                playerData.mentalPoints = rs.getInt("Diem_Tinh_Than");
                playerData.currentDay = rs.getInt("So_Ngay_Choi");
                return playerData;
            } else {
                throw new RuntimeException("Player không tồn tại: " + playerId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi load existing player: " + e.getMessage(), e);
        }
    }

    public static void savePlayerData(String playerId, PlayerData playerData, javax.sql.DataSource dataSource) {
        // Chuyển playerId sang int
        int playerIdInt;
        try {
            playerIdInt = Integer.parseInt(playerId);
        } catch (NumberFormatException e) {
            playerIdInt = 1;
        }
        
        // Sử dụng MERGE cho SQL Server
        String updatePlayerSql = 
            "MERGE INTO Nguoi_Choi AS target " +
            "USING (VALUES (?, ?, ?, ?)) AS source (ID_Nguoi_Choi, Tien, Diem_Tinh_Than, So_Ngay_Choi) " +
            "ON target.ID_Nguoi_Choi = source.ID_Nguoi_Choi " +
            "WHEN MATCHED THEN " +
            "    UPDATE SET Tien = source.Tien, Diem_Tinh_Than = source.Diem_Tinh_Than, So_Ngay_Choi = source.So_Ngay_Choi " +
            "WHEN NOT MATCHED THEN " +
            "    INSERT (ID_Nguoi_Choi, Tien, Diem_Tinh_Than, So_Ngay_Choi) " +
            "    VALUES (source.ID_Nguoi_Choi, source.Tien, source.Diem_Tinh_Than, source.So_Ngay_Choi)";
            
        String deleteInventorySql = "DELETE FROM Kho_Do WHERE ID_Nguoi_Choi = ?";
        String insertInventorySql = "INSERT INTO Kho_Do (ID_Nguoi_Choi, Ten_SP, So_Luong) VALUES (?, ?, ?)";
        
        String deleteUnlockedSql = "DELETE FROM Vat_Pham_Mo_Khoa WHERE ID_Nguoi_Choi = ?";
        String insertUnlockedSql = "INSERT INTO Vat_Pham_Mo_Khoa (ID_Nguoi_Choi, Ten_SP) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            
            // 1. Cập nhật Player Data
            try (PreparedStatement updatePlayerStmt = conn.prepareStatement(updatePlayerSql)) {
                updatePlayerStmt.setInt(1, playerIdInt);
                updatePlayerStmt.setInt(2, playerData.money);
                updatePlayerStmt.setInt(3, playerData.mentalPoints);
                updatePlayerStmt.setInt(4, playerData.currentDay);
                updatePlayerStmt.executeUpdate();
            }

            // 2. Cập nhật Inventory
            try (PreparedStatement deleteInventoryStmt = conn.prepareStatement(deleteInventorySql);
                 PreparedStatement insertInventoryStmt = conn.prepareStatement(insertInventorySql)) {
                
                deleteInventoryStmt.setInt(1, playerIdInt);
                deleteInventoryStmt.executeUpdate();
                
                for (Map.Entry<String, Integer> entry : playerData.inventory.entrySet()) {
                    if (entry.getValue() > 0) {
                        insertInventoryStmt.setInt(1, playerIdInt);
                        insertInventoryStmt.setString(2, entry.getKey());
                        insertInventoryStmt.setInt(3, entry.getValue());
                        insertInventoryStmt.addBatch();
                    }
                }
                insertInventoryStmt.executeBatch();
            }
            
            // 3. Cập nhật Unlocked Items
            try (PreparedStatement deleteUnlockedStmt = conn.prepareStatement(deleteUnlockedSql);
                 PreparedStatement insertUnlockedStmt = conn.prepareStatement(insertUnlockedSql)) {
                
                deleteUnlockedStmt.setInt(1, playerIdInt);
                deleteUnlockedStmt.executeUpdate();
                
                for (String itemName : playerData.unlockedItems) {
                    insertUnlockedStmt.setInt(1, playerIdInt);
                    insertUnlockedStmt.setString(2, itemName);
                    insertUnlockedStmt.addBatch();
                }
                insertUnlockedStmt.executeBatch();
            }
            
            conn.commit();
            System.out.println("Saved player data for ID: " + playerIdInt);
            
        } catch (SQLException e) {
            try {
                Connection conn = dataSource.getConnection();
                conn.rollback();
            } catch (SQLException rollbackE) {
                // Log lỗi rollback
            }
            throw new RuntimeException("Lỗi save player data: " + e.getMessage(), e);
        }
    }
}