package app;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class BasePanel extends JPanel {
    protected BufferedImage backgroundImage;
    protected String username;

    public BasePanel(String backgroundPath, String username) {
        this.username = username;
        setLayout(null);
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(ScreenUtils.WIDTH, ScreenUtils.HEIGHT));

        try {
            backgroundImage = ImageIO.read(new File(backgroundPath));
        } catch (IOException e) {
            System.err.println("Could not load background: " + backgroundPath);
            backgroundImage = null;
        }

        initComponents();
    }

    protected abstract void initComponents();

    public abstract String getPanelName();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    protected void drawTitle(Graphics2D g2d, String titleImagePath, int y) {
        BufferedImage titleImg = loadImage(titleImagePath);
        if (titleImg != null) {
            int imgWidth = ScreenUtils.scaleX(800);
            int imgHeight = (int) (imgWidth * (titleImg.getHeight() / (double) titleImg.getWidth()));
            int imgX = (ScreenUtils.WIDTH - imgWidth) / 2;
            g2d.drawImage(titleImg, imgX, y - imgHeight / 2, imgWidth, imgHeight, null);
        }
    }

    protected JButton createImageButton(String normalPath, String hoverPath, int x, int y, int width, int height) {
        BufferedImage normalImg = loadImage(normalPath);
        BufferedImage hoverImg = loadImage(hoverPath);

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
                BufferedImage img = hovered && hoverImg != null ? hoverImg : normalImg;
                if (img != null)
                    g2d.drawImage(img, 0, 0, getWidth(), getHeight(), null);
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

    protected JButton createFadeButton(String imagePath, int x, int y, int width, int height) {
        BufferedImage img = loadImage(imagePath);

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
                if (img != null) {
                    float alpha = hovered ? 1.0f : 0.4f;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.drawImage(img, 0, 0, getWidth(), getHeight(), null);
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

    protected BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Could not load image: " + path);
            return null;
        }
    }
}
