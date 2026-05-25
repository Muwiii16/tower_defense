package game;

import app.ScreenUtils;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class GameMap {

    private BufferedImage mapImage;
    private int cellSize;
    private int cols;
    private int rows;
    private int mapWidth;
    private int mapHeight;
    private boolean[][] pathCells; // true = path (no tower placement)
    private List<Point> waypoints; // pixel coordinates for enemy path
    private int stageNumber;

    public GameMap(int stageNumber) {
        this.stageNumber = stageNumber;
        this.cellSize = ScreenUtils.scaleX(70);
        this.mapWidth = ScreenUtils.scaleX(1400);
        this.mapHeight = ScreenUtils.scaleY(1080);
        this.cols = mapWidth / cellSize;
        this.rows = mapHeight / cellSize;
        this.pathCells = new boolean[cols][rows];
        this.waypoints = new ArrayList<>();

        loadMap();
        buildPath();
    }

    private void loadMap() {
        try {
            mapImage = ImageIO.read(new File("assets/images/maps/map" + stageNumber + ".png"));
        } catch (IOException e) {
            System.err.println("Could not load map: " + stageNumber);
        }
    }

    private void buildPath() {
        switch (stageNumber) {
            case 1:
                buildMap1Path();
                break;
            case 2:
                buildMap2Path();
                break;
            case 3:
                buildMap3Path();
                break;
        }
    }

    private void buildMap1Path() {
        // Path waypoints from our grid analysis (col, row)
        int[][] gridPath = {
                { 0, 8 }, { 1, 8 }, { 2, 8 }, { 3, 8 }, { 4, 8 }, { 5, 8 }, { 6, 8 }, { 7, 8 }, { 8, 8 },
                { 9, 8 }, { 10, 8 }, { 11, 8 }, { 11, 7 }, { 11, 6 }, { 10, 6 }, { 9, 6 }, { 8, 6 },
                { 8, 5 }, { 8, 4 }, { 8, 3 }, { 9, 3 }, { 10, 3 }, { 11, 3 }, { 12, 3 }, { 13, 3 },
                { 13, 2 }, { 13, 1 }, { 13, 0 }
        };

        for (int[] cell : gridPath) {
            int col = cell[0];
            int row = cell[1];

            // Mark as path cell
            if (col < cols && row < rows) {
                pathCells[col][row] = true;
            }

            // Convert to pixel center
            int px = col * cellSize + cellSize / 2;
            int py = row * cellSize + cellSize / 2;
            waypoints.add(new Point(px, py));
        }
    }

    private void buildMap2Path() {
        // TODO: add map2 path waypoints
    }

    private void buildMap3Path() {
        // TODO: add map3 path waypoints
    }

    public void draw(Graphics2D g2d) {
        // Draw map image
        if (mapImage != null) {
            g2d.drawImage(mapImage, 0, 0, mapWidth, mapHeight, null);
        } else {
            g2d.setColor(new Color(30, 30, 30));
            g2d.fillRect(0, 0, mapWidth, mapHeight);
        }

        // Draw grid overlay (semi-transparent)
        drawGrid(g2d);
    }

    private void drawGrid(Graphics2D g2d) {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int x = col * cellSize;
                int y = row * cellSize;

                if (pathCells[col][row]) {
                    // Path cell — no placement allowed
                    g2d.setColor(new Color(255, 0, 0, 0)); // invisible
                } else {
                    // Placeable cell — subtle highlight
                    g2d.setColor(new Color(255, 255, 255, 15));
                    g2d.fillRect(x, y, cellSize, cellSize);
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.drawRect(x, y, cellSize, cellSize);
                }
            }
        }
    }

    // Highlight a cell when hovered
    public void drawHoverCell(Graphics2D g2d, int col, int row, boolean canPlace) {
        int x = col * cellSize;
        int y = row * cellSize;
        if (canPlace) {
            g2d.setColor(new Color(0, 255, 0, 60));
            g2d.fillRect(x, y, cellSize, cellSize);
            g2d.setColor(new Color(0, 255, 0, 150));
            g2d.drawRect(x, y, cellSize, cellSize);
        } else {
            g2d.setColor(new Color(255, 0, 0, 60));
            g2d.fillRect(x, y, cellSize, cellSize);
            g2d.setColor(new Color(255, 0, 0, 150));
            g2d.drawRect(x, y, cellSize, cellSize);
        }
    }

    // Convert mouse pixel position to grid cell
    public Point pixelToCell(int px, int py) {
        int col = px / cellSize;
        int row = py / cellSize;
        if (col >= 0 && col < cols && row >= 0 && row < rows) {
            return new Point(col, row);
        }
        return null;
    }

    // Convert grid cell to pixel center
    public Point cellToPixel(int col, int row) {
        return new Point(
                col * cellSize + cellSize / 2,
                row * cellSize + cellSize / 2);
    }

    // Check if a cell can have a tower placed on it
    public boolean canPlaceTower(int col, int row, boolean[][] occupiedCells) {
        if (col < 0 || col >= cols || row < 0 || row >= rows)
            return false;
        if (pathCells[col][row])
            return false;
        if (occupiedCells[col][row])
            return false;
        return true;
    }

    // ── Getters ──────────────────────────────
    public List<Point> getWaypoints() {
        return waypoints;
    }

    public BufferedImage getMapImage() {
        return mapImage;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public boolean[][] getPathCells() {
        return pathCells;
    }

    public int getStageNumber() {
        return stageNumber;
    }
}