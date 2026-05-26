package game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Projectile extends GameEntity {

    private double speedX;
    private double speedY;
    private int damage;
    private int spriteSize;
    private boolean isAOE;
    private double aoeRadius;
    private Enemy target;

    public Projectile(double x, double y, Enemy target, int damage,
            double projectileSpeed, int spriteSize,
            boolean isAOE, double aoeRadius, int frameDelay) {
        super(x, y, 1, frameDelay);
        this.target = target;
        this.damage = damage;
        this.spriteSize = spriteSize;
        this.isAOE = isAOE;
        this.aoeRadius = aoeRadius;

        // Calculate direction towards target
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            this.speedX = (dx / dist) * projectileSpeed;
            this.speedY = (dy / dist) * projectileSpeed;
        }
    }

    @Override
    public Projectile update() {
        if (!isAlive())
            return null;

        if (!target.isAlive()) {
            setAlive(false);
            return null;
        }

        // Re-calculate direction towards target every tick (homing)
        double dx = target.getX() - getX();
        double dy = target.getY() - getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        double projectileSpeed = Math.sqrt(speedX * speedX + speedY * speedY);

        if (dist <= projectileSpeed) {
            setX(target.getX());
            setY(target.getY());
            onHit();
            setAlive(false);
        } else {
            setX(getX() + (dx / dist) * projectileSpeed);
            setY(getY() + (dy / dist) * projectileSpeed);
        }

        updateAnimation();
        return null;
    }

    // Called when projectile hits — subclasses define what happens
    public abstract void onHit();

    @Override
    public void draw(Graphics2D g2d) {
        if (!isAlive())
            return;
        drawSprite(g2d, spriteSize, spriteSize);
    }

    // Load single image as projectile sprite
    protected void loadSprite(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            setFrames(new BufferedImage[] { img });
        } catch (IOException e) {
            System.err.println("Could not load projectile sprite: " + path);
        }
    }

    // ── Getters & Setters ──────────────────────────────
    public int getDamage() {
        return damage;
    }

    public boolean isAOE() {
        return isAOE;
    }

    public double getAoeRadius() {
        return aoeRadius;
    }

    public Enemy getTarget() {
        return target;
    }

    public int getSpriteSize() {
        return spriteSize;
    }
}