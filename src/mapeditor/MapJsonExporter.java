package mapeditor;

import main.Cell;
import main.GameMap;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MapJsonExporter {

    public static void exportToJson(GameMap map, String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("[");
            for (int x = 0; x < map.getWidth(); x++) {
                for (int y = 0; y < map.getHeight(); y++) {
                    Cell cell = map.getCells()[x][y];

                    String json = String.format(
                            "  {\"x\": %d, \"y\": %d, \"zone\": \"%s\", \"obstacleType\": \"%s\", \"symbol\": \"%s\", \"gold\": %d}",
                            x, y,
                            cell.getZone(),
                            cell.isObstacle() ? cell.getObstacleType() : "none",
                            cell.getSymbol(),
                            cell.getGoldAmount()
                    );

                    boolean isLast = (x == map.getWidth() - 1) && (y == map.getHeight() - 1);
                    out.println(json + (isLast ? "" : ","));
                }
            }
            out.println("]");
            System.out.println("Карта экспортирована в JSON (вручную): " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении JSON: " + e.getMessage());
        }
    }
}
