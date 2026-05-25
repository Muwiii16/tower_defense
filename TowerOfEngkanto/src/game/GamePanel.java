package game;

import app.*;
import database.DatabaseManager;
import game.enemies.*;
import game.towers.*;
import menu.MainMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;

public class GamePanel extends BasePanel {

    // Game state
    private int stageNumber;
    private String difficulty;
    private int gold;
    private int lives;
    private int score;
    private boolean gameOver;
    private boolean gameWon;
    private boolean paused;

    // Game objects
    private GameMap gameMap;
    private WaveManager waveManager;
    private List<Enemy> enemies;
    private List<Tower> towers;
    private List<Projectile> projectiles;
    private boolean[][] occupiedCells;

    // Sidebar
    private static final int SIDEBAR_WIDTH = ScreenUtils.scaleX(520);
    private static final int MAP_WIDTH = ScreenUtils.WIDTH - SIDEBAR_WIDTH;

    // Tower placement
    private String selectedTowerType;
    private int hoverCol = -1;
    private int hoverRow = -1;

    // Sidebar tower buttons
    private BufferedImage mandirigmaImg;
    private BufferedImage lantakaImg;
    private BufferedImage bantayImg;

    // Game loop timer
    private Timer gameTimer;

    // Database
    private DatabaseManager dbManager;

    public GamePanel(String username, int stageNumber, String difficulty) {
        super("assets/images/maps/map" + stageNumber + ".png", username);
        this.stageNumber = stageNumber;
        this.difficulty = difficulty;
        this.dbManager = new DatabaseManager();
    }

    @Override
    public String getPanelName() {
        return "game";
    }

