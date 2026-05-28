package game;

import game.enemies.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class WaveManager {

    private int stageNumber;
    private String difficulty;
    private List<Point> waypoints;
    private List<Enemy> enemies;

    private int currentWave;
    private int totalWaves;
    private int spawnTimer;
    private int spawnIndex; // which enemy in the queue to spawn next
    private List<String> spawnQueue; // enemy types to spawn this wave
    private boolean waveInProgress;
    private boolean allWavesComplete;

    public WaveManager(int stageNumber, String difficulty,
            List<Point> waypoints, List<Enemy> enemies) {
        this.stageNumber = stageNumber;
        this.difficulty = difficulty;
        this.waypoints = waypoints;
        this.enemies = enemies;
        this.currentWave = 0;
        this.totalWaves = 5; // 5 waves per stage
        this.spawnTimer = 0;
        this.spawnIndex = 0;
        this.spawnQueue = new ArrayList<>();
        this.waveInProgress = false;
        this.allWavesComplete = false;
    }

    public void update() {
        if (!waveInProgress || allWavesComplete)
            return;

        // Spawn enemies from queue
        if (spawnIndex < spawnQueue.size()) {
            spawnTimer++;

            // 1. Look at the NEXT enemy in the queue
            String nextEnemyType = spawnQueue.get(spawnIndex);

            // 2. Get the specific delay required for this enemy type
            int requiredDelay = getSpawnDelay(nextEnemyType);

            // 3. Check if we've waited long enough for this specific enemy
            if (spawnTimer >= requiredDelay) {
                spawnTimer = 0;
                spawnEnemy(nextEnemyType);
                spawnIndex++;
            }
        } else {
            // All enemies spawned for this wave
            waveInProgress = false;
            if (currentWave >= totalWaves) {
                allWavesComplete = true;
            }
        }
    }

    // Call this when player clicks "Start Wave"
    public void startNextWave() {
        if (waveInProgress || allWavesComplete)
            return;
        currentWave++;
        if (currentWave > totalWaves) {
            allWavesComplete = true;
            return;
        }
        spawnQueue = buildWaveQueue(currentWave);
        spawnIndex = 0;
        spawnTimer = 0;
        waveInProgress = true;
        System.out.println("Wave " + currentWave + " started!");
    }

    private List<String> buildWaveQueue(int wave) {
        List<String> queue = new ArrayList<>();
        switch (stageNumber) {
            case 1:
                buildStage1Wave(queue, wave);
                break;
            case 2:
                buildStage2Wave(queue, wave);
                break;
            case 3:
                buildStage3Wave(queue, wave);
                break;
        }
        return queue;
    }

    private void buildStage1Wave(List<String> queue, int wave) {
        // Stage 1 — Gubat ng mga Nilalang
        // Mostly Tikbalang and Kapre, harder as waves progress
        switch (wave) {
            case 1:
                addEnemies(queue, "tikbalang", 5);
                break;
            case 2:
                addEnemies(queue, "tikbalang", 5);
                addEnemies(queue, "kapre", 2);
                break;
            case 3:
                addEnemies(queue, "tikbalang", 6);
                addEnemies(queue, "kapre", 3);
                addEnemies(queue, "manananggal", 2);
                break;
            case 4:
                addEnemies(queue, "kapre", 4);
                addEnemies(queue, "manananggal", 3);
                addEnemies(queue, "tikbalang", 4);
                break;
            case 5:
                addEnemies(queue, "kapre", 5);
                addEnemies(queue, "manananggal", 4);
                addEnemies(queue, "tikbalang", 6);
                break;
        }
    }

    private void buildStage2Wave(List<String> queue, int wave) {
        // Stage 2 — Bundok ng Sumpa (harder)
        switch (wave) {
            case 1:
                addEnemies(queue, "tikbalang", 6);
                addEnemies(queue, "kapre", 2);
                break;
            case 2:
                addEnemies(queue, "tikbalang", 6);
                addEnemies(queue, "kapre", 3);
                addEnemies(queue, "manananggal", 2);
                break;
            case 3:
                addEnemies(queue, "kapre", 4);
                addEnemies(queue, "manananggal", 4);
                addEnemies(queue, "tikbalang", 5);
                break;
            case 4:
                addEnemies(queue, "kapre", 5);
                addEnemies(queue, "manananggal", 5);
                addEnemies(queue, "tikbalang", 6);
                break;
            case 5:
                addEnemies(queue, "kapre", 6);
                addEnemies(queue, "manananggal", 6);
                addEnemies(queue, "tikbalang", 8);
                break;
        }
    }

    private void buildStage3Wave(List<String> queue, int wave) {
        // Stage 3 — Tore ng Engkanto (hardest)
        switch (wave) {
            case 1:
                addEnemies(queue, "kapre", 3);
                addEnemies(queue, "manananggal", 3);
                addEnemies(queue, "tikbalang", 6);
                break;
            case 2:
                addEnemies(queue, "kapre", 4);
                addEnemies(queue, "manananggal", 4);
                addEnemies(queue, "tikbalang", 8);
                break;
            case 3:
                addEnemies(queue, "kapre", 5);
                addEnemies(queue, "manananggal", 5);
                addEnemies(queue, "tikbalang", 8);
                break;
            case 4:
                addEnemies(queue, "kapre", 6);
                addEnemies(queue, "manananggal", 6);
                addEnemies(queue, "tikbalang", 10);
                break;
            case 5:
                addEnemies(queue, "kapre", 8);
                addEnemies(queue, "manananggal", 8);
                addEnemies(queue, "tikbalang", 10);
                break;
        }
    }

    private void addEnemies(List<String> queue, String type, int count) {
        for (int i = 0; i < count; i++)
            queue.add(type);
    }

    private void spawnEnemy(String type) {
        // Spawn at first waypoint
        Point start = waypoints.get(0);
        Enemy e;
        switch (type) {
            case "kapre":
                e = new Kapre(start.x, start.y, waypoints, difficulty);
                break;
            case "tikbalang":
                e = new Tikbalang(start.x, start.y, waypoints, difficulty);
                break;
            case "manananggal":
                e = new Manananggal(start.x, start.y, waypoints, difficulty);
                break;
            default:
                e = new Tikbalang(start.x, start.y, waypoints, difficulty);
        }
        enemies.add(e);
    }

    // Check if wave is done AND all enemies are dead
    public boolean isWaveCleared() {
        if (waveInProgress)
            return false;
        for (Enemy e : enemies) {
            if (e.isAlive())
                return false;
        }
        return true;
    }

    private int getSpawnDelay(String enemyType) {
        switch (enemyType) {
            case "tikbalang":
                return 40; // Quick spawn (~0.6 seconds) to keep them grouped
            case "kapre":
                return 160; // Slow spawn (~2.6 seconds) to space them out
            case "manananggal":
                return 90; // Your current default (~1.5 seconds)
            default:
                return 90; // Fallback just in case
        }
    }

    // ── Getters ──────────────────────────────
    public int getCurrentWave() {
        return currentWave;
    }

    public int getTotalWaves() {
        return totalWaves;
    }

    public boolean isWaveInProgress() {
        return waveInProgress;
    }

    public boolean isAllWavesComplete() {
        return allWavesComplete;
    }
}