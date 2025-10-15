package model;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class Player {
    private String playerId;
    private int money;
    private int mentalPoints;
    private Map<String, Integer> inventory;
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
        this.inventory = data.inventory != null ? data.inventory : new HashMap<>();
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
        saveToDatabase();
    }

    public int getMentalPoints() {
        return mentalPoints;
    }

    public void setMentalPoints(int mentalPoints) {
        this.mentalPoints = mentalPoints;
        saveToDatabase();
    }

    public Map<String, Integer> getInventory() {
        return new HashMap<>(inventory);
    }

    public void addItem(String itemName, int quantity) {
        inventory.put(itemName, inventory.getOrDefault(itemName, 0) + quantity);
        saveToDatabase();
    }

    public void addItemToInventory(String itemName, int quantity) {
        addItem(itemName, quantity);
    }

    public void removeItemFromInventory(String itemName) {
        inventory.remove(itemName);
        saveToDatabase();
    }

    public boolean hasItem(String itemName) {
        return inventory.containsKey(itemName);
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

    public void saveToDatabase() {
        PlayerData data = new PlayerData();
        data.money = this.money;
        data.mentalPoints = this.mentalPoints;
        data.inventory = this.inventory;
        DatabaseStorage.savePlayerData(playerId, data, dataSource);
    }
}