package main;

import units.Hero;

import java.io.Serializable;
import java.util.Random;

public class GameMap implements Serializable {
    private final Cell[][] cells;

    // Цвета ANSI
    public static final String RESET = "\u001B[0m";
    public static final String BLUE = "\u001B[44m";
    public static final String RED = "\u001B[41m";
    public static final String YELLOW = "\u001B[43m";
    public static final String GREEN = "\u001B[42m";
    public static final String GRAY = "\u001B[100m";
    public static final String BLACK = "\u001B[30m";


    public GameMap(int width, int height) {
        cells = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public void setCastle(int x, int y, String owner) {
        cells[x][y].setCastle(true);
        cells[x][y].setOwner(owner);
    }

    public void initZonesAndRoad() {
        // Делим на зоны: левая треть - Player1, правая треть - Player2/AI, остальное - Neutral
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (j < cells[i].length / 3) {
                    cells[i][j].setZone("Player1");
                } else if (j >= 2 * cells[i].length / 3) {
                    cells[i][j].setZone("Player2");
                } else {
                    cells[i][j].setZone("Neutral");
                }
            }
        }

        // Строим дорогу по диагонали от замка к замку
        int size = cells.length;
        for (int i = 0; i < size; i++) {
            cells[i][i].setRoad(true);
        }
    }

    public void addHero(Hero hero, int x, int y) {
        cells[x][y].setHero(hero);
    }

    public boolean checkCastleCapture(String owner) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j].isCastle() && cells[i][j].getOwner().equals(owner)) {
                    Hero occupyingHero = cells[i][j].getHero();
                    if (occupyingHero != null && !occupyingHero.getFraction().equals(owner)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void displayMap() {
        System.out.print("   ");
        for (int j = 0; j < cells[0].length; j++) {
            System.out.printf("%2d ", j); // Просто выводим j
        }
        System.out.println();

        for (int i = 0; i < cells.length; i++) {
            System.out.printf("%2d ", i);
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                String bgColor = "";

                switch (cell.getZone()) {
                    case "Player1":
                        bgColor = BLUE;
                        break;
                    case "Player2":
                        case "AI":
                            bgColor = RED;
                            break;
                        case "Neutral":
                            bgColor = YELLOW;
                            break;
                }

                if (cell.isRoad()) {
                    bgColor = GREEN;
                }

                if (cell.isCastle()) {
                    bgColor = GRAY;
                }

                String symbol = ".";
                if (cell.getHero() != null) {
                    symbol = cell.getHero().getName().substring(0, 1);
                } else if (cell.getGoldAmount() > 0) {
                    symbol = "$";  // ← важно! золото приоритетнее дороги и точки
                } else if (cell.isCastle()) {
                    symbol = "C";
                } else if (cell.isRoad()) {
                    symbol = "+";
                } else {
                    symbol = ".";
                }

                if (cell.getHero() != null) {
                    String name = cell.getHero().getName();
                    symbol = name.isEmpty() ? "H" : String.valueOf(name.charAt(0)).toUpperCase();
                }

                System.out.print(bgColor + BLACK + " " + symbol + " " + RESET);
            }
            System.out.println();
        }
    }

    public void moveHero(Hero hero, int newX, int newY) {
        int oldX = hero.getX();
        int oldY = hero.getY();
        cells[oldX][oldY].setHero(null);
        hero.setX(newX);
        hero.setY(newY);
        cells[newX][newY].setHero(hero);
    }

    public void scatterGold(int count) {
        Random rand = new Random();
        int width = cells.length;
        int height = cells[0].length;

        for (int i = 0; i < count; ) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            Cell cell = cells[x][y];

            // не ставим золото на замки и дороги
            if (!cell.isCastle() && !cell.isRoad() && cell.getGoldAmount() == 0) {
                int gold = 50 + rand.nextInt(101); // от 50 до 150
                cell.setGoldAmount(gold);
                i++;
            }
        }
    }

    public boolean isInsideMap(int x, int y) {
        return x >= 0 && y >= 0 && x < cells.length && y < cells[0].length;
    }

    public Cell getCell(int x, int y) {
        if (isInsideMap(x, y)) {
            return cells[x][y];
        }
        return null;
    }

    public Cell[][] getCells() {
        return cells;
    }
}
