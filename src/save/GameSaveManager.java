package save;

import main.GameMap;
import units.Hero;

import java.io.*;

public class GameSaveManager {
    public static void saveGame(Hero hero1, Hero hero2, GameMap map, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(hero1);
            out.writeObject(hero2);
            out.writeObject(map);
            System.out.println("Игра сохранена: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object[] loadGame(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Hero hero1 = (Hero) in.readObject();
            Hero hero2 = (Hero) in.readObject();
            GameMap map = (GameMap) in.readObject();
            return new Object[]{hero1, hero2, map};
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
