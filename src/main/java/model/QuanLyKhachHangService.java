package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import javax.sql.DataSource;

public class QuanLyKhachHangService {
    private KhachHang khachHangHienTai;
    private final QuanLyKhachHang quanLyKH;
    private final Random random;
    private List<String> danhSachVatPham;
    private Map<String, Integer> giaVatPham; // Tên SP -> Giá Bán
    private Map<String, Item> itemData; // Tên SP -> Item object

    public QuanLyKhachHangService(DataSource dataSource) {
        this.quanLyKH = new QuanLyKhachHang(dataSource);
        this.random = new Random();
        this.danhSachVatPham = new ArrayList<>();
        this.giaVatPham = new HashMap<>();
        this.itemData = new HashMap<>();
        initVatPhamMacDinh(dataSource); // Gọi init trước
        taiDanhSachVatPhamTuDatabase(dataSource); // Sau đó load từ DB
    }
    
    // Khởi tạo bảng Vat_Pham và chèn dữ liệu mặc định nếu cần
    private void initVatPhamMacDinh(DataSource dataSource) {
        // Cần tạo bảng Vat_Pham trước nếu chưa có
        String createTableSql = 
            "CREATE TABLE IF NOT EXISTS Vat_Pham (" +
            "   Ma_SP INT AUTO_INCREMENT PRIMARY KEY," + // Dùng AUTO_INCREMENT
            "   Ten_SP NVARCHAR(100) UNIQUE NOT NULL," +
            "   Gia_Ban INT," +
            "   Gia_Mo_Khoa INT DEFAULT 0," +
            "   Gia_Nhap INT DEFAULT 0," +
            "   Mo_Ta NVARCHAR(255)," +
            "   Loai VARCHAR(50) DEFAULT 'goods'" +
            ")";
        
        // Dữ liệu mặc định
        List<Item> defaultItems = Arrays.asList(
            new Item("Bánh mì", 50, 0, 30, "Món ăn nhẹ lót dạ.", "goods"),
            new Item("Nước suối", 30, 0, 15, "Giải khát đơn giản.", "goods"),
            new Item("Thuốc", 100, 0, 60, "Có thể giúp tăng cường tinh thần.", "consume"),
            new Item("Snack", 20, 0, 10, "Đồ ăn vặt yêu thích.", "goods"),
            new Item("Cà phê", 80, 500, 40, "Giúp tỉnh táo, cần mở khóa.", "goods"),
            new Item("Bánh ngọt", 60, 0, 35, "Thức ăn nhanh có đường.", "goods")
        );

        String insertSql = "INSERT IGNORE INTO Vat_Pham (Ten_SP, Gia_Ban, Gia_Mo_Khoa, Gia_Nhap, Mo_Ta, Loai) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            // 1. Đảm bảo bảng tồn tại
            stmt.execute(createTableSql); 

            // 2. Chèn dữ liệu (sử dụng INSERT IGNORE hoặc ON DUPLICATE KEY UPDATE)
            for (Item item : defaultItems) {
                pstmt.setString(1, item.getName());
                pstmt.setInt(2, item.getPrice());
                pstmt.setInt(3, item.getUnlockPrice());
                pstmt.setInt(4, item.getBaseCost());
                pstmt.setString(5, item.getDescription());
                pstmt.setString(6, item.getType());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (Exception e) {
            // Trường hợp lỗi (ví dụ: DB không chạy), vẫn giữ dữ liệu trong RAM để game chạy tạm thời
            for (Item item : defaultItems) {
                 danhSachVatPham.add(item.getName());
                 giaVatPham.put(item.getName(), item.getPrice());
                 itemData.put(item.getName(), item);
            }
        }
    }


    private void taiDanhSachVatPhamTuDatabase(DataSource dataSource) {
        // Tên cột đã sửa: Ten_SP, Gia_Ban, Gia_Mo_Khoa, Gia_Nhap, Mo_Ta, Loai
        String sql = "SELECT Ten_SP, Gia_Ban, Gia_Mo_Khoa, Gia_Nhap, Mo_Ta, Loai FROM Vat_Pham";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql); 
             ResultSet rs = pstmt.executeQuery()) { 
            
            danhSachVatPham.clear();
            giaVatPham.clear();
            itemData.clear();
            
            while (rs.next()) {
                String tenVatPham = rs.getString("Ten_SP");
                int giaBan = rs.getInt("Gia_Ban");
                
                Item item = new Item(
                    tenVatPham, 
                    giaBan, 
                    rs.getInt("Gia_Mo_Khoa"), 
                    rs.getInt("Gia_Nhap"), 
                    rs.getString("Mo_Ta"),
                    rs.getString("Loai")
                );
                
                danhSachVatPham.add(tenVatPham);
                giaVatPham.put(tenVatPham, giaBan);
                itemData.put(tenVatPham, item);
            }
            
        } catch (Exception e) {
            // Lỗi khi load, có thể do kết nối, không cần làm gì thêm vì đã có initDefault
        }
    }

    private Map<String, Integer> generateRandomYeuCauMap() {
        Map<String, Integer> requiredItems = new HashMap<>();

        // Chỉ tạo yêu cầu từ các vật phẩm đã được load (tức là có trong DB)
        if (danhSachVatPham.isEmpty()) {
            return requiredItems;
        }

        List<String> vatPhamList = new ArrayList<>(danhSachVatPham);
        Collections.shuffle(vatPhamList);

        // Khách hàng yêu cầu 1 đến 4 vật phẩm
        int soLuongVatPham = random.nextInt(Math.min(4, vatPhamList.size())) + 1;

        for (int i = 0; i < soLuongVatPham; i++) {
            String vatPham = vatPhamList.get(i);
            requiredItems.put(vatPham, 1);
        }

        return requiredItems;
    }

    public KhachHang taoKhachHangMoi() {
        List<KhachHang> danhSach = quanLyKH.taiDanhSachKhachHang();
        if (danhSach != null && !danhSach.isEmpty()) {
            KhachHang khachMoi = danhSach.get(random.nextInt(danhSach.size()));
            khachMoi.setVatPhamYeuCau(generateRandomYeuCauMap());
            this.khachHangHienTai = khachMoi;
            return khachMoi;
        }
        return null;
    }

    public String layYeuCauKhachHangHienTai() {
        if (khachHangHienTai == null) {
            return "Chưa có yêu cầu";
        }
        StringBuilder yeuCau = new StringBuilder("Tôi muốn mua: ");
        Map<String, Integer> vatPham = khachHangHienTai.getVatPhamYeuCau();
        int count = 0;
        for (Map.Entry<String, Integer> entry : vatPham.entrySet()) {
            yeuCau.append(entry.getKey());
            if (count < vatPham.size() - 1) {
                yeuCau.append(", ");
            }
            count++;
        }
        return yeuCau.toString();
    }

    public void setKhachHangHienTai(KhachHang khachHang) {
        this.khachHangHienTai = khachHang;
    }

    public KhachHang getKhachHangHienTai() {
        return this.khachHangHienTai;
    }

    public boolean xuLyBanHang(boolean quyetDinhBan, Map<String, Integer> inventory, PlayerData playerData) {
        if (khachHangHienTai == null) {
            return false;
        }

        if (quyetDinhBan) {
            if (!handleSuccessfulSale(inventory, playerData)) {
                return false;
            }
        } else {
            handleRejectedSale(playerData);
        }

        this.khachHangHienTai = null;
        return true;
    }

    private boolean handleSuccessfulSale(Map<String, Integer> inventory, PlayerData playerData) {
        Map<String, Integer> requiredItems = khachHangHienTai.getVatPhamYeuCau();

        // 1. Kiểm tra đủ vật phẩm
        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentQuantity = inventory.getOrDefault(itemName, 0);

            if (currentQuantity < requiredQuantity) {
                return false;
            }
        }

        // 2. Xử lý giao dịch
        int totalMoney = 0;
        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentQuantity = inventory.get(itemName);
            int itemPrice = giaVatPham.getOrDefault(itemName, 50);

            totalMoney += itemPrice * requiredQuantity;

            // Cập nhật tồn kho (inventory)
            if (currentQuantity == requiredQuantity) {
                inventory.remove(itemName);
            } else {
                inventory.put(itemName, currentQuantity - requiredQuantity);
            }
        }

        // 3. Xử lý logic Vong/Thật
        if (khachHangHienTai.isLaVong()) {
            playerData.mentalPoints = Math.max(0, playerData.mentalPoints - 10);
        } else {
            playerData.money += totalMoney;
            playerData.mentalPoints = Math.min(100, playerData.mentalPoints + 5);
        }

        return true;
    }

    private void handleRejectedSale(PlayerData playerData) {
        if (!khachHangHienTai.isLaVong()) {
            // Phạt nếu từ chối khách hàng thật
            playerData.mentalPoints = Math.max(0, playerData.mentalPoints - 5);
        }
        // Từ chối khách Vong không bị phạt
    }

    public String kiemTraDuVatPham(Map<String, Integer> inventory) {
        if (khachHangHienTai == null) {
            return "Chưa có khách hàng";
        }

        StringBuilder missingItems = new StringBuilder();
        Map<String, Integer> requiredItems = khachHangHienTai.getVatPhamYeuCau();

        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentQuantity = inventory.getOrDefault(itemName, 0);

            if (currentQuantity < requiredQuantity) {
                int missing = requiredQuantity - currentQuantity;
                missingItems.append("Thiếu ").append(missing).append(" ").append(itemName).append("\n");
            }
        }

        if (missingItems.length() == 0) {
            return "Bạn có đủ vật phẩm! Có thể bán.";
        } else {
            return "Vật phẩm thiếu:\n" + missingItems.toString();
        }
    }

    public boolean kiemTraCoTheBan(Map<String, Integer> inventory) {
        if (khachHangHienTai == null) {
            return false;
        }

        Map<String, Integer> requiredItems = khachHangHienTai.getVatPhamYeuCau();

        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentQuantity = inventory.getOrDefault(itemName, 0);

            if (currentQuantity < requiredQuantity) {
                return false;
            }
        }
        return true;
    }

    public int getGiaVatPham(String tenVatPham) {
        return giaVatPham.getOrDefault(tenVatPham, 50);
    }
    
    // Bổ sung: Lấy giá nhập (Base Cost)
    public int getGiaNhap(String tenVatPham) {
        Item item = itemData.get(tenVatPham);
        return item != null ? item.getBaseCost() : 50;
    }

    // Bổ sung: Lấy Item object
    public Item getItem(String tenVatPham) {
        return itemData.get(tenVatPham);
    }

    public List<String> getDanhSachVatPham() {
        return new ArrayList<>(danhSachVatPham);
    }

    public Map<String, Integer> getGiaVatPhamMap() {
        return new HashMap<>(giaVatPham);
    }
    public Map<String, Item> getItemDataMap() {
    return new HashMap<>(itemData); // Trả về bản sao để bảo vệ dữ liệu gốc
}
}