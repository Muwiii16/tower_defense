package game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;

public abstract class Enemy extends GameEntity {

    private List<Point> waypoints;
    private int currentWaypoint;
    private double speed;
    private int reward;
    private int damage;
    private boolean reachedEnd;
    private int spriteSize;

    public Enemy(double x, double y, int hp, double speed,
            int reward, int damage, int frameDelay,
            List<Point> waypoints, int spriteSize) {
        super(x, y, hp, frameDelay);
        this.speed = speed;
        this.reward = reward;
        this.damage = damage;
        this.waypoints = waypoints;
        this.currentWaypoint = 0;
        this.reachedEnd = false;
        this.spriteSize = spriteSize;
    }

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
        if (!isAlive() || reachedEnd)
            return;
        moveAlongPath();
        updateAnimation();
    }

    private void moveAlongPath() {
        if (currentWaypoint >= waypoints.size()) {
            reachedEnd = true;
            setAlive(false);
            return;
        }

        Point target = waypoints.get(currentWaypoint);
        double tx = target.x;
        double ty = target.y;

        double dx = tx - getX();
        double dy = ty - getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= speed) {

            setX(tx);
            setY(ty);
            currentWaypoint++;
        } else {

            setX(getX() + (dx / dist) * speed);
            setY(getY() + (dy / dist) * speed);
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (!isAlive())
            return;
        drawSprite(g2d, spriteSize, spriteSize);
        drawHpBar(g2d, spriteSize);
    }

    public void applySpecialAbility(Object target) {
    }

    public double getSpeed() {
        return speed;
    }

    public int getReward() {
        return reward;
    }

    public int getDamage() {
        return damage;
    }

    public boolean hasReachedEnd() {
        return reachedEnd;
    }

    public int getSpriteSize() {
        return spriteSize;
    }

    public int getCurrentWaypoint() {
        return currentWaypoint;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setReachedEnd(boolean r) {
        this.reachedEnd = r;
    }
}