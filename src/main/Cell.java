package main;

import units.Hero;

import java.io.Serializable;

public class Cell implements Serializable {
    private boolean isCastle = false;
    private boolean isRoad = false;
    private int goldAmount = 0;
    private String zone = "Neutral"; // Может быть "Player", "Neutral", "AI"
    private String owner = ""; // Владелец замка ("Player" или "AI")
    private Hero hero; // Герой, стоящий на клетке (если есть)

    // --- Геттеры и сеттеры ---

    public boolean isCastle() {
        return isCastle;
    }

    public void setCastle(boolean castle) {
        isCastle = castle;
    }

    public boolean isRoad() {
        return isRoad;
    }

    public void setRoad(boolean road) {
        isRoad = road;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public int getGoldAmount() {
        return goldAmount;
    }

    public void setGoldAmount(int amount) {
        this.goldAmount = amount;
    }

//public String toString() {
//        if (content != null && !content.isEmpty()) {
//            switch (content) {
//                case "А": // Герой игрока
//                    return "\u001B[32m" + content + "\u001B[0m"; // Зеленый
//                case "a": // Герой компьютера
//                    return "\u001B[31m" + content + "\u001B[0m"; // Красный
//                case "G": // Золото
//                    return "\u001B[33m" + content + "\u001B[0m"; // Желтый
//                case "P_C": // Замок игрока
//                    return "\u001B[34m" + content + "\u001B[0m"; // Синий
//                case "C_C": // Замок компьютера
//                    return "\u001B[35m" + content + "\u001B[0m"; // Пурпурный
//                default:
//                    return content; // Без цвета
//            }
//        }
//        // Типы местности
//        switch (terrainType) {
//            case "Road":
//                return "\u001B[90m" + "+" + "\u001B[0m"; // Серая дорога
//            case "Forest":
//                return "\u001B[32m" + "*" + "\u001B[0m"; // Зеленый лес
//            case "Plain":
//                return "\u001B[36m" + "-" + "\u001B[0m"; // Голубая равнина
//            default:
//                return terrainType.substring(0, 1); // Без цвета
//        }
//    }
}
