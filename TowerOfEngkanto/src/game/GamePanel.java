package game;

import app.*;
import database.DatabaseManager;
import menu.MainMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends BasePanel {

    private int stageNumber;
    private String difficulty;
    private int gold;
    private int lives;
    private int score;
    private int currentWave;
    private int totalWaves;

    private BufferedImage mandirigmaImg;
    private BufferedImage lantakaImg;
    private BufferedImage bantayImg;

    private static final String[] STAGE_NAMES = {
            "", "Gubat ng mga Nilalang", "Bundok ng Sumpa", "Tore ng Engkanto"
    };

    private static final int SIDEBAR_WIDTH = ScreenUtils.scaleX(520);
    private static final int MAP_WIDTH = ScreenUtils.WIDTH - SIDEBAR_WIDTH;

    private GameMap gameMap;
    private Timer gameTimer;
    private DatabaseManager dbManager;

    public GamePanel(String username, int stageNumber, String difficulty) {
        super(null, username);
        this.stageNumber = stageNumber;
        this.difficulty = difficulty;
        this.dbManager = new DatabaseManager();

        // Re-run init now that fields are set
        removeAll();
        initComponents();
    }

    @Override
    public String getPanelName() {
        return "game";
    }

    @Override
    protected void initComponents() {
        if (difficulty == null)
            return;

        switch (difficulty) {
            case "easy":
                gold = 250;
                lives = 30;
                break;
            case "hard":
                gold = 150;
                lives = 10;
                break;
            default:
                gold = 200;
                lives = 20;
                break;
        }
        score = 0;
        currentWave = 0;
        totalWaves = 5;

        gameMap = new GameMap(stageNumber, MAP_WIDTH);

        mandirigmaImg = loadImage("assets/images/enemies_towers/mandirigma.png");
        lantakaImg = loadImage("assets/images/enemies_towers/lantaka_cannon.png");
        bantayImg = loadImage("assets/images/enemies_towers/bantay.png");

        // Game loop
        gameTimer = new Timer(16, e -> repaint());
        gameTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw map
        gameMap.draw(g2d);

        drawStageInfo(g2d);

        // Draw sidebar
        drawSidebar(g2d);

        g2d.dispose();
    }

    private void drawSidebar(Graphics2D g2d) {
        int sx = MAP_WIDTH;

        // Background
        g2d.setColor(new Color(15, 10, 5, 230));
        g2d.fillRect(sx, 0, SIDEBAR_WIDTH, ScreenUtils.HEIGHT);

        // Left border
        g2d.setColor(new Color(180, 140, 40));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(sx, 0, sx, ScreenUtils.HEIGHT);

        // Wave
        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(22)));
        g2d.setColor(new Color(220, 180, 60));
        g2d.drawString("Wave: " + currentWave + "/" + totalWaves,
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(60));

        // Lives
        g2d.setColor(new Color(220, 80, 80));
        g2d.drawString("Lives: " + lives,
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(100));

        // Gold
        g2d.setColor(new Color(255, 210, 60));
        g2d.drawString("Gold: " + gold,
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(140));

        // Score
        g2d.setColor(new Color(180, 220, 140));
        g2d.drawString("Score: " + score,
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(180));

        // Divider
        g2d.setColor(new Color(180, 140, 40, 120));
        g2d.fillRect(sx + ScreenUtils.scaleX(10), ScreenUtils.scaleY(200),
                SIDEBAR_WIDTH - ScreenUtils.scaleX(20), 2);

        // Towers label
        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(18)));
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawString("TOWERS", sx + ScreenUtils.scaleX(20),
                ScreenUtils.scaleY(250));

        // Tower placeholders
        drawTowerSlot(g2d, mandirigmaImg, "Mandirigma", "Cost: 75g", sx, ScreenUtils.scaleY(270));
        drawTowerSlot(g2d, lantakaImg, "Lantaka Cannon", "Cost: 150g", sx, ScreenUtils.scaleY(410));
        drawTowerSlot(g2d, bantayImg, "Bantay-Bantayan", "Cost: 100g", sx, ScreenUtils.scaleY(550));

        // Start wave button
        drawStartWaveButton(g2d, sx);

        // Back button (temp)
        g2d.setFont(new Font("SansSerif", Font.PLAIN, ScreenUtils.scaleFont(14)));
        g2d.setColor(new Color(180, 100, 100));
        g2d.drawString("[ Back to Menu ]",
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(980));
    }

    private void drawTowerSlot(Graphics2D g2d, BufferedImage img, String name,
            String cost, int sx, int y) {
        int btnW = SIDEBAR_WIDTH - ScreenUtils.scaleX(40);
        int btnH = ScreenUtils.scaleY(110);
        int x = sx + ScreenUtils.scaleX(20);

        // Background
        g2d.setColor(new Color(25, 20, 10, 200));
        g2d.fillRoundRect(x, y, btnW, btnH, 10, 10);

        // Border
        g2d.setColor(new Color(120, 90, 30, 150));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x, y, btnW, btnH, 10, 10);

        // Image placeholder
        if (img != null) {
            g2d.drawImage(img, x + ScreenUtils.scaleX(5), y + ScreenUtils.scaleY(10), ScreenUtils.scaleX(80),
                    ScreenUtils.scaleY(80), null);
        } else {
            g2d.setColor(new Color(60, 50, 30, 150));
            g2d.fillRoundRect(x + ScreenUtils.scaleX(5),
                    y + ScreenUtils.scaleY(10),
                    ScreenUtils.scaleX(80), ScreenUtils.scaleY(80), 8, 8);
            g2d.setColor(new Color(150, 120, 60, 150));
            g2d.drawString("IMG", x + ScreenUtils.scaleX(22),
                    y + ScreenUtils.scaleY(55));
        }

        // Name and cost
        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(16)));
        g2d.setColor(new Color(220, 200, 140));
        g2d.drawString(name, x + ScreenUtils.scaleX(100),
                y + ScreenUtils.scaleY(40));

        g2d.setFont(new Font("SansSerif", Font.PLAIN, ScreenUtils.scaleFont(13)));
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawString(cost, x + ScreenUtils.scaleX(100),
                y + ScreenUtils.scaleY(65));
    }

    private void drawStartWaveButton(Graphics2D g2d, int sx) {
        int btnX = sx + ScreenUtils.scaleX(20);
        int btnY = ScreenUtils.scaleY(800);
        int btnW = SIDEBAR_WIDTH - ScreenUtils.scaleX(40);
        int btnH = ScreenUtils.scaleY(80);

        g2d.setColor(new Color(30, 80, 30, 220));
        g2d.fillRoundRect(btnX, btnY, btnW, btnH, 15, 15);
        g2d.setColor(new Color(80, 200, 80));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(btnX, btnY, btnW, btnH, 15, 15);

        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(20)));
        g2d.setColor(new Color(150, 255, 150));
        String label = "▶  Start Wave 1";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(label,
                btnX + (btnW - fm.stringWidth(label)) / 2,
                btnY + (btnH + fm.getAscent()) / 2 - 4);
    }

    private void drawStageInfo(Graphics2D g2d) {
        int barHeight = gameMap.getOffsetY();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, MAP_WIDTH, barHeight);

        // Stage number
        g2d.setFont(loadGothicFont(28));
        g2d.setColor(new Color(180, 140, 40));
        g2d.drawString("STAGE " + stageNumber,
                ScreenUtils.scaleX(20), barHeight - ScreenUtils.scaleY(35));

        // Stage name
        g2d.setFont(loadGothicFont(22));
        g2d.setColor(new Color(200, 180, 120));
        g2d.drawString(STAGE_NAMES[stageNumber],
                ScreenUtils.scaleX(20), barHeight - ScreenUtils.scaleY(10));

        // Difficulty — right aligned
        Color diffColor;
        switch (difficulty) {
            case "easy":
                diffColor = new Color(80, 200, 80);
                break;
            case "hard":
                diffColor = new Color(220, 60, 60);
                break;
            default:
                diffColor = new Color(220, 180, 60);
                break;
        }
        g2d.setFont(loadGothicFont(22));
        g2d.setColor(diffColor);
        String diff = difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1);
        String label = "[ " + diff + " ]";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(label,
                MAP_WIDTH - fm.stringWidth(label) - ScreenUtils.scaleX(20),
                barHeight - ScreenUtils.scaleY(20));
    }

    private Font loadGothicFont(float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT,
                    new File("assets/fonts/MedievalSharp.ttf"));
            return font.deriveFont(Font.PLAIN, ScreenUtils.scaleFont((int) size));
        } catch (Exception e) {
            System.err.println("Could not load gothic font, using fallback");
            return new Font("Serif", Font.BOLD, ScreenUtils.scaleFont((int) size));
        }
    }
}