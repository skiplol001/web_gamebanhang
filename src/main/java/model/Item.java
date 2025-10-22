package model;

public class Item {
    private String name;
    private int price; // Giá bán
    private int unlockPrice; // Giá mở khóa
    private int baseCost; // Giá nhập/chi phí gốc
    private String description;
    private String type; // Ví dụ: 'consume', 'key-item', 'goods'

    public Item(String name, int price, int unlockPrice, int baseCost, String description, String type) {
        this.name = name;
        this.price = price;
        this.unlockPrice = unlockPrice;
        this.baseCost = baseCost;
        this.description = description;
        this.type = type;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getUnlockPrice() { return unlockPrice; }
    public int getBaseCost() { return baseCost; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    
    // Alias cho JSP
    public String getTenSP() { return name; }
    public int getGiaBan() { return price; }
    public int getGiaMoKhoa() { return unlockPrice; }
    public int getGiaNhap() { return baseCost; }
    public String getMoTa() { return description; }
    public String getLoai() { return type; }
    // Giả định thêm thuộc tính cho mặc cả (có thể được tính toán ở Service)
    public int getMaxDiscountPercent() { return 25; } 
    public int getTonKho() { return 0; } // Sẽ được Service gán giá trị từ Kho_Do
}