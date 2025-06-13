package buildings;

import units.Hero;

public class Tavern extends Building {
    @Override
    public String getName() {
        return "Таверна";
    }

    @Override
    public void applyEffect(Hero hero) {
        hero.setCanHireNewHeroes(true);
    }
}
