package game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public abstract class Tower extends GameEntity {

    private int damage;
    private double range; // in pixels
    private int attackSpeed; // ticks between attacks
    private int attackTimer; // counts up to attackSpeed
    private int cost;
    private int spriteSize;
    private Enemy target; // current enemy being targeted
    private boolean slow; // does this tower slow enemies?

    public Tower(double x, double y, int hp, int damage, double range,
            int attackSpeed, int cost, int spriteSize, int frameDelay) {
        super(x, y, hp, frameDelay);
        this.damage = damage;
        this.range = range;
        this.attackSpeed = attackSpeed;
        this.attackTimer = 0;
        this.cost = cost;
        this.spriteSize = spriteSize;
        this.target = null;
        this.slow = false;
    }

    // Load animation frames
    protected void loadFrames(String folderPath, String prefix, int frameCount) {
        BufferedImage[] frames = new BufferedImage[frameCount];
        for (int i = 0; i < frameCount; i++) {
            try {
                frames[i] = ImageIO.read(new File(folderPath + "/" + prefix + (i + 1) + ".png"));
            } catch (IOException e) {
                System.err.println("Could not load frame: " + prefix + (i + 1) + ".png");
            }
        }
        setFrames(frames);
    }

    @Override
    public void update() {
        updateAnimation();
        if (target != null && (!target.isAlive() || !isInRange(target))) {
            target = null;
        }
        attackTimer++;
        if (attackTimer >= attackSpeed) {
            attackTimer = 0;
            if (target != null) {
                attack(target);
            }
        }
    }

    // Find closest enemy in range from list
    public void findTarget(List<Enemy> enemies) {
        if (target != null && target.isAlive() && isInRange(target))
            return;
        target = null;
        double closestDist = Double.MAX_VALUE;
        for (Enemy e : enemies) {
            if (!e.isAlive())
                continue;
            double dist = distanceTo(e);
            if (dist <= range && dist < closestDist) {
                // Prioritize by target priority — subclasses override selectTarget()
                if (selectTarget(e, target)) {
                    closestDist = dist;
                    target = e;
                }
            }
        }
    }

    // Subclasses override this to change targeting priority
    // Returns true if newEnemy should replace currentTarget
    protected boolean selectTarget(Enemy newEnemy, Enemy currentTarget) {
        return true; // default: pick first in range
    }

    // Subclasses override this to define attack behavior
    public abstract Projectile attack(Enemy target);

    @Override
    public void draw(Graphics2D g2d) {
        if (!isAlive())
            return;

        // Draw range circle when selected (optional debug)
        // g2d.setColor(new Color(255, 255, 255, 40));
        // g2d.drawOval((int)(getX()-range), (int)(getY()-range), (int)(range*2),
        // (int)(range*2));

        drawSprite(g2d, spriteSize, spriteSize);
    }

    // Draw range circle when tower is selected
    public void drawRange(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.fillOval((int) (getX() - range), (int) (getY() - range),
                (int) (range * 2), (int) (range * 2));
        g2d.setColor(new Color(255, 255, 255, 120));
        g2d.drawOval((int) (getX() - range), (int) (getY() - range),
                (int) (range * 2), (int) (range * 2));
    }

    protected boolean isInRange(Enemy e) {
        return distanceTo(e) <= range;
    }

    protected double distanceTo(Enemy e) {
        double dx = e.getX() - getX();
        double dy = e.getY() - getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    // ── Getters & Setters ──────────────────────────────
    public int getDamage() {
        return damage;
    }

    public double getRange() {
        return range;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public int getCost() {
        return cost;
    }

    public int getSpriteSize() {
        return spriteSize;
    }

    public Enemy getTarget() {
        return target;
    }

    public boolean isSlow() {
        return slow;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setTarget(Enemy target) {
        this.target = target;
    }

    public void setSlow(boolean slow) {
        this.slow = slow;
    }
}