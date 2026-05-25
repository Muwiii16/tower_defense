package menu;

import app.*;
import database.DatabaseManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LeaderboardPanel extends BasePanel {
    private DatabaseManager dbManager;

    public LeaderboardPanel(String username) {
        super("assets/images/background.png", username);
    }

    @Override
    public String getPanelName() {
        return "leaderboard";
    }

    @Override
    protected void initComponents() {
        dbManager = new DatabaseManager();

        List<String[]> topPlayers = dbManager.getTopLeaderboard();

        System.out.println("Leaderboard loaded, players found: " + topPlayers.size());

        String[] headers = { "RANK", "USERNAME", "SCORE", "BEST STAGE", "DIFFICULTY", "DATE" };
        int[] colWidths = { 80, 300, 200, 180, 180, 280 };
        int[] colX = new int[headers.length];

        int tableWidth = ScreenUtils.scaleX(1400);
        int tableX = (ScreenUtils.WIDTH - tableWidth) / 2;
        int startY = ScreenUtils.scaleY(220);
        int rowHeight = ScreenUtils.scaleY(60);

        int runningX = tableX + ScreenUtils.scaleX(20);
        for (int i = 0; i < headers.length; i++) {
            colX[i] = runningX;
            runningX += ScreenUtils.scaleX(colWidths[i]);
        }

        JPanel headerPanel = createHeaderRow(headers, colX, colWidths, tableWidth, rowHeight);
        headerPanel.setBounds(tableX, startY, tableWidth, rowHeight);
        add(headerPanel);

        for (int i = 0; i < topPlayers.size(); i++) {
            String[] player = topPlayers.get(i);
            String[] rowData = {
                    "#" + (i + 1),
                    player[0],
                    player[1],
                    "Stage " + player[2],
                    capitalize(player[3]),
                    player[4].substring(0, 10)
            };
            Color rowColor = getRankColor(i);
            JPanel row = createDataRow(rowData, colX, colWidths, tableWidth, rowHeight, rowColor, i);
            row.setBounds(tableX, startY + rowHeight + ScreenUtils.scaleY(5) +
                    (rowHeight + ScreenUtils.scaleY(5)) * i, tableWidth, rowHeight);
            add(row);
        }

        if (topPlayers.isEmpty()) {
            JLabel empty = new JLabel("No scores yet. Be the first to play!", SwingConstants.CENTER);
            empty.setFont(new Font("Serif", Font.ITALIC, ScreenUtils.scaleFont(24)));
            empty.setForeground(new Color(180, 180, 180));
            empty.setBounds(tableX, startY + rowHeight + ScreenUtils.scaleY(20),
                    tableWidth, ScreenUtils.scaleY(60));
            add(empty);
        }

        int btnWidth = ScreenUtils.scaleX(720);
        int btnHeight = (int) (btnWidth * 0.25);
        JButton backBtn = createImageButton(
                "assets/images/buttons/back_btn_def.png",
                "assets/images/buttons/back_btn_hover.png",
                (ScreenUtils.WIDTH - btnWidth) / 2,
                ScreenUtils.scaleY(900), btnWidth, btnHeight);
        backBtn.addActionListener(e -> {
            int unlockedStage = dbManager.getUnlockedStage(username);
            App.getInstance().addPanel(new MainMenuPanel(username, unlockedStage), "mainmenu");
            App.getInstance().showPanel("mainmenu");
        });
        add(backBtn);
    }

    private JPanel createHeaderRow(String[] headers, int[] colX, int[] colWidths,
            int width, int height) {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(60, 40, 10),
                        width, 0, new Color(40, 25, 5));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(new Color(200, 160, 50));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                g2d.dispose();
            }
        };
        panel.setOpaque(false);

        int panelX = colX[0];
        for (int i = 0; i < headers.length; i++) {
            JLabel label = new JLabel(headers[i], SwingConstants.CENTER);
            label.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(18)));
            label.setForeground(new Color(220, 180, 60));
            label.setBounds(colX[i] - panelX, 0, ScreenUtils.scaleX(colWidths[i]), height);
            panel.add(label);
        }
        return panel;
    }

    private JPanel createDataRow(String[] data, int[] colX, int[] colWidths,
            int width, int height, Color rankColor, int index) {
        JPanel panel = new JPanel(null) {
            boolean hovered = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = hovered ? new Color(60, 50, 30, 200) : new Color(10, 8, 5, 180);
                g2d.setColor(base);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(new Color(120, 90, 30, 150));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                g2d.setColor(rankColor);
                g2d.setStroke(new BasicStroke(4));
                g2d.drawLine(3, 8, 3, getHeight() - 8);

                g2d.dispose();
            }
        };
        panel.setOpaque(false);

        int panelX = colX[0];
        for (int i = 0; i < data.length; i++) {
            JLabel label = new JLabel(data[i], SwingConstants.CENTER);
            label.setFont(new Font(i == 0 ? "Serif" : "SansSerif",
                    i == 0 ? Font.BOLD : Font.PLAIN, ScreenUtils.scaleFont(i == 0 ? 20 : 16)));
            label.setForeground(i == 0 ? rankColor : new Color(210, 200, 180));
            label.setBounds(colX[i] - panelX, 0, ScreenUtils.scaleX(colWidths[i]), height);
            panel.add(label);
        }
        return panel;
    }

    private Color getRankColor(int rank) {
        switch (rank) {
            case 0:
                return new Color(255, 215, 0); // Gold
            case 1:
                return new Color(192, 192, 192); // Silver
            case 2:
                return new Color(205, 127, 50); // Bronze
            default:
                return new Color(180, 160, 120); // Default
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty())
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawTitle(g2d, "assets/images/titles/gothic_leaderboard_title.png", ScreenUtils.scaleY(120));
    }
}
