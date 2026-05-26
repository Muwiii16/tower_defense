package game.towers;

import game.Enemy;
import game.Projectile;

public class MandirigmaProjectile extends Projectile {

    public MandirigmaProjectile(double x, double y, Enemy target, int damage) {
        super(x, y, target, damage, 12.0, 16, false, 0, 2);
        loadSprite("assets/images/gameplay/mandirigma/projectile.png");
    }

    @Override
    public void onHit() {
        Enemy target = getTarget();
        if (!target.isAlive())
            return;

        // Check Tikbalang dodge
        if (target instanceof game.enemies.Tikbalang) {
            game.enemies.Tikbalang t = (game.enemies.Tikbalang) target;
            if (t.attemptDodge()) {
                System.out.println("Tikbalang dodged!");
                return;
            }
        }
        target.takeDamage(getDamage());
    }
}