import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MainMenu extends JFrame {
    private BufferedImage backgroundImage;

    public MainMenu() {
        setTitle("Tower of Engkanto");
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
        setContentPane(panel);

        int btnX = (1920 - 300) / 2;

        JButton startBtn = createMenuButton("Start Game", btnX, 400);
        JButton howToBtn = createMenuButton("How to Play", btnX, 475);
        JButton codexBtn = createMenuButton("Codex", btnX, 550);
        JButton exitBtn = createMenuButton("Exit", btnX, 625);

        panel.add(startBtn);
        panel.add(howToBtn);
        panel.add(codexBtn);
        panel.add(exitBtn);
    }

    private JButton createMenuButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 300, 55);
        btn.setFont(new Font("Serif", Font.BOLD, 20));
        btn.setForeground(new Color(210, 230, 245));
        btn.setBackground(new Color(10, 25, 45, 175));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}
