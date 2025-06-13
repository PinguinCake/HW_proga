package battle;

import units.Hero;
import units.Unit;

import java.util.*;

public class Battle {
    public static boolean testMode = false;

    public static void startBattle(Hero hero1, Hero hero2) {

        List<Unit> army1 = new ArrayList<>(hero1.getArmy());
        List<Unit> army2 = new ArrayList<>(hero2.getArmy());

        if (hero1.hasWatchTower()) {
            for (Unit u : army1) u.boost(10);
        }
        if (hero2.hasWatchTower()) {
            for (Unit u : army2) u.boost(10);
        }

        System.out.println("\n⚔️ БИТВА между " + hero1.getName() + " и " + hero2.getName());

        while (!army1.isEmpty() && !army2.isEmpty()) {
            printBattleField(army1, army2);

            Unit attacker = army1.get(0);
            Unit defender = army2.get(0);

            System.out.println(attacker.getName() + " наносит " + attacker.getAttack() + " урона " + defender.getName());
            defender.takeDamage(attacker.getAttack());

            if (!defender.isAlive()) {
                System.out.println(defender.getName() + " погиб!");
                army2.remove(0);
            }

            if (!army2.isEmpty()) {
                Unit counter = army2.get(0);
                attacker.takeDamage(counter.getAttack());
                System.out.println(counter.getName() + " контратакует на " + counter.getAttack() + " урона");

                if (!attacker.isAlive()) {
                    System.out.println(attacker.getName() + " погиб!");
                    army1.remove(0);
                }
            }

            if (!testMode) {
                System.out.println("Нажмите Enter для продолжения...");
                new Scanner(System.in).nextLine();
            }
        }

        if (army1.isEmpty() && army2.isEmpty()) {
            System.out.println("Ничья!");
        } else {
            if (!army1.isEmpty()) {
                System.out.println(hero1.getName() + " выиграл бой!");
            } else {
                System.out.println(hero2.getName() + " выиграл бой!");
            }
        }

        hero1.setArmy(new ArrayList<>(army1));
        hero2.setArmy(new ArrayList<>(army2));
    }

    private static void printBattleField(List<Unit> left, List<Unit> right) {
        System.out.println("\n--- Поле боя ---");
        int max = Math.max(left.size(), right.size());
        for (int i = 0; i < max; i++) {
            String l = (i < left.size()) ? String.format("%-20s", left.get(i).getName() + " " + left.get(i).getHealth() + "HP") : " ".repeat(20);
            String r = (i < right.size()) ? String.format("%-20s", right.get(i).getName() + " " + right.get(i).getHealth() + "HP") : " ".repeat(20);
            System.out.println(l + "   vs   " + r);
        }
        System.out.println("----------------\n");
    }
}
