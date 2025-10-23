package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

public class VatPhamDAO {
    
    public List<Item> getAllAvailableItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT Ten_SP, Gia_Ban, Gia_Mo_Khoa FROM Vat_Pham"; 
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String tenSp = rs.getString("Ten_SP");
                int giaBan = rs.getInt("Gia_Ban");
                int giaMoKhoa = rs.getInt("Gia_Mo_Khoa");
                
                Item item = new Item(tenSp, giaBan, giaMoKhoa);
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách Vật phẩm: " + e.getMessage());
            throw e;
        }
        return items;
    }
}