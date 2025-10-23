package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import util.DBConnection;

public class NguoiChoiDAO {
    
    // --- TẢI DỮ LIỆU NGƯỜI CHƠI ---
    public PlayerData loadPlayerData(int playerId) throws SQLException {
        PlayerData playerData = null;
        
        // 1. Tải dữ liệu chính từ Nguoi_Choi
        String sqlPlayer = "SELECT Tien, Diem_Tinh_Than, So_Ngay_Choi FROM Nguoi_Choi WHERE ID_Nguoi_Choi = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psPlayer = conn.prepareStatement(sqlPlayer)) {
            
            psPlayer.setInt(1, playerId);
            try (ResultSet rsPlayer = psPlayer.executeQuery()) {
                if (rsPlayer.next()) {
                    playerData = new PlayerData();
                    playerData.money = rsPlayer.getInt("Tien");
                    playerData.mentalPoints = rsPlayer.getInt("Diem_Tinh_Than");
                    playerData.currentDay = rsPlayer.getInt("So_Ngay_Choi");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải dữ liệu Player chính: " + e.getMessage());
            throw e;
        }
        
        if (playerData == null) return null; 
        
        // 2. Tải Inventory từ Kho_Do
        String sqlInventory = "SELECT Ten_SP, So_Luong FROM Kho_Do WHERE ID_Nguoi_Choi = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psInv = conn.prepareStatement(sqlInventory)) {
            
            psInv.setInt(1, playerId);
            try (ResultSet rsInv = psInv.executeQuery()) {
                while (rsInv.next()) {
                    playerData.inventory.put(rsInv.getString("Ten_SP"), rsInv.getInt("So_Luong"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải Inventory: " + e.getMessage());
            throw e;
        }
        
        // 3. Tải Vật phẩm đã mở khóa từ Vat_Pham_Mo_Khoa
        String sqlUnlock = "SELECT Ten_SP FROM Vat_Pham_Mo_Khoa WHERE ID_Nguoi_Choi = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psUnlock = conn.prepareStatement(sqlUnlock)) {
            
            psUnlock.setInt(1, playerId);
            try (ResultSet rsUnlock = psUnlock.executeQuery()) {
                while (rsUnlock.next()) {
                    // PlayerData.unlockedItems là Set<String>
                    playerData.unlockedItems.add(rsUnlock.getString("Ten_SP"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải Vật phẩm mở khóa: " + e.getMessage());
            throw e;
        }
        
        return playerData;
    }

    // --- LƯU DỮ LIỆU NGƯỜI CHƠI ---
    public void savePlayerData(int playerId, PlayerData data) throws SQLException {
        // 1. Cập nhật dữ liệu chính vào Nguoi_Choi
        // Giả định bạn đã có logic cập nhật trường Ten_Nguoi_Choi nếu cần
        String sqlPlayer = "UPDATE Nguoi_Choi SET Tien=?, Diem_Tinh_Than=?, So_Ngay_Choi=? WHERE ID_Nguoi_Choi = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psPlayer = conn.prepareStatement(sqlPlayer)) {
            
            psPlayer.setInt(1, data.money);
            psPlayer.setInt(2, data.mentalPoints);
            psPlayer.setInt(3, data.currentDay);
            psPlayer.setInt(4, playerId);
            psPlayer.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu dữ liệu Player chính: " + e.getMessage());
            throw e;
        }
        
        // 2. Cập nhật Inventory vào Kho_Do
        String sqlDeleteInv = "DELETE FROM Kho_Do WHERE ID_Nguoi_Choi = ?";
        String sqlInsertInv = "INSERT INTO Kho_Do (ID_Nguoi_Choi, Ten_SP, So_Luong) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection()) {
             // Xóa cũ
            try (PreparedStatement psDel = conn.prepareStatement(sqlDeleteInv)) {
                psDel.setInt(1, playerId);
                psDel.executeUpdate();
            }
            // Thêm mới
            try (PreparedStatement psIns = conn.prepareStatement(sqlInsertInv)) {
                for (Map.Entry<String, Integer> entry : data.inventory.entrySet()) {
                    if (entry.getValue() > 0) { // Chỉ lưu những mặt hàng có số lượng > 0
                        psIns.setInt(1, playerId);
                        psIns.setString(2, entry.getKey());
                        psIns.setInt(3, entry.getValue());
                        psIns.addBatch(); 
                    }
                }
                psIns.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu Inventory: " + e.getMessage());
            throw e;
        }
    }

    // --- TẠO NGƯỜI CHƠI MỚI ---
    public void createNewPlayer(int playerId, String tenNguoiChoi) throws SQLException {
        // Kiểm tra xem ID đã tồn tại chưa
        if (loadPlayerData(playerId) != null) {
            System.out.println("Người chơi với ID " + playerId + " đã tồn tại. Bỏ qua tạo mới.");
            return;
        }
        
        // 1. Thêm vào Nguoi_Choi với giá trị mặc định (1000 tiền, 100 tinh thần, ngày 1)
        String sqlInsertPlayer = "INSERT INTO Nguoi_Choi (ID_Nguoi_Choi, Ten_Nguoi_Choi, Tien, Diem_Tinh_Than, So_Ngay_Choi) VALUES (?, ?, 1000, 100, 1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsertPlayer)) {
            
            ps.setInt(1, playerId);
            ps.setString(2, tenNguoiChoi);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo Player mới: " + e.getMessage());
            throw e;
        }
    }
}