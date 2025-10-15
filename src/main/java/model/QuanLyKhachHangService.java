package model;

import java.util.*;
import javax.sql.DataSource;

public class QuanLyKhachHangService {
    private KhachHang khachHangHienTai;
    private final QuanLyKhachHang quanLyKH;
    private final Random random;
    private List<String> danhSachVatPham;
    private Map<String, Integer> giaVatPham;

    public QuanLyKhachHangService(DataSource dataSource) {
        this.quanLyKH = new QuanLyKhachHang(dataSource);
        this.random = new Random();
        this.danhSachVatPham = new ArrayList<>();
        this.giaVatPham = new HashMap<>();
        taiDanhSachVatPhamTuDatabase(dataSource);
    }

    private void taiDanhSachVatPhamTuDatabase(DataSource dataSource) {
        String sql = "SELECT ten_vat_pham, gia FROM vat_pham";
        
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String tenVatPham = rs.getString("ten_vat_pham");
                int gia = rs.getInt("gia");
                danhSachVatPham.add(tenVatPham);
                giaVatPham.put(tenVatPham, gia);
            }
            
            if (danhSachVatPham.isEmpty()) {
                initVatPhamMacDinh(dataSource);
            }
            
        } catch (Exception e) {
            initVatPhamMacDinh(dataSource);
        }
    }

    private void initVatPhamMacDinh(DataSource dataSource) {
        Map<String, Integer> defaultItems = Map.of(
            "Snack", 20,
            "Thuốc", 100,
            "Nước suối", 30,
            "Bánh mì", 50
        );

        String sql = "INSERT INTO vat_pham (ten_vat_pham, gia) VALUES (?, ?)";
        
        try (var conn = dataSource.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            
            for (var entry : defaultItems.entrySet()) {
                pstmt.setString(1, entry.getKey());
                pstmt.setInt(2, entry.getValue());
                pstmt.addBatch();
                
                danhSachVatPham.add(entry.getKey());
                giaVatPham.put(entry.getKey(), entry.getValue());
            }
            pstmt.executeBatch();
        } catch (Exception e) {
            danhSachVatPham = new ArrayList<>(defaultItems.keySet());
            giaVatPham = new HashMap<>(defaultItems);
        }
    }

    private HashMap<String, Integer> generateRandomYeuCauMap() {
        HashMap<String, Integer> requiredItems = new HashMap<>();

        if (danhSachVatPham.isEmpty()) {
            return requiredItems;
        }

        List<String> vatPhamList = new ArrayList<>(danhSachVatPham);
        Collections.shuffle(vatPhamList);

        int soLuongVatPham = random.nextInt(Math.min(4, vatPhamList.size())) + 1;

        for (int i = 0; i < soLuongVatPham; i++) {
            String vatPham = vatPhamList.get(i);
            requiredItems.put(vatPham, 1);
        }

        return requiredItems;
    }

    public KhachHang taoKhachHangMoi() {
        List<KhachHang> danhSach = quanLyKH.layDanhSachKhachHangHomNay();
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
        HashMap<String, Integer> vatPham = khachHangHienTai.getVatPhamYeuCau();
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
        HashMap<String, Integer> requiredItems = khachHangHienTai.getVatPhamYeuCau();

        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentQuantity = inventory.getOrDefault(itemName, 0);

            if (currentQuantity < requiredQuantity) {
                return false;
            }
        }

        int totalMoney = 0;
        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            String itemName = entry.getKey();
            int requiredQuantity = entry.getValue();
            int currentQuantity = inventory.get(itemName);
            int itemPrice = giaVatPham.getOrDefault(itemName, 50);

            totalMoney += itemPrice * requiredQuantity;

            if (currentQuantity == requiredQuantity) {
                inventory.remove(itemName);
            } else {
                inventory.put(itemName, currentQuantity - requiredQuantity);
            }
        }

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
            playerData.mentalPoints = Math.max(0, playerData.mentalPoints - 5);
        }
    }

    public String kiemTraDuVatPham(Map<String, Integer> inventory) {
        if (khachHangHienTai == null) {
            return "Chưa có khách hàng";
        }

        StringBuilder missingItems = new StringBuilder();
        HashMap<String, Integer> requiredItems = khachHangHienTai.getVatPhamYeuCau();

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

        HashMap<String, Integer> requiredItems = khachHangHienTai.getVatPhamYeuCau();

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

    public List<String> getDanhSachVatPham() {
        return new ArrayList<>(danhSachVatPham);
    }

    public Map<String, Integer> getGiaVatPhamMap() {
        return new HashMap<>(giaVatPham);
    }
}