package game;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameEntity {

    private double x;
    private double y;
    private int hp;
    private int maxHp;
    private boolean alive;
    private BufferedImage[] frames;
    private int currentFrame;
    private int frameTimer;
    private int frameDelay;

    public GameEntity(double x, double y, int hp, int frameDelay) {
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = hp;
        this.alive = true;
        this.currentFrame = 0;
        this.frameTimer = 0;
        this.frameDelay = frameDelay;
    }

    public abstract Projectile update();

    public abstract void draw(Graphics2D g2d);

    protected void updateAnimation() {
        if (frames == null || frames.length == 0)
            return;
        frameTimer++;
        if (frameTimer >= frameDelay) {
            frameTimer = 0;
            currentFrame = (currentFrame + 1) % frames.length;
        }
    }

    protected void drawSprite(Graphics2D g2d, int width, int height) {
        if (frames == null || frames.length == 0)
            return;
        BufferedImage frame = frames[currentFrame];
        if (frame != null) {
            g2d.drawImage(frame, (int) x - width / 2, (int) y - height / 2, width, height, null);
        }
    }

    protected void drawHpBar(Graphics2D g2d, int width) {
        int barW = width;
        int barH = 6;
        int barX = (int) x - width / 2;
        int barY = (int) y - width / 2 - 10;

        g2d.setColor(new Color(60, 0, 0));
        g2d.fillRect(barX, barY, barW, barH);

        float ratio = (float) hp / maxHp;
        Color hpColor = ratio > 0.5f ? new Color(0, 200, 0)
                : ratio > 0.25f ? new Color(220, 180, 0)
                        : new Color(200, 0, 0);
        g2d.setColor(hpColor);
        g2d.fillRect(barX, barY, (int) (barW * ratio), barH);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(barX, barY, barW, barH);
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            alive = false;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public boolean isAlive() {
        return alive;
    }

    public BufferedImage[] getFrames() {
        return frames;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setFrames(BufferedImage[] frames) {
        this.frames = frames;
    }

    public void setFrameDelay(int frameDelay) {
        this.frameDelay = frameDelay;
    }
}