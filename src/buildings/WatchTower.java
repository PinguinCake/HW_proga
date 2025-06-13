package buildings;

import units.Hero;

public class WatchTower extends Building {
    @Override
    public String getName() {
        return "Сторожевой пост";
    }

    @Override
    public void applyEffect(Hero hero) {
        hero.setWatchTower(true);
        hero.setBuildingArmyLimitBonus(5); // Увеличенный лимит
    }
}
