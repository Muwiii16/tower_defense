package game.towers;

import game.Enemy;
import game.Projectile;
import game.Tower;

public class LantakaCannon extends Tower {

    private static final int DAMAGE = 40;
    private static final double RANGE = 175; // pixels
    private static final int ATTACK_SPEED = 120; // ticks (2.0s at 60fps)
    private static final int COST = 150;
    private static final int SPRITE_SIZE = 90;
    private static final int FRAME_DELAY = 10;
    private static final double AOE_RADIUS = 105; // 1.5 tiles

    public LantakaCannon(double x, double y) {
        super(x, y, 999, DAMAGE, RANGE, ATTACK_SPEED, COST, SPRITE_SIZE, FRAME_DELAY);
        loadStance("assets/images/gameplay/lantaka");
    }

    // Prioritize strongest enemy (most HP)
    @Override
    protected boolean selectTarget(Enemy newEnemy, Enemy currentTarget) {
        if (currentTarget == null)
            return true;
        // Skip flying enemies — can't hit them
        if (newEnemy instanceof game.enemies.Manananggal)
            return false;
        return newEnemy.getHp() > currentTarget.getHp();
    }

    @Override
    public Projectile attack(Enemy target) {
        // Don't attack flying units
        if (target instanceof game.enemies.Manananggal)
            return null;
        return new LantakaProjectile(getX(), getY(), target, DAMAGE, AOE_RADIUS);
    }
}