package main;

import battle.Battle;
import bot.Bot;
import buildings.Building;
import buildings.Stable;
import buildings.Tavern;
import buildings.WatchTower;
import mapeditor.MapEditor;
import mapeditor.MapJsonImporter;
import save.GameSaveManager;
import score.ScoreManager;
import units.Hero;
import units.Spearman;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Главное меню ===");
        System.out.println("1 - Новая игра");
        System.out.println("2 - Загрузить игру");
        System.out.println("3 - Редактор карт");
        System.out.println("4 - Показать рекорды");
        System.out.print("Выбор: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> {
                System.out.println("Режим игры (1 - PvP, 2 - PvE): ");
                int mode = Integer.parseInt(scanner.nextLine());

                GameMap gameMap;
                System.out.print("Загрузить карту из JSON? (y/n): ");
                if (scanner.nextLine().equalsIgnoreCase("y")) {
                    System.out.print("Имя JSON-файла (например, map1.json): ");
                    String jsonFile = scanner.nextLine();
                    gameMap = MapJsonImporter.importFromJson(jsonFile, 12, 12);
                } else {
                    gameMap = new GameMap(12, 12);
                    gameMap.initZonesAndRoad();
                    gameMap.scatterGold(10);
                }

                // Герои
                System.out.print("Введите имя героя игрока 1: ");
                String name1 = scanner.nextLine();
                Hero hero1 = new Hero(name1, 200, 30, 2, 4, 0, 0, gameMap, "Player1");
                gameMap.addHero(hero1, 0, 0);

                Hero hero2;
                if (mode == 1) {
                    System.out.print("Введите имя героя игрока 2: ");
                    String name2 = scanner.nextLine();
                    hero2 = new Hero(name2, 200, 30, 2, 4, 11, 11, gameMap, "Player2");
                } else {
                    hero2 = new Hero("AI", 200, 30, 2, 4, 11, 11, gameMap, "AI");
                }
                gameMap.addHero(hero2, 11, 11);

                List<Hero> playerHeroes = new ArrayList<>();
                playerHeroes.add(hero1);

                List<Hero> enemyHeroes = new ArrayList<>();
                enemyHeroes.add(hero2);

                // Игра начинается
                boolean gameRunning = true;
                while (gameRunning) {
                    gameMap.displayMap();

                    // Переключение активного героя
                    System.out.println("\nВыберите активного героя:");
                    for (int i = 0; i < playerHeroes.size(); i++) {
                        System.out.println((i + 1) + ". " + playerHeroes.get(i).getName());
                    }
                    int heroChoice = Integer.parseInt(scanner.nextLine()) - 1;
                    Hero activeHero = playerHeroes.get(heroChoice);

                    System.out.println("Ход героя " + activeHero.getName() + ". Введите команду:");
                    String[] input = scanner.nextLine().split(" ");
                    switch (input[0]) {
                        case "move" -> activeHero.move(Integer.parseInt(input[1]), Integer.parseInt(input[2]));
                        case "buy" -> activeHero.buyRandomUnit();
                        case "buyMany" -> activeHero.buyMultipleUnits(Integer.parseInt(input[1]));
                        case "build" -> {
                            Building b = switch (input[1]) {
                                case "tavern" -> new Tavern();
                                case "stable" -> new Stable();
                                case "tower" -> new WatchTower();
                                default -> null;
                            };
                            if (b != null) activeHero.addBuilding(b);
                        }
                        case "hire" -> {
                            if (activeHero.canHireNewHeroes()) {
                                Hero newHero = new Hero(
                                        activeHero.getName() + "_extra",
                                        180, 25, 2, 3,
                                        activeHero.getX(), activeHero.getY(),
                                        gameMap, activeHero.getOwner()
                                );
                                gameMap.addHero(newHero, newHero.getX(), newHero.getY());
                                playerHeroes.add(newHero);
                                System.out.println("Нанят новый герой: " + newHero.getName());
                            } else {
                                System.out.println("Таверна не построена.");
                            }
                        }
                        case "exit" -> gameRunning = false;
                    }

                    // Бот или второй игрок
                    if (mode == 2) {
                        Hero bot = enemyHeroes.get(0);
                        Bot.performBotAction(bot, gameMap);
                    } else {
                        System.out.println("Ход второго игрока (" + hero2.getName() + ")...");
                        // Можно реализовать второй ввод или автопереход
                    }

                    // Победа
                    if (hero2.isDefeated()) {
                        System.out.println("🎉 Победа игрока 1!");
                        ScoreManager.recordScore(hero1.getName(), gameMap.getMapNameOrDefault(), hero1.getGold());
                        gameRunning = false;
                    } else if (hero1.isDefeated()) {
                        System.out.println("💀 Победил " + hero2.getName());
                        ScoreManager.recordScore(hero2.getName(), gameMap.getMapNameOrDefault(), hero2.getGold());
                        gameRunning = false;
                    }
                }
            }

            case "2" -> {
                System.out.print("Введите имя сохранения: ");
                String filename = scanner.nextLine();
                Object[] loaded = GameSaveManager.loadGame(filename + ".sav");
                if (loaded != null) {
                    hero1 = (Hero) loaded[0];
                    hero2 = (Hero) loaded[1];
                    gameMap = (GameMap) loaded[2];
                    System.out.println("Игра загружена.");
                } else {
                    System.out.println("Не удалось загрузить.");
                    return;
                }
            }
            case "3" -> {
                gameMap = MapEditor.createMapFromUserInput(12, 12);
                System.out.println("Карта создана. Перезапустите игру для начала.");
                return;
            }
            case "4" -> {
                List<String> top = ScoreManager.getTop5Scores();
                System.out.println("Топ-5 игроков:");
                for (String s : top) {
                    String[] parts = s.split(",");
                    System.out.println("Имя: " + parts[0] + ", Карта: " + parts[1] + ", Очки: " + parts[2]);
                }
                return;
            }
        }

        System.out.println("Игра завершена.");
    }

    private static boolean isPrimaryAction(String cmd) {
        return cmd.equals("move") || cmd.equals("buy") || cmd.equals("build") || cmd.equals("attack");
    }

    private static void printHelp() {
        System.out.println("Команды:");
        System.out.println("move X Y - перемещение");
        System.out.println("attack - атаковать врага");
        System.out.println("buy - купить юнита");
        System.out.println("build - построить здание");
        System.out.println("stats - показать характеристики");
        System.out.println("save - сохранить игру");
        System.out.println("endturn - завершить ход");
        System.out.println("exit - выйти");
    }
}
