package mapeditor;

import main.Cell;
import main.GameMap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapEditor {
    public static GameMap createMapFromUserInput(int width, int height) {
        GameMap map = new GameMap(width, height);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Редактирование карты (вводите команды, end - завершить):");

        while (true) {
            System.out.println("set x y type [zone] [gold/penalty] [symbol/type]:");
            String line = scanner.nextLine();
            if (line.equals("end")) break;
            String[] parts = line.split(" ");
            if (parts.length < 3) continue;

            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            String type = parts[2];
            String zone = (parts.length >= 4) ? parts[3] : "Neutral";

            Cell cell = map.getCells()[x][y];
            cell.setZone(zone);

            switch (type) {
                case "road" -> cell.setRoad(true);
                case "castle" -> {
                    cell.setCastle(true);
                    cell.setOwner(zone);
                }
                case "gold" -> {
                    int gold = (parts.length >= 5) ? Integer.parseInt(parts[4]) : 100;
                    cell.setGoldAmount(gold);
                }
                case "clear" -> {
                    cell.setRoad(false);
                    cell.setCastle(false);
                    cell.setGoldAmount(0);
                    cell.setObstacle(false);
                }
                case "obstacle" -> {
                    String obstacleType = (parts.length >= 5) ? parts[4] : "penalty";
                    int penalty = (parts.length >= 6) ? Integer.parseInt(parts[5]) : -1;
                    String symbol = (parts.length >= 7) ? parts[6] : "#";
                    cell.setObstacle(true);
                    cell.setObstacleType(obstacleType);
                    cell.setPenalty(penalty);
                    cell.setSymbol(symbol);
                }
            }
        }

        // Сохраняем в JSON после редактирования
        System.out.print("Введите имя файла для JSON-экспорта (например, map1.json): ");
        String filename = scanner.nextLine();
        MapJsonExporter.exportToJson(map, filename);

        return map;
    }

    public static void saveMapToFile(GameMap map, String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (int x = 0; x < map.getCells().length; x++) {
                for (int y = 0; y < map.getCells()[x].length; y++) {
                    Cell cell = map.getCells()[x][y];
                    out.printf("%d,%d,%s,%s,%b,%b,%d%n",
                            x, y,
                            cell.getZone(),
                            cell.getOwner() == null ? "None" : cell.getOwner(),
                            cell.isRoad(),
                            cell.isCastle(),
                            cell.getGoldAmount()
                    );
                }
            }
            System.out.println("Карта сохранена в файл: " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении карты: " + e.getMessage());
        }
    }

    public static GameMap loadMapFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<String[]> lines = new ArrayList<>();
            while (reader.ready()) {
                String[] parts = reader.readLine().split(",");
                if (parts.length == 7) lines.add(parts);
            }

            // Определим размер карты по макс. x/y
            int maxX = lines.stream().mapToInt(p -> Integer.parseInt(p[0])).max().orElse(11);
            int maxY = lines.stream().mapToInt(p -> Integer.parseInt(p[1])).max().orElse(11);
            GameMap map = new GameMap(maxX + 1, maxY + 1);

            for (String[] parts : lines) {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                String zone = parts[2];
                String owner = parts[3];
                boolean road = Boolean.parseBoolean(parts[4]);
                boolean castle = Boolean.parseBoolean(parts[5]);
                int gold = Integer.parseInt(parts[6]);

                Cell cell = map.getCells()[x][y];
                cell.setZone(zone);
                cell.setOwner(owner.equals("None") ? null : owner);
                cell.setRoad(road);
                cell.setCastle(castle);
                cell.setGoldAmount(gold);
            }

            System.out.println("Карта успешно загружена из файла: " + filename);
            return map;

        } catch (IOException e) {
            System.out.println("Ошибка при загрузке карты: " + e.getMessage());
            return null;
        }
    }

}
