package game.towers;

import game.Enemy;
import game.Projectile;
import java.util.List;

public class LantakaProjectile extends Projectile {

    private double aoeRadius;
    private List<Enemy> allEnemies;

    public LantakaProjectile(double x, double y, Enemy target,
            int damage, double aoeRadius) {
        super(x, y, target, damage, 10.0, 20, true, aoeRadius, 2);
        this.aoeRadius = aoeRadius;
        loadSprite("assets/images/gameplay/lantaka/projectile.png");
    }

    public void setAllEnemies(List<Enemy> enemies) {
        this.allEnemies = enemies;
    }

    @Override
    public void onHit() {
        if (allEnemies == null)
            return;
        // AOE — damage all enemies in radius
        for (Enemy e : allEnemies) {
            if (!e.isAlive())
                continue;
            if (e instanceof game.enemies.Manananggal)
                continue; // can't hit flying
            double dx = e.getX() - getX();
            double dy = e.getY() - getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= aoeRadius) {
                e.takeDamage(getDamage());
            }
        }
    }
}