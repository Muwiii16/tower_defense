package game.towers;

import game.Enemy;
import game.Projectile;
import game.Tower;

public class Mandirigma extends Tower {

    private static final int DAMAGE = 15;
    private static final double RANGE = 210; // pixels
    private static final int ATTACK_SPEED = 30; // ticks (0.5s at 60fps)
    private static final int COST = 75;
    private static final int SPRITE_SIZE = 80;
    private static final int FRAME_DELAY = 8;

    public Mandirigma(double x, double y) {
        super(x, y, 999, DAMAGE, RANGE, ATTACK_SPEED, COST, SPRITE_SIZE, FRAME_DELAY);
        loadStance("assets/images/gameplay/mandirigma");
    }

    // Prioritize first enemy on path (highest waypoint index)
    @Override
    protected boolean selectTarget(Enemy newEnemy, Enemy currentTarget) {
        if (currentTarget == null)
            return true;
        return newEnemy.getCurrentWaypoint() > currentTarget.getCurrentWaypoint();
    }

    // Single target attack
    @Override
    public Projectile attack(Enemy target) {
        return new MandirigmaProjectile(getX(), getY(), target, DAMAGE);
    }
}