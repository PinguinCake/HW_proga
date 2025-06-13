package bot;

import units.Hero;
import main.GameMap;
import main.Cell;

import java.util.Random;

public class Bot {
    public void botMove(Hero aiHero, GameMap map) {
        System.out.println("Ход бота (" + aiHero.getName() + ")");
        Random rand = new Random();

        int action = rand.nextInt(3); // 0 = move, 1 = buy, 2 = build

        switch (action) {
            case 0 -> moveTowardsEnemyCastle(aiHero, map);
            case 1 -> {
                if (aiHero.getGold() >= 50) aiHero.buyRandomForAI(aiHero);
                else moveTowardsEnemyCastle(aiHero, map); // fallback
            }
            case 2 -> {
                if (aiHero.getGold() >= 100 && Math.random() < 0.7) aiHero.buildRandomStructure();
                else moveTowardsEnemyCastle(aiHero, map); // fallback
            }
        }

        // атака остаётся при встрече
        tryAttack(aiHero, map);
    }


    private boolean tryAttack(Hero aiHero, GameMap map) {
        Cell[][] cells = map.getCells();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int tx = aiHero.getX() + dx;
                int ty = aiHero.getY() + dy;
                if (tx >= 0 && ty >= 0 && tx < cells.length && ty < cells[0].length) {
                    Hero target = cells[tx][ty].getHero();
                    if (target != null && !target.getFraction().equals(aiHero.getFraction())) {
                        target.takeDamage(aiHero.getAttack());
                        System.out.println("Бот атакует " + target.getName());
                        if (!target.isAlive()) {
                            System.out.println(target.getName() + " пал!");
                            cells[tx][ty].setHero(null);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void moveTowardsEnemyCastle(Hero aiHero, GameMap map) {
        int dx = Integer.compare(0 - aiHero.getX(), 0);
        int dy = Integer.compare(0 - aiHero.getY(), 0);
        int nx = aiHero.getX() + dx;
        int ny = aiHero.getY() + dy;
        Cell[][] cells = map.getCells();
        if (nx >= 0 && ny >= 0 && nx < cells.length && ny < cells[0].length && cells[nx][ny].getHero() == null) {
            cells[aiHero.getX()][aiHero.getY()].setHero(null);
            aiHero.setX(nx);
            aiHero.setY(ny);
            cells[nx][ny].setHero(aiHero);
            System.out.println("Бот двигается на (" + nx + "," + ny + ")");
        } else {
            System.out.println("Бот не может двигаться.");
        }
    }
}
