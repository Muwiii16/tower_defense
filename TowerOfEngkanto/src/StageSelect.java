import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class StageSelect extends JFrame {
    private BufferedImage backgroundImage;
    private String username;
    private int unlockedStage;

    public StageSelect(String username, int unlockedStage) {
        this.username = username;
        this.unlockedStage = unlockedStage;

        setTitle("Select Stage");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);

        try {
            backgroundImage = ImageIO.read(new File("assets/images/background.png"));
        } catch (IOException e) {
            System.err.println("Could not load background: " + e.getMessage());
        }

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                }

                g.setColor(new Color(0, 0, 0, 120));
                g.fillRect(0, 0, getWidth(), getHeight());

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setFont(new Font("Serif", Font.BOLD, 80));
                String title = "Select Stage";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(title)) / 2;
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.drawString(title, x + 2, 155 + 2);
                g2d.setColor(new Color(220, 245, 255));
                g2d.drawString(title, x, 150);
            }
        };
        panel.setLayout(null);
        setContentPane(panel);

        int btnWidth = 600;
        int btnHeight = (int) (600 / 3.74);
        int btnX = (1920 - btnWidth) / 2;

        JButton stage1Btn = createStageButton("stg1_btn.png", btnX, 300, btnWidth, btnHeight, false, Color.GREEN);
        JButton stage2Btn = createStageButton(unlockedStage >= 2 ? "stg2_btn.png" : "stg2_btn_lck.png", btnX, 500,
                btnWidth, btnHeight, unlockedStage < 2, Color.MAGENTA);
        JButton stage3Btn = createStageButton(unlockedStage >= 3 ? "stg3_btn.png" : "stg3_btn_lck.png", btnX,
                700, btnWidth, btnHeight, unlockedStage < 3, Color.RED);

        panel.add(stage1Btn);
        panel.add(stage2Btn);
        panel.add(stage3Btn);
    }

    private JButton createStageButton(String imagePath, int x, int y, int width, int height, boolean locked,
            Color glowColor) {

        BufferedImage stageImg;
        try {
            stageImg = ImageIO.read(new File("assets/images/stages/buttons/" + imagePath));
        } catch (IOException e) {
            System.err.println("Could not load stage button image: " + e.getMessage());
            stageImg = null;
        }

        final BufferedImage finalImg = stageImg;

        JButton btn = new JButton() {
            boolean hovered = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        if (!locked) {
                            hovered = true;
                            repaint();
                        }
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (finalImg != null) {
                    g2d.drawImage(finalImg, 0, 0, getWidth(), getHeight(), null);
                }

                if (hovered) {
                    g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 150));
                    g2d.setStroke(new BasicStroke(6));
                    g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 20, 20);
                }
                g2d.dispose();
            }
        };

        btn.setBounds(x, y, width, height);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        if (!locked)
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        return btn;
    }
}
