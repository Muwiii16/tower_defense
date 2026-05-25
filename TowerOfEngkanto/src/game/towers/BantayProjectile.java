package game.towers;

import game.Enemy;
import game.Projectile;

public class BantayProjectile extends Projectile {

    public BantayProjectile(double x, double y, Enemy target, int damage) {
        super(x, y, target, damage, 8.0, 14, false, 0, 2);
        loadSprite("assets/images/projectiles/arrow.png");
    }

    @Override
    public void onHit() {
        Enemy target = getTarget();
        if (!target.isAlive())
            return;

        // Flak Shot ignores Tikbalang's evasion
        // so no dodge check here — always hits
        target.takeDamage(getDamage());
    }
}