package menu;

import app.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EnemyCodexPanel extends BasePanel {
    public EnemyCodexPanel(String username) {
        super("assets/images/background.png", username);
    }

    @Override
    public String getPanelName() {
        return "enemycodex";
    }

    @Override
    protected void initComponents() {
        String[][] enemies = {
                { "Kapre", "250 HP", "Slow (1.0 tiles/sec)", "5 Lives",
                        "Anito's Resolve: Immune to slow/stun effects.",
                        "assets/images/enemies_towers/kapre.png" },
                { "Tikbalang", "85 HP", "Fast (2.5 tiles/sec)", "2 Lives",
                        "Swift Shadow: 30% chance to dodge single-target projectiles.",
                        "assets/images/enemies_towers/tikbalang.png" },
                { "Manananggal", "45 HP", "Normal (1.5 tiles/sec)", "3 Lives",
                        "Skybound: Flying unit. Ignores ground-only attacks.",
                        "assets/images/enemies_towers/manananggal.png" }
        };

        int cardWidth = ScreenUtils.scaleX(1600);
        int cardHeight = ScreenUtils.scaleY(220);
        int cardX = (ScreenUtils.WIDTH - cardWidth) / 2;
        int startY = ScreenUtils.scaleY(180);
        int spacing = ScreenUtils.scaleY(240);

        for (int i = 0; i < enemies.length; i++) {
            JPanel card = createEnemyCard(enemies[i], cardWidth, cardHeight);
            card.setBounds(cardX, startY + spacing * i, cardWidth, cardHeight);
            add(card);
        }

        int btnWidth = ScreenUtils.scaleX(720);
        int btnHeight = (int) (btnWidth * 0.25);
        JButton backBtn = createImageButton(
                "assets/images/buttons/back_btn_def.png",
                "assets/images/buttons/back_btn_hover.png",
                (ScreenUtils.WIDTH - btnWidth) / 2,
                ScreenUtils.scaleY(900), btnWidth, btnHeight);
        backBtn.addActionListener(e -> {
            App.getInstance().showPanel("codex");
        });
        add(backBtn);
    }

    private JPanel createEnemyCard(String[] data, int width, int height) {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Card background
                g2d.setColor(new Color(0, 0, 0, 160));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Card border
                g2d.setColor(new Color(180, 140, 60, 200));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                g2d.dispose();
            }
        };
        card.setOpaque(false);

        int imgSize = height - ScreenUtils.scaleY(20);
        BufferedImage img = loadImage(data[5]);
        if (img != null) {
            JLabel imgLabel = new JLabel(new ImageIcon(
                    img.getScaledInstance(imgSize, imgSize, Image.SCALE_SMOOTH)));
            imgLabel.setBounds(ScreenUtils.scaleX(20), ScreenUtils.scaleY(10), imgSize, imgSize);
            card.add(imgLabel);
        }

        int textX = imgSize + ScreenUtils.scaleX(40);
        int textY = ScreenUtils.scaleY(30);
        int lineH = ScreenUtils.scaleY(38);

        JLabel nameLabel = new JLabel(data[0]);
        nameLabel.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(28)));
        nameLabel.setForeground(new Color(255, 210, 80));
        nameLabel.setBounds(textX, textY, width - textX - ScreenUtils.scaleX(20), lineH);
        card.add(nameLabel);

        String[] stats = {
                "❤  HP: " + data[1],
                "⚡  Speed: " + data[2],
                "💀  Leak Dmg: " + data[3],
                "✦  Ability: " + data[4]
        };
        Color[] colors = {
                new Color(220, 80, 80),
                new Color(80, 200, 220),
                new Color(220, 220, 80),
                new Color(180, 220, 140)
        };

        for (int i = 0; i < stats.length; i++) {
            JLabel stat = new JLabel(stats[i]);
            stat.setFont(new Font("SansSerif", Font.PLAIN, ScreenUtils.scaleFont(18)));
            stat.setForeground(colors[i]);
            stat.setBounds(textX, textY + lineH + lineH * i, width - textX - ScreenUtils.scaleX(20), lineH);
            card.add(stat);
        }

        return card;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTitle((Graphics2D) g, "ENEMIES", ScreenUtils.scaleY(130));
    }
}