    @Override
    protected void initComponents() {
        // Init game state based on difficulty
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
        gameOver = false;
        gameWon = false;
        paused = false;

        // Init collections
        enemies = new ArrayList<>();
        towers = new ArrayList<>();
        projectiles = new ArrayList<>();
        gameMap = new GameMap(stageNumber);
        occupiedCells = new boolean[gameMap.getCols()][gameMap.getRows()];
        waveManager = new WaveManager(stageNumber, difficulty,
                gameMap.getWaypoints(), enemies);

        // Load sidebar images
        mandirigmaImg = loadImage("assets/images/enemies_towers/mandirigma.png");
        lantakaImg = loadImage("assets/images/enemies_towers/lantaka_cannon.png");
        bantayImg = loadImage("assets/images/enemies_towers/bantay.png");

        // Mouse listener for tower placement and hover
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleHover(e.getX(), e.getY());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Start wave button
                int btnX = MAP_WIDTH + ScreenUtils.scaleX(20);
                int btnY = ScreenUtils.scaleY(800);
                int btnW = SIDEBAR_WIDTH - ScreenUtils.scaleX(40);
                int btnH = ScreenUtils.scaleY(80);

                if (e.getX() >= btnX && e.getX() <= btnX + btnW &&
                        e.getY() >= btnY && e.getY() <= btnY + btnH) {
                    waveManager.startNextWave();
                }
            }
        });

        // Start game loop — 60fps
        gameTimer = new Timer(16, e -> {
            if (!paused && !gameOver && !gameWon) {
                update();
            }
            repaint();
        });
        gameTimer.start();
    }

    // ── Game Loop ──────────────────────────────────────

    private void update() {
        waveManager.update();
        updateEnemies();
        updateTowers();
        updateProjectiles();
        checkWaveCleared();
        checkGameOver();
    }

    private void updateEnemies() {
        for (Enemy e : enemies) {
            e.update();
            if (e.hasReachedEnd()) {
                lives -= e.getDamage();
                e.setAlive(false);
            }
        }
        enemies.removeIf(e -> !e.isAlive());
    }

    private void updateTowers() {
        for (Tower t : towers) {
            t.findTarget(enemies);
            t.update();
            Projectile p = t.attack(t.getTarget());
            if (p != null) {
                // Give AOE projectiles access to all enemies
                if (p instanceof LantakaProjectile) {
                    ((LantakaProjectile) p).setAllEnemies(enemies);
                }
                projectiles.add(p);
                t.setTarget(null); // reset so attack() isn't called again
            }
        }
    }

    private void updateProjectiles() {
        for (Projectile p : projectiles) {
            p.update();
            if (!p.isAlive() && p.getTarget() != null
                    && !p.getTarget().isAlive()) {
                // Enemy killed — give gold and score
                gold += getRewardForEnemy(p.getTarget());
                score += getScoreForEnemy(p.getTarget());
            }
        }
        projectiles.removeIf(p -> !p.isAlive());
    }

    private int getRewardForEnemy(Enemy e) {
        return e.getReward();
    }

    private int getScoreForEnemy(Enemy e) {
        int base = e.getReward() * 10;
        if (difficulty.equals("hard"))
            return (int) (base * 1.5);
        if (difficulty.equals("easy"))
            return (int) (base * 0.8);
        return base;
    }

    private void checkWaveCleared() {
        if (waveManager.isAllWavesComplete() && enemies.isEmpty()) {
            gameWon = true;
            gameTimer.stop();
            onStageClear();
        }
    }

    private void checkGameOver() {
        if (lives <= 0) {
            lives = 0;
            gameOver = true;
            gameTimer.stop();
            showGameOverDialog();
        }
    }

    private void onStageClear() {
        // Save progress to database
        dbManager.saveGameProgress(username, stageNumber, difficulty, score);
        showVictoryDialog();
    }

    private void showVictoryDialog() {
        SwingUtilities.invokeLater(() -> {
            String msg = "Stage " + stageNumber + " Cleared!\n" +
                    "Score: " + score + "\n" +
                    "Lives remaining: " + lives;
            JOptionPane.showMessageDialog(this, msg, "Victory!", JOptionPane.INFORMATION_MESSAGE);
            returnToMainMenu();
        });
    }

    private void showGameOverDialog() {
        SwingUtilities.invokeLater(() -> {
            String msg = "Game Over!\nScore: " + score;
            JOptionPane.showMessageDialog(this, msg, "Game Over", JOptionPane.ERROR_MESSAGE);
            returnToMainMenu();
        });
    }

    private void returnToMainMenu() {
        gameTimer.stop();
        int unlockedStage = dbManager.getUnlockedStage(username);
        App.getInstance().addPanel(new MainMenuPanel(username, unlockedStage), "mainmenu");
        App.getInstance().showPanel("mainmenu");
    }

    // ── Input Handling ──────────────────────────────────

    private void handleClick(int mx, int my) {
        // Check sidebar buttons
        if (mx >= MAP_WIDTH) {
            handleSidebarClick(mx, my);
            return;
        }

        // Place tower on map
        if (selectedTowerType == null)
            return;
        Point cell = gameMap.pixelToCell(mx, my);
        if (cell == null)
            return;

        int col = cell.x;
        int row = cell.y;

        if (!gameMap.canPlaceTower(col, row, occupiedCells))
            return;

        Tower tower = createTower(selectedTowerType, col, row);
        if (tower == null)
            return;

        if (gold < tower.getCost()) {
            JOptionPane.showMessageDialog(this,
                    "Not enough gold!", "Insufficient Funds",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        gold -= tower.getCost();
        occupiedCells[col][row] = true;
        towers.add(tower);
        selectedTowerType = null;
    }

    private void handleSidebarClick(int mx, int my) {
        // Tower selection buttons in sidebar
        int btnX = MAP_WIDTH + ScreenUtils.scaleX(20);
        int btnSize = ScreenUtils.scaleX(100);
        int startY = ScreenUtils.scaleY(300);
        int spacing = ScreenUtils.scaleY(130);

        if (mx >= btnX && mx <= btnX + btnSize) {
            if (my >= startY && my <= startY + btnSize) {
                selectedTowerType = "mandirigma";
            } else if (my >= startY + spacing && my <= startY + spacing + btnSize) {
                selectedTowerType = "lantaka";
            } else if (my >= startY + spacing * 2 && my <= startY + spacing * 2 + btnSize) {
                selectedTowerType = "bantay";
            }
        }
    }

    private void handleHover(int mx, int my) {
        if (mx >= MAP_WIDTH) {
            hoverCol = -1;
            hoverRow = -1;
            return;
        }
        Point cell = gameMap.pixelToCell(mx, my);
        if (cell != null) {
            hoverCol = cell.x;
            hoverRow = cell.y;
        }
    }

    private Tower createTower(String type, int col, int row) {
        Point pixel = gameMap.cellToPixel(col, row);
        switch (type) {
            case "mandirigma":
                return new Mandirigma(pixel.x, pixel.y);
            case "lantaka":
                return new LantakaCannon(pixel.x, pixel.y);
            case "bantay":
                return new BantayBantayan(pixel.x, pixel.y);
            default:
                return null;
        }
    }

    // ── Drawing ──────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw map
        gameMap.draw(g2d);

        // Draw hover cell
        if (hoverCol >= 0 && selectedTowerType != null) {
            boolean canPlace = gameMap.canPlaceTower(hoverCol, hoverRow, occupiedCells);
            gameMap.drawHoverCell(g2d, hoverCol, hoverRow, canPlace);
        }

        // Draw towers
        for (Tower t : towers)
            t.draw(g2d);

        // Draw enemies
        for (Enemy e : enemies)
            e.draw(g2d);

        // Draw projectiles
        for (Projectile p : projectiles)
            p.draw(g2d);

        // Draw sidebar
        drawSidebar(g2d);

        g2d.dispose();
    }

    private void drawSidebar(Graphics2D g2d) {
        int sx = MAP_WIDTH;

        // Sidebar background
        g2d.setColor(new Color(15, 10, 5, 230));
        g2d.fillRect(sx, 0, SIDEBAR_WIDTH, ScreenUtils.HEIGHT);

        // Gold border on left
        g2d.setColor(new Color(180, 140, 40));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(sx, 0, sx, ScreenUtils.HEIGHT);

        // Wave info
        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(22)));
        g2d.setColor(new Color(220, 180, 60));
        g2d.drawString("Wave: " + waveManager.getCurrentWave()
                + "/" + waveManager.getTotalWaves(),
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(60));

        // Lives
        g2d.setColor(new Color(220, 80, 80));
        g2d.drawString("❤  Lives: " + lives,
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(100));

        // Gold
        g2d.setColor(new Color(255, 210, 60));
        g2d.drawString("💰  Gold: " + gold,
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
        g2d.drawString("TOWERS", sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(260));

        // Tower buttons
        drawTowerButton(g2d, mandirigmaImg, "Mandirigma",
                "Cost: 75g | DMG: 15",
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(280),
                "mandirigma");
        drawTowerButton(g2d, lantakaImg, "Lantaka Cannon",
                "Cost: 150g | DMG: 40",
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(420),
                "lantaka");
        drawTowerButton(g2d, bantayImg, "Bantay-Bantayan",
                "Cost: 100g | DMG: 25",
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(560),
                "bantay");

        // Start Wave button
        drawStartWaveButton(g2d, sx);

        // Selected tower indicator
        if (selectedTowerType != null) {
            g2d.setColor(new Color(100, 220, 100));
            g2d.setFont(new Font("SansSerif", Font.PLAIN, ScreenUtils.scaleFont(14)));
            g2d.drawString("Click map to place " + selectedTowerType,
                    sx + ScreenUtils.scaleX(10), ScreenUtils.scaleY(740));
            g2d.drawString("(Right-click to cancel)",
                    sx + ScreenUtils.scaleX(10), ScreenUtils.scaleY(760));
        }
    }

    private void drawTowerButton(Graphics2D g2d, BufferedImage img,
            String name, String stats, int x, int y, String type) {
        int btnW = SIDEBAR_WIDTH - ScreenUtils.scaleX(40);
        int btnH = ScreenUtils.scaleY(120);
        int imgSize = ScreenUtils.scaleX(80);

        boolean selected = type.equals(selectedTowerType);

        // Button background
        g2d.setColor(selected ? new Color(60, 50, 20, 220)
                : new Color(25, 20, 10, 200));
        g2d.fillRoundRect(x, y, btnW, btnH, 10, 10);

        // Border
        g2d.setColor(selected ? new Color(220, 180, 60)
                : new Color(120, 90, 30, 150));
        g2d.setStroke(new BasicStroke(selected ? 2 : 1));
        g2d.drawRoundRect(x, y, btnW, btnH, 10, 10);

        // Tower image
        if (img != null) {
            g2d.drawImage(img, x + ScreenUtils.scaleX(5),
                    y + (btnH - imgSize) / 2, imgSize, imgSize, null);
        }

        // Tower name
        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(16)));
        g2d.setColor(new Color(220, 200, 140));
        g2d.drawString(name, x + imgSize + ScreenUtils.scaleX(15),
                y + ScreenUtils.scaleY(40));

        // Stats
        g2d.setFont(new Font("SansSerif", Font.PLAIN, ScreenUtils.scaleFont(13)));
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawString(stats, x + imgSize + ScreenUtils.scaleX(15),
                y + ScreenUtils.scaleY(65));
    }

    private void drawStartWaveButton(Graphics2D g2d, int sx) {
        int btnX = sx + ScreenUtils.scaleX(20);
        int btnY = ScreenUtils.scaleY(800);
        int btnW = SIDEBAR_WIDTH - ScreenUtils.scaleX(40);
        int btnH = ScreenUtils.scaleY(80);

        boolean canStart = !waveManager.isWaveInProgress()
                && !waveManager.isAllWavesComplete()
                && !gameOver && !gameWon;

        g2d.setColor(canStart ? new Color(30, 80, 30, 220)
                : new Color(40, 40, 40, 180));
        g2d.fillRoundRect(btnX, btnY, btnW, btnH, 15, 15);

        g2d.setColor(canStart ? new Color(80, 200, 80)
                : new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(btnX, btnY, btnW, btnH, 15, 15);

        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(20)));
        g2d.setColor(canStart ? new Color(150, 255, 150)
                : new Color(120, 120, 120));
        String label = waveManager.isAllWavesComplete() ? "All Waves Done!"
                : waveManager.isWaveInProgress() ? "Wave in Progress..."
                        : "▶  Start Wave " + (waveManager.getCurrentWave() + 1);

        FontMetrics fm = g2d.getFontMetrics();
        int lx = btnX + (btnW - fm.stringWidth(label)) / 2;
        int ly = btnY + (btnH + fm.getAscent()) / 2 - 4;
        g2d.drawString(label, lx, ly);
    }

    // Right click to cancel tower selection
    {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    selectedTowerType = null;
                }
            }
        });
    }
}