package mapeditor;

import main.Cell;
import main.GameMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapJsonImporter {

    public static GameMap importFromJson(String filename, int width, int height) {
        GameMap map = new GameMap(width, height);
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            Pattern pattern = Pattern.compile("\\{\\s*\"x\": (\\d+),\\s*\"y\": (\\d+),\\s*\"zone\": \"(.*?)\",\\s*\"obstacleType\": \"(.*?)\",\\s*\"symbol\": \"(.*?)\",\\s*\"gold\": (\\d+)\\s*}");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int x = Integer.parseInt(matcher.group(1));
                    int y = Integer.parseInt(matcher.group(2));
                    String zone = matcher.group(3);
                    String obstacleType = matcher.group(4);
                    String symbol = matcher.group(5);
                    int gold = Integer.parseInt(matcher.group(6));

                    Cell cell = map.getCells()[x][y];
                    cell.setZone(zone);
                    cell.setGoldAmount(gold);
                    cell.setSymbol(symbol);

                    if (!obstacleType.equals("none")) {
                        cell.setObstacle(true);
                        cell.setObstacleType(obstacleType);
                    }
                }
            }
            System.out.println("Карта успешно загружена из JSON: " + filename);
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке JSON: " + e.getMessage());
        }

        return map;
    }
}
