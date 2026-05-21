import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MainMenu extends JFrame {
    private BufferedImage backgroundImage;

    public MainMenu(String username) {
        setTitle("Tower of Engkanto");
        setSize(ScreenUtils.WIDTH, ScreenUtils.HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        System.out.print("Logged in as: " + username);

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

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2d.setFont(new Font("Serif", Font.BOLD, 80));
                String title = "Tower of Engkanto";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(title)) / 2;
                int y = 155;

                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.drawString(title, x + 2, y + 2);

                g2d.setColor(new Color(220, 245, 255));
                g2d.drawString(title, x, y);
            }
        };
        panel.setLayout(null);
        panel.setDoubleBuffered(true);
        setContentPane(panel);

        int btnX = (1920 - 438) / 2;

        int desiredWidth = 400;
        double originalAspect = 4.38;
        int desiredHeight = (int) (desiredWidth / originalAspect);

        JButton startBtn = createMenuButton("start_btn_def.png", "start_btn_hover.png", btnX, 400, desiredWidth,
                desiredHeight);
        JButton howToBtn = createMenuButton("htp_btn_def.png", "htp_btn_hover.png", btnX, 520, desiredWidth,
                desiredHeight);
        JButton codexBtn = createMenuButton("codex_btn_def.png", "codex_btn_hover.png", btnX, 640, desiredWidth,
                desiredHeight);
        JButton ldbBtn = createMenuButton("ldb_btn_def.png", "ldb_btn_hover.png", btnX, 760, desiredWidth,
                desiredHeight);
        JButton exitBtn = createMenuButton("exit_btn_def.png", "exit_btn_hover.png", btnX, 880, desiredWidth,
                desiredHeight);

        exitBtn.addActionListener(e -> {
            System.exit(0);
        });

        startBtn.addActionListener(e -> {
            new StageSelect(username, 1).setVisible(true);
            dispose();
        });

        panel.add(startBtn);
        panel.add(howToBtn);
        panel.add(codexBtn);
        panel.add(ldbBtn);
        panel.add(exitBtn);
    }

    private JButton createMenuButton(String normalPath, String hoverPath, int x, int y, int width, int height) {
        JButton btn = new JButton();

        try {
            BufferedImage normalImg = ImageIO.read(new File("assets/images/buttons/" + normalPath));
            BufferedImage hoverImg = ImageIO.read(new File("assets/images/buttons/" + hoverPath));

            Image normalImgScaled = normalImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            Image hoverImgScaled = hoverImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            btn.setIcon(new ImageIcon(normalImgScaled));
            btn.setRolloverIcon(new ImageIcon(hoverImgScaled));
        } catch (IOException e) {
            System.err.println("Could not load button images: " + e.getMessage());
            btn.setText(normalPath.substring(0, normalPath.indexOf('_')));
            btn.setForeground(Color.RED);
        }

        btn.setBounds(x, y, width, height);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
