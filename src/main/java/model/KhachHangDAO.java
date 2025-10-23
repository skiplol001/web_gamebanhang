package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection; 

public class KhachHangDAO {
    
    // Phương thức tải Hồ sơ Khách hàng Gốc
    public List<KhachHang> loadDailyCustomerProfiles() throws SQLException {
        List<KhachHang> customers = new ArrayList<>();
        
        // Truy vấn cần cột La_Vong
        String sql = "SELECT Ma_KH, Ten_Khach_Hang, Tuoi, Gioi_Tinh, La_Vong FROM Khach_Hang_Goc ORDER BY Ma_KH";
        
        System.out.println("🔎 DEBUG DAO: Bắt đầu thực hiện truy vấn SQL: " + sql);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
                
            System.out.println("✅ DEBUG DAO: Đã thực thi truy vấn. Bắt đầu đọc dữ liệu...");
            int count = 0;
            
            while (rs.next()) {
                String maKh = rs.getString("Ma_KH");
                String tenKh = rs.getString("Ten_Khach_Hang");
                int tuoi = rs.getInt("Tuoi");
                String gioiTinh = rs.getString("Gioi_Tinh");
                // Đảm bảo tên cột 'La_Vong' khớp chính xác
                boolean laVong = rs.getBoolean("La_Vong"); 
                
                // Giả định KhachHang constructor là: (ten, tuoi, gioiTinh, maKh, laVong)
                KhachHang customer = new KhachHang(tenKh, tuoi, gioiTinh, maKh, laVong);
                customers.add(customer);
                count++;
                
                // Debug chi tiết cho 1 khách hàng đầu tiên
                if (count == 1) {
                    System.out.println("✅ DEBUG DAO: Đã tải khách hàng đầu tiên: " + customer.toString());
                }
            }
            
            System.out.println("🔎 DEBUG DAO: Kết thúc đọc ResultSet. Tổng số khách hàng tải được: " + customers.size());
            
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tải danh sách Hồ sơ Khách hàng Gốc: " + e.getMessage());
            e.printStackTrace(); // Rất quan trọng để xem lỗi kết nối/SQL
            throw e; 
        }
        
        if (customers.isEmpty()) {
            System.out.println("⚠️ DEBUG DAO: Danh sách khách hàng RỖNG sau khi truy vấn. Kiểm tra dữ liệu trong DB!");
        }
        
        return customers;
    }
}