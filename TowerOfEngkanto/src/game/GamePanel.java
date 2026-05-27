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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends BasePanel {

    // Game state
    private int stageNumber;
    private String difficulty;
    private int gold;
    private int lives;
    private int score;
    private boolean gameOver;
    private boolean gameWon;

    // Sidebar constants
    private static final int SIDEBAR_WIDTH = ScreenUtils.scaleX(520);
    private static final int MAP_WIDTH = ScreenUtils.WIDTH - SIDEBAR_WIDTH;

    // Game objects
    private GameMap gameMap;
    private WaveManager waveManager;
    private List<Enemy> enemies;
    private List<Tower> towers;
    private List<Projectile> projectiles;
    private boolean[][] occupiedCells;

    // Tower placement
    private String selectedTowerType;
    private int hoverCol = -1;
    private int hoverRow = -1;
    private Tower selectedPlacedTower; // tower clicked on map

    // Sidebar images
    private BufferedImage mandirigmaImg;
    private BufferedImage lantakaImg;
    private BufferedImage bantayImg;

    // Prep time and wave delay
    private int prepTimer;
    private int waveDelayTimer;
    private boolean prepPhase;
    private boolean waitingForNextWave;
    private static final int PREP_SECONDS = 15;
    private static final int WAVE_DELAY_SECONDS = 9;

    // Pause
    private boolean paused;

    private int cursorX, cursorY; // current mouse position

    // Stage info
    private static final String[] STAGE_NAMES = {
            "", "Gubat ng mga Nilalang", "Bundok ng Sumpa", "Tore ng Engkanto"
    };

    // Game loop
    private Timer gameTimer;
    private DatabaseManager dbManager;

    public GamePanel(String username, int stageNumber, String difficulty) {
        super(null, username);
        this.stageNumber = stageNumber;
        this.difficulty = difficulty;
        this.dbManager = new DatabaseManager();
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

        // Init stats
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
        prepPhase = true;
        prepTimer = PREP_SECONDS * 60; // 60 ticks per second
        waitingForNextWave = false;
        waveDelayTimer = 0;

        // Init collections
        enemies = new ArrayList<>();
        towers = new ArrayList<>();
        projectiles = new ArrayList<>();
        gameMap = new GameMap(stageNumber, MAP_WIDTH);
        occupiedCells = new boolean[gameMap.getCols()][gameMap.getRows()];
        waveManager = new WaveManager(stageNumber, difficulty,
                gameMap.getWaypoints(), enemies);

        // Load sidebar images
        mandirigmaImg = loadImage("assets/images/enemies_towers/mandirigma.png");
        lantakaImg = loadImage("assets/images/enemies_towers/lantaka_cannon.png");
        bantayImg = loadImage("assets/images/enemies_towers/bantay.png");

        gameTimer = new Timer(16, e -> {
            if (!paused && !gameOver && !gameWon)
                update();
            repaint();
        });

        // Mouse click — tower placement + wave button
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    selectedTowerType = null;
                    return;
                }

                // Check start wave button
                int btnX = MAP_WIDTH + ScreenUtils.scaleX(20);
                int btnY = ScreenUtils.scaleY(800);
                int btnW = SIDEBAR_WIDTH - ScreenUtils.scaleX(40);
                int btnH = ScreenUtils.scaleY(80);
                if (e.getX() >= btnX && e.getX() <= btnX + btnW &&
                        e.getY() >= btnY && e.getY() <= btnY + btnH) {
                    waveManager.startNextWave();
                    return;
                }

                // Check pause button (upper-right of map area)
                int pauseBtnSize = ScreenUtils.scaleX(44);
                int pauseBtnX = MAP_WIDTH - pauseBtnSize - ScreenUtils.scaleX(12);
                int pauseBtnY = ScreenUtils.scaleY(8);
                if (e.getX() >= pauseBtnX && e.getX() <= pauseBtnX + pauseBtnSize &&
                        e.getY() >= pauseBtnY && e.getY() <= pauseBtnY + pauseBtnSize) {
                    paused = true;
                    gameTimer.stop();
                    showPauseMenu();
                    return;
                }

                // Check back to menu click
                if (e.getX() >= MAP_WIDTH + ScreenUtils.scaleX(20) &&
                        e.getY() >= ScreenUtils.scaleY(960)) {
                    returnToMainMenu();
                    return;
                }

                // Check sidebar tower selection
                if (e.getX() >= MAP_WIDTH) {
                    handleSidebarClick(e.getX(), e.getY());
                    return;
                }

                // Place tower on map
                handleMapClick(e.getX(), e.getY());
            }
        });

        // Mouse hover
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                cursorX = e.getX();
                cursorY = e.getY();
                if (e.getX() >= MAP_WIDTH) {
                    hoverCol = -1;
                    hoverRow = -1;
                    return;
                }
                Point cell = gameMap.pixelToCell(e.getX(), e.getY());
                if (cell != null) {
                    hoverCol = cell.x;
                    hoverRow = cell.y;
                }
            }
        });

        // Game loop 60fps
        gameTimer = new Timer(16, e -> {
            if (!gameOver && !gameWon)
                update();
            repaint();
        });
        gameTimer.start();
    }

    // ── Game Loop ──────────────────────────────────────

    private void update() {
        // Always check for win condition first
        if (waveManager.isAllWavesComplete() && enemies.isEmpty()) {
            gameWon = true;
            gameTimer.stop();
            SwingUtilities.invokeLater(this::showVictoryDialog);
            return;
        }

        // Prep phase countdown
        if (prepPhase) {
            prepTimer--;
            if (prepTimer <= 0) {
                prepPhase = false;
                waveManager.startNextWave();
            }
            updateEnemies();
            updateTowers();
            updateProjectiles();
            return;
        }

        // Waiting between waves
        if (waitingForNextWave) {
            waveDelayTimer--;
            if (waveDelayTimer <= 0) {
                waitingForNextWave = false;
                if (!waveManager.isAllWavesComplete()) {
                    waveManager.startNextWave();
                }
            }
            updateEnemies();
            updateTowers();
            updateProjectiles();
            return;
        }

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
            Projectile p = t.update(); // ← update returns projectile when ready
            if (p != null) {
                if (p instanceof LantakaProjectile) {
                    ((LantakaProjectile) p).setAllEnemies(enemies);
                }
                projectiles.add(p);
            }
        }
    }

    private void updateProjectiles() {
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update();
            if (!p.isAlive()) {
                // Award gold if enemy killed
                if (p.getTarget() != null && !p.getTarget().isAlive()) {
                    gold += p.getTarget().getReward();
                    score += getScoreForEnemy(p.getTarget());
                }
                it.remove();
            }
        }
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
        // If all waves spawned and all enemies dead — win immediately
        if (waveManager.isAllWavesComplete() && enemies.isEmpty()) {
            gameWon = true;
            gameTimer.stop();
            SwingUtilities.invokeLater(this::showVictoryDialog);
            return;
        }

        // Between waves — start countdown only if not the last wave
        boolean isLastWave = waveManager.getCurrentWave() >= waveManager.getTotalWaves();
        if (!waveManager.isWaveInProgress() && enemies.isEmpty()
                && !waveManager.isAllWavesComplete()
                && !waitingForNextWave && !prepPhase && !isLastWave) {
            waitingForNextWave = true;
            waveDelayTimer = WAVE_DELAY_SECONDS * 60;
        }
    }

    private void checkGameOver() {
        if (lives <= 0) {
            lives = 0;
            gameOver = true;
            gameTimer.stop();
            SwingUtilities.invokeLater(this::showGameOverDialog);
        }
    }

    private void showVictoryDialog() {
        dbManager.saveGameProgress(username, stageNumber, difficulty, score);
        JOptionPane.showMessageDialog(this,
                "Stage " + stageNumber + " Cleared!\nScore: " + score +
                        "\nLives remaining: " + lives,
                "Victory!", JOptionPane.INFORMATION_MESSAGE);
        returnToMainMenu();
    }

    private void showGameOverDialog() {
        JOptionPane.showMessageDialog(this,
                "Game Over!\nScore: " + score, "Game Over", JOptionPane.ERROR_MESSAGE);
        returnToMainMenu();
    }

    private void returnToMainMenu() {
        gameTimer.stop();
        int unlockedStage = dbManager.getUnlockedStage(username);
        App.getInstance().addPanel(new MainMenuPanel(username, unlockedStage), "mainmenu");
        App.getInstance().showPanel("mainmenu");
    }

    // ── Input ──────────────────────────────────────────

    private void handleSidebarClick(int mx, int my) {
        int x = MAP_WIDTH + ScreenUtils.scaleX(20);
        int btnH = ScreenUtils.scaleY(110);
        int startY = ScreenUtils.scaleY(270);
        int spacing = ScreenUtils.scaleY(140);

        if (mx >= x && mx <= MAP_WIDTH + SIDEBAR_WIDTH - ScreenUtils.scaleX(20)) {
            if (my >= startY && my <= startY + btnH)
                selectedTowerType = "mandirigma";
            else if (my >= startY + spacing && my <= startY + spacing + btnH)
                selectedTowerType = "lantaka";
            else if (my >= startY + spacing * 2 && my <= startY + spacing * 2 + btnH)
                selectedTowerType = "bantay";
        }
    }

    private void handleMapClick(int mx, int my) {
        if (selectedTowerType == null)
            return;
        Point cell = gameMap.pixelToCell(mx, my);
        if (cell == null)
            return;

        int col = cell.x, row = cell.y;
        if (!gameMap.canPlaceTower(col, row, occupiedCells))
            return;

        Tower tower = createTower(selectedTowerType, col, row);
        if (tower == null)
            return;

        if (gold < tower.getCost()) {
            JOptionPane.showMessageDialog(this, "Not enough sampaguita!",
                    "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (Tower t : towers) {
            double dx = mx - t.getX();
            double dy = my - t.getY();
            if (Math.sqrt(dx * dx + dy * dy) <= gameMap.getCellSize() / 2) {
                selectedPlacedTower = (selectedPlacedTower == t) ? null : t;
                return;
            }
        }
        // deselect if clicking empty cell
        if (selectedTowerType == null)
            selectedPlacedTower = null;

        gold -= tower.getCost();
        occupiedCells[col][row] = true;
        towers.add(tower);
        selectedTowerType = null;
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

    // ── Drawing ────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        gameMap.draw(g2d);
        drawStageInfo(g2d);
        drawPauseButton(g2d);

        // Hover highlight
        if (hoverCol >= 0 && selectedTowerType != null) {
            boolean canPlace = gameMap.canPlaceTower(hoverCol, hoverRow, occupiedCells);
            gameMap.drawHoverCell(g2d, hoverCol, hoverRow, canPlace);
        }
        // Draw tower ghost following cursor
        if (selectedTowerType != null && cursorX < MAP_WIDTH) {
            drawTowerGhost(g2d, cursorX, cursorY);
        }

        // Draw range of selected placed tower
        if (selectedPlacedTower != null) {
            drawRangeCircle(g2d, (int) selectedPlacedTower.getX(),
                    (int) selectedPlacedTower.getY(),
                    (int) selectedPlacedTower.getRange(),
                    new Color(255, 255, 255, 40),
                    new Color(255, 255, 255, 120));
        }

        // Draw range preview when hovering with selected tower
        if (selectedTowerType != null && cursorX < MAP_WIDTH) {
            drawRangeCircle(g2d, cursorX, cursorY,
                    (int) getTowerRange(selectedTowerType),
                    new Color(100, 200, 255, 30),
                    new Color(100, 200, 255, 100));
            drawTowerGhost(g2d, cursorX, cursorY);
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

        drawSidebar(g2d);
        g2d.dispose();
    }

    private void drawRangeCircle(Graphics2D g2d, int cx, int cy,
            int radius, Color fill, Color border) {
        g2d.setColor(fill);
        g2d.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        g2d.setColor(border);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    private void drawTowerGhost(Graphics2D g2d, int mx, int my) {
        BufferedImage img = null;
        switch (selectedTowerType) {
            case "mandirigma":
                img = mandirigmaImg;
                break;
            case "lantaka":
                img = lantakaImg;
                break;
            case "bantay":
                img = bantayImg;
                break;
        }
        if (img == null)
            return;

        int size = ScreenUtils.scaleX(52);
        Composite original = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.drawImage(img, mx - size / 2, my - size / 2, size, size, null);
        g2d.setComposite(original);
    }

    private void showPauseMenu() {
        String[] options = { "Resume", "Restart", "Main Menu", "Exit Game" };
        int choice = JOptionPane.showOptionDialog(this,
                "Game Paused", "PAUSE",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        switch (choice) {
            case 0:
                paused = false;
                gameTimer.start(); // ← restart timer
                requestFocusInWindow();
                break;
            case 1:
                restartGame();
                break;
            case 2:
                returnToMainMenu();
                break;
            case 3:
                System.exit(0);
                break;
            default:
                paused = false;
                gameTimer.start();
                break;
        }
    }

    private void restartGame() {
        gameTimer.stop();
        App.getInstance().addPanel(
                new GamePanel(username, stageNumber, difficulty), "game");
        App.getInstance().showPanel("game");
    }

    private void drawStageInfo(Graphics2D g2d) {
        int barHeight = gameMap.getOffsetY();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, MAP_WIDTH, barHeight);

        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(28)));
        g2d.setColor(new Color(180, 140, 40));
        g2d.drawString("STAGE " + stageNumber,
                ScreenUtils.scaleX(20), barHeight - ScreenUtils.scaleY(35));

        g2d.setFont(new Font("Serif", Font.ITALIC, ScreenUtils.scaleFont(22)));
        g2d.setColor(new Color(200, 180, 120));
        g2d.drawString(STAGE_NAMES[stageNumber],
                ScreenUtils.scaleX(20), barHeight - ScreenUtils.scaleY(10));

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
        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(22)));
        g2d.setColor(diffColor);
        String diff = difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1);
        String label = "[ " + diff + " ]";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(label,
                MAP_WIDTH - fm.stringWidth(label) - ScreenUtils.scaleX(20),
                barHeight - ScreenUtils.scaleY(20));
    }

    private void drawSidebar(Graphics2D g2d) {
        int sx = MAP_WIDTH;

        g2d.setColor(new Color(15, 10, 5, 230));
        g2d.fillRect(sx, 0, SIDEBAR_WIDTH, ScreenUtils.HEIGHT);

        g2d.setColor(new Color(180, 140, 40));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(sx, 0, sx, ScreenUtils.HEIGHT);

        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(22)));
        g2d.setColor(new Color(220, 180, 60));
        g2d.drawString("Wave: " + waveManager.getCurrentWave()
                + "/" + waveManager.getTotalWaves(),
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(60));

        g2d.setColor(new Color(220, 80, 80));
        g2d.drawString("Lives: " + lives,
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(100));

        g2d.setColor(new Color(255, 210, 60));
        g2d.drawString("Sampaguita: " + gold,
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(140));

        g2d.setColor(new Color(180, 220, 140));
        g2d.drawString("Score: " + score,
                sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(180));

        g2d.setColor(new Color(180, 140, 40, 120));
        g2d.fillRect(sx + ScreenUtils.scaleX(10), ScreenUtils.scaleY(200),
                SIDEBAR_WIDTH - ScreenUtils.scaleX(20), 2);

        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(18)));
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawString("TOWERS", sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(250));

        drawTowerSlot(g2d, mandirigmaImg, "Mandirigma",
                "Cost: 75g", sx, ScreenUtils.scaleY(270), "mandirigma");
        drawTowerSlot(g2d, lantakaImg, "Lantaka Cannon",
                "Cost: 150g", sx, ScreenUtils.scaleY(410), "lantaka");
        drawTowerSlot(g2d, bantayImg, "Bantay-Bantayan",
                "Cost: 100g", sx, ScreenUtils.scaleY(550), "bantay");

        drawStartWaveButton(g2d, sx);

        // Selected tower hint
        if (selectedTowerType != null) {
            g2d.setFont(new Font("SansSerif", Font.PLAIN, ScreenUtils.scaleFont(13)));
            g2d.setColor(new Color(100, 220, 100));
            g2d.drawString("Click map to place",
                    sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(740));
            g2d.drawString("Right-click to cancel",
                    sx + ScreenUtils.scaleX(20), ScreenUtils.scaleY(760));
        }

    }

    private void drawTowerSlot(Graphics2D g2d, BufferedImage img,
            String name, String cost, int sx, int y, String type) {
        int btnW = SIDEBAR_WIDTH - ScreenUtils.scaleX(40);
        int btnH = ScreenUtils.scaleY(110);
        int x = sx + ScreenUtils.scaleX(20);

        boolean selected = type.equals(selectedTowerType);

        g2d.setColor(selected ? new Color(60, 50, 20, 220)
                : new Color(25, 20, 10, 200));
        g2d.fillRoundRect(x, y, btnW, btnH, 10, 10);

        g2d.setColor(selected ? new Color(220, 180, 60)
                : new Color(120, 90, 30, 150));
        g2d.setStroke(new BasicStroke(selected ? 2 : 1));
        g2d.drawRoundRect(x, y, btnW, btnH, 10, 10);

        if (img != null) {
            g2d.drawImage(img, x + ScreenUtils.scaleX(5),
                    y + ScreenUtils.scaleY(10),
                    ScreenUtils.scaleX(80), ScreenUtils.scaleY(80), null);
        }

        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(16)));
        g2d.setColor(new Color(220, 200, 140));
        g2d.drawString(name, x + ScreenUtils.scaleX(100),
                y + ScreenUtils.scaleY(40));

        g2d.setFont(new Font("SansSerif", Font.PLAIN, ScreenUtils.scaleFont(13)));
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawString(cost, x + ScreenUtils.scaleX(100),
                y + ScreenUtils.scaleY(65));
    }

    private double getTowerRange(String type) {
        switch (type) {
            case "mandirigma":
                return ScreenUtils.scaleX(210);
            case "lantaka":
                return ScreenUtils.scaleX(175);
            case "bantay":
                return ScreenUtils.scaleX(350);
            default:
                return ScreenUtils.scaleX(200);
        }
    }

    private void drawPauseButton(Graphics2D g2d) {
        int size = ScreenUtils.scaleX(44);
        int x = MAP_WIDTH - size - ScreenUtils.scaleX(12);
        int y = ScreenUtils.scaleY(8);

        // Button background
        g2d.setColor(new Color(20, 15, 8, 200));
        g2d.fillRoundRect(x, y, size, size, 10, 10);

        // Border — gold tint
        g2d.setColor(new Color(180, 140, 40, 200));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(x, y, size, size, 10, 10);

        // Pause icon — two vertical bars
        int barW = ScreenUtils.scaleX(5);
        int barH = ScreenUtils.scaleY(18);
        int barY = y + (size - barH) / 2;
        int gap = ScreenUtils.scaleX(5);
        int totalW = barW * 2 + gap;
        int barX1 = x + (size - totalW) / 2;
        int barX2 = barX1 + barW + gap;

        g2d.setColor(new Color(220, 200, 140));
        g2d.fillRoundRect(barX1, barY, barW, barH, 3, 3);
        g2d.fillRoundRect(barX2, barY, barW, barH, 3, 3);
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

        String label;
        if (waveManager.isAllWavesComplete()) {
            label = "All Waves Done!";
        } else if (prepPhase) {
            label = "Starting in " + (prepTimer / 60 + 1) + "s...";
        } else if (waitingForNextWave) {
            label = "Next wave in " + (waveDelayTimer / 60 + 1) + "s...";
        } else if (waveManager.isWaveInProgress()) {
            label = "Wave in Progress...";
        } else {
            label = "▶  Start Wave " + (waveManager.getCurrentWave() + 1);
        }

        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(label,
                btnX + (btnW - fm.stringWidth(label)) / 2,
                btnY + (btnH + fm.getAscent()) / 2 - 4);
    }
}