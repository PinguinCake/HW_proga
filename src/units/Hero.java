package units;

import main.GameMap;
import main.Cell;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.ArrayList;

public class Hero extends Unit implements Serializable {
    private ArrayList<Unit> army = new ArrayList<>();
    public ArrayList<String> buildings = new ArrayList<>();
    private int x, y;
    private GameMap map;
    private String fraction;
    private int gold = 500;
    private int buildingArmyLimitBonus = 0;
    private int buildingSpeedBonus = 0;
    public boolean hasWatchTower = false;

    public Hero(String name, int health, int attack, int speed, int moveSpeed, int x, int y, GameMap map, String fraction) {
        super(name, health, attack, moveSpeed);
        this.x = x;
        this.y = y;
        this.map = map;
        this.fraction = fraction;
    }

    public void move(int newX, int newY) {
        int actualSpeed = speed + buildingSpeedBonus;

        if (Math.abs(newX - x) + Math.abs(newY - y) <= actualSpeed) {
            map.getCells()[x][y].setHero(null); // убираем героя со старой клетки
            x = newX;
            y = newY;

            Hero target = map.getCells()[x][y].getHero();
            if (target != null && !target.getFraction().equals(this.fraction)) {
                System.out.println("Вы подошли к врагу. Бой начинается!");
                battle.Battle.startBattle(this, target);

                if (target.isDefeated()) {
                    System.out.println(target.getName() + " повержен. Игра окончена!");
                    // System.exit(0);
                }
                return;
            }

            map.getCells()[x][y].setHero(this); // ставим героя на новую клетку
            System.out.println(name + " перемещается в (" + x + ", " + y + ")");
        } else {
            System.out.println("Слишком далеко! (Максимальная дальность: " + actualSpeed + ")");
        }

        int foundGold = map.getCells()[x][y].getGoldAmount();
        if (foundGold > 0) {
            gold += foundGold;
            map.getCells()[x][y].setGoldAmount(0);
            System.out.println(name + " нашёл " + foundGold + " золота!");
        }

    }

    public void attack(Hero enemy) {
        if (this.x == enemy.getX() && this.y == enemy.getY()) {
            battle.Battle.startBattle(this, enemy);
        } else {
            System.out.println("Нет врага на этой клетке.");
        }
    }

    public void buyUnits() {
        int armyLimit = 5 + buildingArmyLimitBonus;
        if (army.size() >= armyLimit) {
            System.out.println("Превышен лимит армии! Постройте Таверну.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите юнита (1 - Копейщик 50, 2 - Арбалетчик 80, 3 - Мечник 100, 4 - Кавалерист 120, 5 - Паладин 150)");
        int choice = scanner.nextInt();
        Unit u = switch (choice) {
            case 1 -> new Spearman();
            case 2 -> new Crossbowman();
            case 3 -> new Swordsman();
            case 4 -> new Cavalry();
            case 5 -> new Paladin();
            default -> null;
        };
        int cost = switch (choice) {
            case 1 -> 50;
            case 2 -> 80;
            case 3 -> 100;
            case 4 -> 120;
            case 5 -> 150;
            default -> 0;
        };
        if (u != null && gold >= cost) {
            army.add(u);
            gold -= cost;
            System.out.println(u.getName() + " нанят!");
        } else {
            System.out.println("Недостаточно золота или неверный выбор.");
        }
    }

    public void buildStructure() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Постройки: 1 - Таверна(200), 2 - Конюшня(150), 3 - Пост(100)");
        int choice = scanner.nextInt();
        String name = "";
        int cost = 0;
        switch (choice) {
            case 1 -> { name = "Таверна"; cost = 200; buildingArmyLimitBonus += 3; }
            case 2 -> { name = "Конюшня"; cost = 150; buildingSpeedBonus += 1; }
            case 3 -> { name = "Сторожевой пост"; cost = 100; hasWatchTower = true; }
        }
        if (gold >= cost) {
            buildings.add(name);
            gold -= cost;
            System.out.println("Построено: " + name);
        } else {
            System.out.println("Недостаточно золота.");
        }
    }

    public void buyRandomUnit() {
        int choice = (int)(Math.random() * 5) + 1;
        Unit u = switch (choice) {
            case 1 -> new Spearman();
            case 2 -> new Crossbowman();
            case 3 -> new Swordsman();
            case 4 -> new Cavalry();
            case 5 -> new Paladin();
            default -> null;
        };
        int cost = switch (choice) {
            case 1 -> 50;
            case 2 -> 80;
            case 3 -> 100;
            case 4 -> 120;
            case 5 -> 150;
            default -> 0;
        };
        if (gold >= cost && u != null) {
            army.add(u);
            gold -= cost;
            System.out.println(name + " (бот) купил: " + u.getName());
        }
    }

    public void buyRandomForAI(Hero hero) {
        try {
            Method method = hero.getClass().getDeclaredMethod("buyRandomUnit");
            method.setAccessible(true);
            method.invoke(hero);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildRandomStructure() {
        int choice = (int)(Math.random() * 3) + 1;
        String name = switch (choice) {
            case 1 -> "Таверна";
            case 2 -> "Конюшня";
            case 3 -> "Сторожевой пост";
            default -> "";
        };
        int cost = switch (choice) {
            case 1 -> 200;
            case 2 -> 150;
            case 3 -> 100;
            default -> 0;
        };
        if (gold >= cost) {
            buildings.add(name);
            gold -= cost;
            System.out.println(name + " построена ботом");
        }
    }

    public void printStats() {
        System.out.println("\n📊 Статистика: " + name);
        System.out.println("Фракция: " + fraction);
        System.out.println("HP: " + health);
        System.out.println("Золото: " + gold);
        System.out.println("Постройки: " + buildings);
        System.out.println("Армия:");
        for (Unit u : army) {
            System.out.println("- " + u.getName() + " (HP: " + u.getHealth() + ")");
        }
    }

    // Геттеры и победа
    public int getX() { return x; }
    public int getY() { return y; }
    public int getGold() { return gold; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public String getFraction() { return fraction; }
    public boolean isDefeated() { return health <= 0 || army.isEmpty(); }
    public ArrayList<Unit> getArmy() { return army; }
    public void setArmy(ArrayList<Unit> newArmy) { this.army = newArmy; }

    public boolean hasWatchTower() {
        return buildings.contains("Сторожевой пост");
    }

    public void setGold(int i) { gold = i; }

    public ArrayList<String> getBuildings() { return buildings; }

    public void setBuildingArmyLimitBonus(int i) { buildingArmyLimitBonus = i; }

    public void setBuildingSpeedBonus(int i) { buildingSpeedBonus = i; }

    public int getBuildingArmyLimitBonus() { return buildingArmyLimitBonus; }
}
