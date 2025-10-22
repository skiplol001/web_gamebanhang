package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerData {
    public int money;
    public int mentalPoints;
    public int currentDay;
    public Map<String, Integer> inventory;
    public Set<String> unlockedItems; // Đã bổ sung
    
    public PlayerData() {
        this.money = 1000;
        this.mentalPoints = 100;
        this.currentDay = 1;
        this.inventory = new HashMap<>();
        this.unlockedItems = new HashSet<>(); 
    }
}