package model;

import java.util.HashMap;
import java.util.Map;

public class KhachHang {
    private String ten;
    private int tuoi;
    private String gioiTinh;
    private String maKH;
    private boolean laVong;
    private Map<String, Integer> vatPhamYeuCau;

    // Constructor rỗng (nên có, nếu bạn dùng Setters)
    public KhachHang() {
        this.vatPhamYeuCau = new HashMap<>();
    }
    
    // Constructor đầy đủ (đang được DAO sử dụng)
    public KhachHang(String ten, int tuoi, String gioiTinh, String maKH, boolean laVong) {
        this.ten = ten;
        this.tuoi = tuoi;
        this.gioiTinh = gioiTinh;
        this.maKH = maKH;
        this.laVong = laVong;
        this.vatPhamYeuCau = new HashMap<>();
    }

    // --- GETTERS (Cần thiết cho EL/JSTL) ---
    public String getTen() { return ten; }
    public int getTuoi() { return tuoi; }
    public String getGioiTinh() { return gioiTinh; }
    public String getMaKH() { return maKH; }
    public boolean isLaVong() { return laVong; } 
    public Map<String, Integer> getVatPhamYeuCau() { return vatPhamYeuCau; }

    // --- SETTERS (Để DAO có thể tạo Object rỗng rồi gán giá trị)
    public void setTen(String ten) { this.ten = ten; }
    public void setTuoi(int tuoi) { this.tuoi = tuoi; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public void setLaVong(boolean laVong) { this.laVong = laVong; }

    public void setVatPhamYeuCau(Map<String, Integer> vatPhamYeuCau) { 
        this.vatPhamYeuCau = vatPhamYeuCau; 
    }

    public void themVatPhamYeuCau(String tenVatPham, int soLuong) { 
        this.vatPhamYeuCau.put(tenVatPham, soLuong); 
    }

    @Override
    public String toString() {
        return "Tên: " + ten + ", Tuổi: " + tuoi + ", Giới tính: " + gioiTinh
                             + ", Mã KH: " + maKH + ", Loại: " + (laVong ? "Vong" : "Người thường");
    }

    public String layThongTin() {
        return "Tên: " + ten
                             + "\nTuổi: " + tuoi
                             + "\nGiới tính: " + gioiTinh
                             + "\nMã khách hàng: " + maKH;
    }
}