package model;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet; // Cần import

public class Player {
    private String playerId;
    private int money;
    private int mentalPoints;
    private int currentDay; 
    private Map<String, Integer> inventory;
    private Set<String> unlockedItems; 
    private DataSource dataSource;

    public Player(String playerId, DataSource dataSource) {
        this.playerId = playerId;
        this.dataSource = dataSource;
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        PlayerData data = DatabaseStorage.loadPlayerData(playerId, dataSource);
        this.money = data.money;
        this.mentalPoints = data.mentalPoints;
        this.currentDay = data.currentDay; 
        this.inventory = data.inventory != null ? data.inventory : new HashMap<>();
        this.unlockedItems = data.unlockedItems != null ? data.unlockedItems : new HashSet<>(); 
    }

    public String getPlayerId() { return playerId; } 
    public String getTenNguoiChoi() { return "Player " + playerId; } 
    public int getMoney() { return money; }
    public void setMoney(int money) {
        this.money = money;
        saveToDatabase();
    }

    public int getMentalPoints() { return mentalPoints; }
    public void setMentalPoints(int mentalPoints) {
        this.mentalPoints = mentalPoints;
        saveToDatabase();
    }
    
    public int getCurrentDay() { return currentDay; } 
    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
        saveToDatabase();
    }

    public int getDiemTinhThan() { return mentalPoints; } 
    public int getTien() { return money; } 

    // Quản lý Kho đồ
    public Map<String, Integer> getInventory() {
        return new HashMap<>(inventory);
    }

    public void addItem(String itemName, int quantity) {
        inventory.put(itemName, inventory.getOrDefault(itemName, 0) + quantity);
        saveToDatabase();
    }
    
    public void removeItem(String itemName, int quantity) {
        int current = inventory.getOrDefault(itemName, 0);
        if (current <= quantity) {
            inventory.remove(itemName);
        } else {
            inventory.put(itemName, current - quantity);
        }
        saveToDatabase();
    }

    // Quản lý Vật phẩm đã mở khóa
    public Set<String> getUnlockedItems() {
        return new HashSet<>(unlockedItems);
    }
    
    public void unlockItem(String itemName) {
        this.unlockedItems.add(itemName);
        saveToDatabase();
    }
    
    public boolean isItemUnlocked(String itemName) {
        return this.unlockedItems.contains(itemName);
    }

    // Lưu dữ liệu vào CSDL
    public void saveToDatabase() {
        PlayerData data = new PlayerData();
        data.money = this.money;
        data.mentalPoints = this.mentalPoints;
        data.currentDay = this.currentDay;
        data.inventory = this.inventory;
        data.unlockedItems = this.unlockedItems;
        DatabaseStorage.savePlayerData(playerId, data, dataSource);
    }
    public PlayerData toPlayerData() {
    PlayerData data = new PlayerData();
    data.money = this.money;
    data.mentalPoints = this.mentalPoints;
    data.currentDay = this.currentDay;
    data.inventory = this.inventory;
    data.unlockedItems = this.unlockedItems;
    return data;
}   
}