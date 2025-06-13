package score;

import java.io.*;
import java.util.*;

public class ScoreManager {
    private static final String FILE = "records.csv";

    public static void recordScore(String name, String map, int score) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE, true))) {
            out.println(name + "," + map + "," + score);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTop5Scores() {
        List<String> scores = new ArrayList<>();

        // ✅ создаём файл, если его нет
        File f = new File(FILE);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return scores; // пустой список, всё норм
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        scores.sort((a, b) -> Integer.compare(
                Integer.parseInt(b.split(",")[2]),
                Integer.parseInt(a.split(",")[2])
        ));

        return scores.subList(0, Math.min(5, scores.size()));
    }
}
