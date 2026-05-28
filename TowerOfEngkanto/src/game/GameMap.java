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
    private BufferedImage shrineImage;
    private int cellSize;
    private int cols;
    private int rows;
    private int mapWidth;
    private int mapHeight;
    private int offsetY; // vertical offset for letterbox
    private boolean[][] pathCells; // true = path (no tower placement)
    private List<Point> waypoints; // pixel coordinates for enemy path
    private int stageNumber;

    public GameMap(int stageNumber, int mapWidth) {
        this.stageNumber = stageNumber;
        this.mapWidth = mapWidth;
        this.mapHeight = (int) (mapWidth * (941.0 / 1672.0));
        this.offsetY = (ScreenUtils.HEIGHT - mapHeight) / 2;
        this.cellSize = mapWidth / 20;
        this.cols = 20;
        this.rows = mapHeight / cellSize;
        this.pathCells = new boolean[cols][rows];
        this.waypoints = new ArrayList<>();

        loadMap();
        buildPath();
    }

    private void loadMap() {
        // Load map image
        try {
            mapImage = ImageIO.read(new File("assets/images/gameplay/maps/map" + stageNumber + ".png"));
        } catch (IOException e) {
            System.err.println("Could not load map: " + stageNumber);
        }

        // Load shrine image
        try {
            shrineImage = ImageIO.read(new File("assets/images/gameplay/shrine.png"));
        } catch (IOException e) {
            System.err.println("Could not load shrine image");
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
        int[][] gridPath = {
                { 0, 8 }, { 1, 8 }, { 2, 8 }, { 3, 8 }, { 4, 8 }, { 5, 8 }, { 6, 8 }, { 7, 8 }, { 8, 8 },
                { 9, 8 }, { 10, 8 }, { 11, 8 }, { 11, 7 }, { 11, 6 }, { 10, 6 }, { 9, 6 }, { 8, 6 },
                { 8, 5 }, { 8, 4 }, { 8, 3 }, { 9, 3 }, { 10, 3 }, { 11, 3 }, { 12, 3 }, { 13, 3 },
                { 13, 2 }, { 13, 1 }, { 13, 0 }
        };

        for (int[] cell : gridPath) {
            int col = cell[0];
            int row = cell[1];
            if (col < cols && row < rows) {
                pathCells[col][row] = true;
            }
            int px = col * cellSize + cellSize / 2;
            int py = row * cellSize + cellSize / 2 + offsetY;
            waypoints.add(new Point(px, py));
        }

        // Blocked cells (water) — not path but also not placeable
        int[][] blockedCells = {
                { 0, 9 }, { 1, 9 }, { 2, 9 }, { 3, 9 }, { 4, 9 }, { 5, 9 }, { 6, 9 }, { 7, 9 }, { 8, 9 },
                { 0, 10 }, { 1, 10 }, { 2, 10 }, { 3, 10 }, { 4, 10 }, { 5, 10 }, { 6, 10 }, { 7, 10 }, { 8, 10 },
                { 9, 10 }
        };
        for (int[] cell : blockedCells) {
            int col = cell[0];
            int row = cell[1];
            if (col < cols && row < rows) {
                pathCells[col][row] = true; // mark as unavailable
            }
        }

    }

    private void buildMap2Path() {
        int[][] gridPath = {
                { 0, 6 }, { 1, 6 }, { 2, 6 }, { 3, 6 }, { 4, 6 }, { 5, 6 }, { 6, 6 }, { 7, 6 }, { 7, 5 },
                { 7, 4 }, { 7, 3 }, { 7, 2 }, { 8, 2 }, { 9, 2 }, { 10, 2 }, { 11, 2 }, { 12, 2 },
                { 12, 3 }, { 12, 4 }, { 12, 5 }, { 12, 6 }, { 13, 6 }, { 14, 6 }, { 15, 6 }, { 16, 6 },
                { 16, 7 }, { 16, 8 }, { 16, 9 }
        };

        for (int[] cell : gridPath) {
            int col = cell[0];
            int row = cell[1];
            if (col < cols && row < rows) {
                pathCells[col][row] = true;
            }
            int px = col * cellSize + cellSize / 2;
            int py = row * cellSize + cellSize / 2 + offsetY;
            waypoints.add(new Point(px, py));
        }

        // Blocked cells — not path but also not placeable
        int[][] blockedCells = {
                { 0, 0 }, { 0, 1 }, { 0, 2 }, { 1, 0 }, { 1, 1 }, { 1, 2 }, { 2, 0 }, { 2, 1 }, { 2, 2 }, { 3, 0 },
                { 3, 1 }, { 3, 2 }, { 4, 0 }, { 4, 1 }, { 4, 2 }, { 19, 0 }, { 19, 1 }, { 19, 2 },
                { 18, 0 }, { 18, 1 }, { 18, 2 }, { 17, 0 }, { 17, 1 }, { 17, 2 }, { 16, 1 }, { 2, 8 }, { 3, 8 },
                { 4, 8 }, { 2, 9 }, { 3, 9 }, { 4, 9 },
                { 17, 8 }, { 18, 8 }, { 19, 8 },
                { 16, 9 }, { 17, 9 }, { 18, 9 }, { 19, 9 },
                { 12, 10 }, { 13, 10 }, { 14, 10 }, { 15, 10 }, { 16, 10 }, { 17, 10 }, { 18, 10 }, { 19, 10 },
        };
        for (int[] cell : blockedCells) {
            int col = cell[0];
            int row = cell[1];
            if (col < cols && row < rows) {
                pathCells[col][row] = true; // mark as unavailable
            }
        }
    }

    private void buildMap3Path() {
        // 1. Define the exact grid coordinates the road passes through
        int[][] gridPath = {
                // Enters from the left
                { 0, 3 }, { 1, 3 }, { 2, 3 }, { 3, 3 },{ 4, 3 },{ 5, 3 },{ 6, 3 },
                // Curves down
                { 6, 4 }, { 6, 5 }, { 6, 6 },{ 6, 7 }, { 6, 8 },
                // Moves right along the bottom
                { 7, 8 }, { 8, 8 },{ 9, 8 }, { 10, 8 },
                // Curves back up
                { 10, 7 }, { 10, 6 }, { 10, 5 }, { 10, 4 },{ 10, 3 }, { 10, 2 },
                // Moves right across the top
                { 11, 2 }, { 12, 2 }, { 13, 2 }, { 14, 2 }, 
                // Dips down slightly
                { 14, 3 }, { 14, 4 },{ 14, 5 }, { 14, 6 },
                // Moves right again
                { 15, 6 }, { 16, 6 }, { 17, 6 }, { 18, 6 },{ 19, 6 },
        };

        // 2. Clear any old waypoints just to be safe
        waypoints.clear();

        // 3. Loop through the array to register the path in the game
        for (int[] cell : gridPath) {
            int c = cell[0];
            int r = cell[1];

            // Make sure the coordinates are safely inside the map grid
            if (c >= 0 && c < cols && r >= 0 && r < rows) {
                // Mark this cell so the player CANNOT place a tower on the road
                pathCells[c][r] = true;

                // Convert the grid coordinate into actual (X, Y) pixels for the enemies
                waypoints.add(cellToPixel(c, r));
            }
        }
    }

    public void draw(Graphics2D g2d) {
        // Black background for letterbox
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, mapWidth, ScreenUtils.HEIGHT);

        // Center map vertically
        int offsetY = (ScreenUtils.HEIGHT - mapHeight) / 2;

        if (mapImage != null) {
            g2d.drawImage(mapImage, 0, offsetY, mapWidth, mapHeight, null);
        }

        // Draw shrine dynamically at the LAST waypoint of the current stage
        if (shrineImage != null && !waypoints.isEmpty()) {
            // 1. Get the very last waypoint (end of the path)
            Point lastWaypoint = waypoints.get(waypoints.size() - 1);

            // 2. Waypoints store the CENTER of the cell. We subtract half the cell size
            // to get the top-left X and Y coordinates needed to draw the image.
            int sx = lastWaypoint.x - cellSize / 2;
            int sy = lastWaypoint.y - cellSize / 2;

            g2d.drawImage(shrineImage, sx, sy, cellSize, cellSize, null);
        }

        drawGrid(g2d, offsetY);
    }

    private void drawGrid(Graphics2D g2d, int offsetY) {

    }

    // Highlight a cell when hovered
    public void drawHoverCell(Graphics2D g2d, int col, int row, boolean canPlace) {
        // Draw faint grid around hover area
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (pathCells[c][r])
                    continue;
                int x = c * cellSize;
                int y = r * cellSize + offsetY;
                g2d.setColor(new Color(255, 255, 255, 8));
                g2d.drawRect(x, y, cellSize, cellSize);
            }
        }

        // Highlight hovered cell
        int x = col * cellSize;
        int y = row * cellSize + offsetY;
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

    public Point pixelToCell(int px, int py) {
        int col = px / cellSize;
        int row = (py - offsetY) / cellSize; // ← subtract offsetY
        if (col >= 0 && col < cols && row >= 0 && row < rows) {
            return new Point(col, row);
        }
        return null;
    }

    public Point cellToPixel(int col, int row) {
        return new Point(
                col * cellSize + cellSize / 2,
                row * cellSize + cellSize / 2 + offsetY // ← add offsetY
        );
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

    public int getOffsetY() {
        return offsetY;
    }
}