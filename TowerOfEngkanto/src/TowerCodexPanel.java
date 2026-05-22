import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TowerCodexPanel extends BasePanel {

    public TowerCodexPanel(String username) {
        super("assets/images/background.png", username);
    }

    @Override
    public String getPanelName() {
        return "towercodex";
    }

    @Override
    protected void initComponents() {

        String[][] towers = {
                { "Mandirigma", "75 Gold", "15 DMG", "Fast (0.5s)", "Medium (3 tiles)", "First",
                        "Shredder: High physical DPS; melts large single health pools.",
                        "assets/images/enemies_towers/mandirigma.png" },
                { "Lantaka Cannon", "150 Gold", "40 DMG", "Very Slow (2.0s)", "Short-Medium (2.5 tiles)", "Strongest",
                        "Heavy Splash: Deals damage in 1.5-tile radius. Cannot hit flying units.",
                        "assets/images/enemies_towers/lantaka_cannon.png" },
                { "Bantay-Bantayan", "100 Gold", "25 DMG", "Slow (1.2s)", "Long (5 tiles)", "Flying First",
                        "Flak Shot: 2x damage vs Flying units and ignores Evasion traits.",
                        "assets/images/enemies_towers/bantay.png" }
        };

        int cardWidth = ScreenUtils.scaleX(1600);
        int cardHeight = ScreenUtils.scaleY(220);
        int cardX = (ScreenUtils.WIDTH - cardWidth) / 2;
        int startY = ScreenUtils.scaleY(180);
        int spacing = ScreenUtils.scaleY(240);

        for (int i = 0; i < towers.length; i++) {
            JPanel card = createTowerCard(towers[i], cardWidth, cardHeight);
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

    private JPanel createTowerCard(String[] data, int width, int height) {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 160));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(new Color(80, 160, 220, 200));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                g2d.dispose();
            }
        };
        card.setOpaque(false);

        int imgSize = height - ScreenUtils.scaleY(20);
        BufferedImage img = loadImage(data[7]);
        if (img != null) {
            JLabel imgLabel = new JLabel(new ImageIcon(
                    img.getScaledInstance(imgSize, imgSize, Image.SCALE_SMOOTH)));
            imgLabel.setBounds(ScreenUtils.scaleX(20), ScreenUtils.scaleY(10), imgSize, imgSize);
            card.add(imgLabel);
        }

        int textX = imgSize + ScreenUtils.scaleX(40);
        int textY = ScreenUtils.scaleY(30);
        int lineH = ScreenUtils.scaleY(38);

        // Name
        JLabel nameLabel = new JLabel(data[0]);
        nameLabel.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(28)));
        nameLabel.setForeground(new Color(80, 180, 255));
        nameLabel.setBounds(textX, textY, width - textX - ScreenUtils.scaleX(20), lineH);
        card.add(nameLabel);

        // Stats
        String[] leftStats = {
                "💰  Cost: " + data[1],
                "⚔  Damage: " + data[2],
                "⚡  Atk Speed: " + data[3],
                "🎯  Range: " + data[4],
        };
        Color[] leftColors = {
                new Color(255, 210, 80),
                new Color(220, 80, 80),
                new Color(80, 200, 220),
                new Color(140, 220, 140),
                new Color(200, 160, 255)
        };

        for (int i = 0; i < leftStats.length; i++) {
            JLabel stat = new JLabel(leftStats[i]);
            stat.setFont(new Font("SansSerif", Font.PLAIN, ScreenUtils.scaleFont(18)));
            stat.setForeground(leftColors[i]);
            stat.setBounds(textX, textY + lineH + lineH * i, ScreenUtils.scaleX(600), lineH);
            card.add(stat);
        }

        int rightX = textX + ScreenUtils.scaleX(620);
        int rightW = width - rightX - ScreenUtils.scaleX(20);

        JLabel priorityText = new JLabel("🎯  Target Priority: " + data[5]);
        priorityText.setFont(new Font("SansSerif", Font.PLAIN, ScreenUtils.scaleFont(16)));
        priorityText.setForeground(new Color(200, 160, 255));
        priorityText.setBounds(rightX, textY + lineH, rightW, lineH);
        card.add(priorityText);

        JLabel traitTitle = new JLabel("✦  Special Trait");
        traitTitle.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(18)));
        traitTitle.setForeground(new Color(180, 220, 140));
        traitTitle.setBounds(rightX, textY + lineH + 38, rightW, lineH);
        card.add(traitTitle);

        JLabel traitText = new JLabel("<html>" + data[6] + "</html>");
        traitText.setFont(new Font("SansSerif", Font.PLAIN, ScreenUtils.scaleFont(16)));
        traitText.setForeground(new Color(200, 200, 200));
        traitText.setBounds(rightX + 30, textY + lineH * 2, rightW, lineH * 3);
        card.add(traitText);

        return card;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTitle((Graphics2D) g, "TOWERS", ScreenUtils.scaleY(130));
    }
}