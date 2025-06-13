package buildings;

import units.Hero;

public class Stable extends Building {
    @Override
    public String getName() {
        return "Конюшня";
    }

    @Override
    public void applyEffect(Hero hero) {
        hero.setBuildingSpeedBonus(1);
    }
}
