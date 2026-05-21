import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DifficultySelect extends JFrame {

    private BufferedImage backgroundImage;
    private String username;
    private int stageNumber;
    private int unlockedStage;

    public DifficultySelect(String username, int stageNumber, int unlockedStage) {
        this.username = username;
        this.stageNumber = stageNumber;
        this.unlockedStage = unlockedStage;

        setTitle("Select Difficulty");
        setSize(ScreenUtils.WIDTH, ScreenUtils.HEIGHT);
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
                String title = "Select Difficulty";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(title)) / 2;
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.drawString(title, x + 2, 152);
                g2d.setColor(new Color(220, 245, 255));
                g2d.drawString(title, x, 150);

                g2d.setFont(new Font("Serif", Font.PLAIN, 36));
                String sub = "Stage " + stageNumber;
                FontMetrics fm2 = g2d.getFontMetrics();
                int sx = (getWidth() - fm2.stringWidth(sub)) / 2;
                g2d.setColor(new Color(200, 200, 200, 200));
                g2d.drawString(sub, sx, 210);
            }
        };
        panel.setLayout(null);
        panel.setDoubleBuffered(true);
        setContentPane(panel);

        int btnWidth = 550;
        int btnHeight = (int) (550 / 5.69);
        int btnX = (1920 - btnWidth) / 2;

        JButton easyBtn = createDifficultyButton("easy_btn.png", btnX, 320, btnWidth,
                btnHeight);
        JButton mediumBtn = createDifficultyButton("normal_btn.png", btnX, 500, btnWidth,
                btnHeight);
        JButton hardBtn = createDifficultyButton("hard_btn.png", btnX, 680, btnWidth,
                btnHeight);
        JButton backBtn = createBackButton(btnX, 860, btnWidth, btnHeight);

        easyBtn.addActionListener(e -> startGame("easy"));
        mediumBtn.addActionListener(e -> startGame("medium"));
        hardBtn.addActionListener(e -> startGame("hard"));
        backBtn.addActionListener(e -> {
            new StageSelect(username, unlockedStage).setVisible(true);
            dispose();
        });
        panel.add(easyBtn);
        panel.add(mediumBtn);
        panel.add(hardBtn);
        panel.add(backBtn);
    }

    private JButton createDifficultyButton(String imagePath, int x, int y, int width, int height) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File("assets/images/stages/buttons/" + imagePath));
        } catch (IOException e) {
            System.err.println("Could not load button: " + e.getMessage());
            img = null;
        }

        final BufferedImage finalImg = img;

        JButton btn = new JButton() {
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
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (finalImg != null) {
                    float alpha = hovered ? 1.0f : 0.4f;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.drawImage(finalImg, 0, 0, getWidth(), getHeight(), null);
                }
                g2d.dispose();
            }
        };
        btn.setBounds(x, y, width, height);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void startGame(String difficulty) {
        System.out.println("Starting Stage " + stageNumber + "on " + difficulty);
        JOptionPane.showMessageDialog(this,
                "Starting Stage " + stageNumber + " - " + difficulty + "!\n(Game screen coming soon)");
    }

    private JButton createBackButton(int x, int y, int width, int height) {
        BufferedImage normalImg;
        BufferedImage hoverImg;
        try {
            normalImg = ImageIO.read(new File("assets/images/buttons/back_btn_def.png"));
            hoverImg = ImageIO.read(new File("assets/images/buttons/back_btn_hover.png"));
        } catch (IOException e) {
            System.err.println("Could not load back button images: " + e.getMessage());
            return new JButton("Back");
        }

        final BufferedImage finalNormal = normalImg;
        final BufferedImage finalHover = hoverImg;

        JButton btn = new JButton() {
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
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(hovered ? finalHover : finalNormal, 0, 0, getWidth(), getHeight(), null);
                g2d.dispose();
            }
        };
        btn.setBounds(x, y, width, height);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
