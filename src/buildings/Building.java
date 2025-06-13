package buildings;

import units.Hero;
import java.io.Serializable;

public abstract class Building implements Serializable {
    public abstract String getName();
    public abstract void applyEffect(Hero hero);
}
