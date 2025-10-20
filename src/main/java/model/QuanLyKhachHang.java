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
            
            // Chuyển từ Text Block sang chuỗi nối chuỗi (String Concatenation)
            String createKhachHangThatTable = 
                "CREATE TABLE IF NOT EXISTS khach_hang_that (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    ten VARCHAR(100) NOT NULL," +
                "    tuoi INT NOT NULL," +
                "    gioi_tinh VARCHAR(10) NOT NULL," +
                "    ma_kh VARCHAR(20) NOT NULL UNIQUE" +
                ")";
            
            // Chuyển từ Text Block sang chuỗi nối chuỗi (String Concatenation)
            String createKhachHangTable = 
                "CREATE TABLE IF NOT EXISTS khach_hang (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    ten VARCHAR(100) NOT NULL," +
                "    tuoi INT NOT NULL," +
                "    gioi_tinh VARCHAR(10) NOT NULL," +
                "    ma_kh VARCHAR(20) NOT NULL," +
                "    la_vong BOOLEAN NOT NULL," +
                "    ngay_tao DATE DEFAULT CURDATE()" +
                ")";
            
            stmt.execute(createKhachHangThatTable);
            stmt.execute(createKhachHangTable);
            
            if (isKhachHangThatEmpty()) {
                insertDefaultKhachHangThat();
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khởi tạo database: " + e.getMessage(), e);
        }
    }

    private boolean isKhachHangThatEmpty() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM khach_hang_that")) {
            
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            // Nếu có lỗi SQL (ví dụ: bảng chưa tồn tại), coi như trống để chèn dữ liệu mặc định
            return true; 
        }
    }

    private void insertDefaultKhachHangThat() {
        // Giả định class KhachHang có constructor phù hợp
        List<KhachHang> defaultCustomers = Arrays.asList(
            new KhachHang("Liễu Như Yên", 25, "Nữ", "KH001", false),
            new KhachHang("Tạ Minh Kha", 30, "Nam", "KH002", false),
            new KhachHang("Tiểu Lạc", 18, "Nữ", "KH003", false),
            new KhachHang("Shyn Mụi Mụi", 20, "Nữ", "KH004", false),
            new KhachHang("Tăng Quốc Cường", 28, "Nam", "KH005", false)
        );

        String sql = "INSERT INTO khach_hang_that (ten, tuoi, gioi_tinh, ma_kh) VALUES (?, ?, ?, ?)";
        
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
             ResultSet rs = stmt.executeQuery("SELECT * FROM khach_hang_that")) {
            
            while (rs.next()) {
                // Tỷ lệ xuất hiện "Khách Hàng Vòng" là 30%
                boolean laVong = random.nextDouble() < 0.3;

                if (laVong) {
                    // Tạo thông tin ngẫu nhiên cho Khách Vòng
                    int tuoiVong = 100 + random.nextInt(100);
                    String maVong = "V" + (1000 + random.nextInt(9000));
                    
                    result.add(new KhachHang(
                        rs.getString("ten"), // Vẫn lấy tên của khách hàng thật làm tên giả
                        tuoiVong,
                        rs.getString("gioi_tinh"),
                        maVong,
                        true
                    ));
                } else {
                    // Khách hàng thật
                    result.add(new KhachHang(
                        rs.getString("ten"),
                        rs.getInt("tuoi"),
                        rs.getString("gioi_tinh"),
                        rs.getString("ma_kh"),
                        false
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách khách hàng: " + e.getMessage(), e);
        }

        luuDanhSachKhachHang(result);
        return result;
    }

    private void luuDanhSachKhachHang(List<KhachHang> danhSach) {
        String sql = "INSERT INTO khach_hang (ten, tuoi, gioi_tinh, ma_kh, la_vong) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (KhachHang kh : danhSach) {
                pstmt.setString(1, kh.getTen());
                pstmt.setInt(2, kh.getTuoi());
                pstmt.setString(3, kh.getGioiTinh());
                pstmt.setString(4, kh.getMaKH());
                pstmt.setBoolean(5, kh.isLaVong());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu danh sách khách hàng: " + e.getMessage(), e);
        }
    }

    public List<KhachHang> taiDanhSachKhachHang() {
        List<KhachHang> result = new ArrayList<>();
        // Sử dụng CURDATE() để lấy danh sách khách hàng của ngày hiện tại
        String sql = "SELECT * FROM khach_hang WHERE ngay_tao = CURDATE()"; 
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                result.add(new KhachHang(
                    rs.getString("ten"),
                    rs.getInt("tuoi"),
                    rs.getString("gioi_tinh"),
                    rs.getString("ma_kh"),
                    rs.getBoolean("la_vong")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách khách hàng: " + e.getMessage(), e);
        }

        if (result.isEmpty()) {
            // Nếu chưa có danh sách khách hàng được tạo và lưu trong ngày hôm nay, 
            // thì tạo mới và lưu lại
            return layDanhSachKhachHangHomNay(); 
        }
        
        return result;
    }

    public List<KhachHang> getDanhSachKhachHangThat() {
        List<KhachHang> result = new ArrayList<>();
        String sql = "SELECT * FROM khach_hang_that";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                result.add(new KhachHang(
                    rs.getString("ten"),
                    rs.getInt("tuoi"),
                    rs.getString("gioi_tinh"),
                    rs.getString("ma_kh"),
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