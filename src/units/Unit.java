package units;

import java.io.Serializable;

public abstract class Unit  implements Serializable {
    protected String name;
    protected int health;
    protected int attack;
    protected int speed;

    public Unit(String name, int health, int attack, int speed) {
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.speed = speed;
    }

    public void boost(int bonus) {
        this.attack += bonus;
        this.health += bonus;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int getAttack() {
        return attack;
    }

    public int getHealth() {
        return health;
    }

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }
}
