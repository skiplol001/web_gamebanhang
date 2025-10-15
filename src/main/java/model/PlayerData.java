package model;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    public int money;
    public int mentalPoints;
    public int currentDay;
    public Map<String, Integer> inventory;
    
    public PlayerData() {
        this.money = 1000;
        this.mentalPoints = 100;
        this.currentDay = 1;
        this.inventory = new HashMap<>();
    }
}