package model;

import java.sql.*;
import java.util.*;
import javax.sql.DataSource;

public class QuanLyKhachHang {
    private DataSource dataSource;

    public QuanLyKhachHang(DataSource dataSource) {
        this.dataSource = dataSource;
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = dataSource.getConnection()) {
            Statement stmt = conn.createStatement();
            
            // Kiểm tra và tạo bảng Khach_Hang_Goc nếu chưa tồn tại
            String createKhachHangThatTable = 
                "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Khach_Hang_Goc' AND xtype='U') " +
                "CREATE TABLE Khach_Hang_Goc (" +
                "   Ma_KH VARCHAR(20) PRIMARY KEY," + 
                "   Ten_Khach_Hang NVARCHAR(100) NOT NULL," +
                "   Tuoi INT NOT NULL," +
                "   Gioi_Tinh NVARCHAR(10) NOT NULL" + 
                ")";
            
            // Kiểm tra và tạo bảng Khach_Hang_Tam_Thoi nếu chưa tồn tại
            String createKhachHangTable = 
                "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Khach_Hang_Tam_Thoi' AND xtype='U') " +
                "CREATE TABLE Khach_Hang_Tam_Thoi (" + 
                "   ID INT IDENTITY(1,1) PRIMARY KEY," +
                "   Ten_Khach_Hang NVARCHAR(100) NOT NULL," +
                "   Tuoi INT NOT NULL," +
                "   Gioi_Tinh NVARCHAR(10) NOT NULL," +
                "   Ma_KH VARCHAR(20) NOT NULL," +
                "   La_Vong BIT NOT NULL," +
                "   Ngay_Tao DATE DEFAULT CAST(GETDATE() AS DATE)" +
                ")";
            
            stmt.execute(createKhachHangThatTable);
            stmt.execute(createKhachHangTable);
            
            if (isKhachHangThatEmpty()) {
                insertDefaultKhachHangThat();
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khởi tạo database Khách Hàng: " + e.getMessage(), e);
        }
    }

    private boolean isKhachHangThatEmpty() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Khach_Hang_Goc")) {
            
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            return true; 
        }
    }

    private void insertDefaultKhachHangThat() {
        List<KhachHang> defaultCustomers = Arrays.asList(
             new KhachHang("Liễu Như Yên", 25, "Nữ", "KH001", false),
             new KhachHang("Tạ Minh Kha", 30, "Nam", "KH002", false),
             new KhachHang("Tiểu Lạc", 18, "Nữ", "KH003", false),
             new KhachHang("Shyn Mụi Mụi", 20, "Nữ", "KH004", false),
             new KhachHang("Tăng Quốc Cường", 28, "Nam", "KH005", false),
             new KhachHang("Huyền Thanh Tố Uyển", 19, "Nữ", "KH10", false),
             new KhachHang("Lữ Khách Phương Bắc", 18, "Nam", "KH012", false),
             new KhachHang("Vua Ăn Đòn", 18, "Nam", "KH013", false)
        );

        String sql = "INSERT INTO Khach_Hang_Goc (Ten_Khach_Hang, Tuoi, Gioi_Tinh, Ma_KH) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (KhachHang kh : defaultCustomers) {
                pstmt.setString(1, kh.getTen());
                pstmt.setInt(2, kh.getTuoi());
                pstmt.setString(3, kh.getGioiTinh());
                pstmt.setString(4, kh.getMaKH());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm khách hàng mặc định: " + e.getMessage(), e);
        }
    }

    public List<KhachHang> layDanhSachKhachHangHomNay() {
        List<KhachHang> result = new ArrayList<>();
        Random random = new Random();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Khach_Hang_Goc")) {
            
            while (rs.next()) {
                // Tỷ lệ xuất hiện "Khách Hàng Vòng" là 30%
                boolean laVong = random.nextDouble() < 0.3;

                if (laVong) {
                    // Tạo thông tin ngẫu nhiên cho Khách Vòng
                    int tuoiVong = 100 + random.nextInt(100);
                    String maVong = "V" + (1000 + random.nextInt(9000));
                    
                    result.add(new KhachHang(
                        rs.getString("Ten_Khach_Hang"), // Tên thật làm tên giả
                        tuoiVong,
                        rs.getString("Gioi_Tinh"),
                        maVong,
                        true
                    ));
                } else {
                    // Khách hàng thật
                    result.add(new KhachHang(
                        rs.getString("Ten_Khach_Hang"),
                        rs.getInt("Tuoi"),
                        rs.getString("Gioi_Tinh"),
                        rs.getString("Ma_KH"),
                        false
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách khách hàng gốc: " + e.getMessage(), e);
        }

        luuDanhSachKhachHang(result);
        return result;
    }

    private void luuDanhSachKhachHang(List<KhachHang> danhSach) {
        // Xóa dữ liệu cũ của ngày hôm nay trước
        String deleteSql = "DELETE FROM Khach_Hang_Tam_Thoi WHERE Ngay_Tao = CAST(GETDATE() AS DATE)";
        String insertSql = "INSERT INTO Khach_Hang_Tam_Thoi (Ten_Khach_Hang, Tuoi, Gioi_Tinh, Ma_KH, La_Vong) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            
            // Xóa dữ liệu cũ
            deleteStmt.executeUpdate();
            
            // Thêm dữ liệu mới
            for (KhachHang kh : danhSach) {
                insertStmt.setString(1, kh.getTen());
                insertStmt.setInt(2, kh.getTuoi());
                insertStmt.setString(3, kh.getGioiTinh());
                insertStmt.setString(4, kh.getMaKH());
                insertStmt.setBoolean(5, kh.isLaVong());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu danh sách khách hàng tạm thời: " + e.getMessage(), e);
        }
    }

    public List<KhachHang> taiDanhSachKhachHang() {
        List<KhachHang> result = new ArrayList<>();
        // Sử dụng CAST(GETDATE() AS DATE) cho SQL Server
        String sql = "SELECT * FROM Khach_Hang_Tam_Thoi WHERE Ngay_Tao = CAST(GETDATE() AS DATE)"; 
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                result.add(new KhachHang(
                    rs.getString("Ten_Khach_Hang"),
                    rs.getInt("Tuoi"),
                    rs.getString("Gioi_Tinh"),
                    rs.getString("Ma_KH"),
                    rs.getBoolean("La_Vong")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách khách hàng: " + e.getMessage(), e);
        }

        if (result.isEmpty()) {
            return layDanhSachKhachHangHomNay(); 
        }
        
        return result;
    }

    public List<KhachHang> getDanhSachKhachHangThat() {
        List<KhachHang> result = new ArrayList<>();
        String sql = "SELECT * FROM Khach_Hang_Goc";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                result.add(new KhachHang(
                    rs.getString("Ten_Khach_Hang"),
                    rs.getInt("Tuoi"),
                    rs.getString("Gioi_Tinh"),
                    rs.getString("Ma_KH"),
                    false // Luôn là khách hàng thật
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách khách hàng thật: " + e.getMessage(), e);
        }
        
        return result;
    }

    public int demSoKhachVong(List<KhachHang> danhSach) {
        int count = 0;
        for (KhachHang kh : danhSach) {
            if (kh.isLaVong()) {
                count++;
            }
        }
        return count;
    }

    public int demSoKhachThuong(List<KhachHang> danhSach) {
        int count = 0;
        for (KhachHang kh : danhSach) {
            if (!kh.isLaVong()) {
                count++;
            }
        }
        return count;
    }
}