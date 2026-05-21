import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StageSelectPanel extends BasePanel {

    private int unlockedStage;

    public StageSelectPanel(String username, int unlockedStage) {
        super("assets/images/background.png", username);
        this.unlockedStage = unlockedStage;
    }

    @Override
    public String getPanelName() {
        return "stageselect";
    }

    @Override
    protected void initComponents() {
        int btnWidth = ScreenUtils.scaleX(600);
        int btnHeight = ScreenUtils.scaleY(160);
        int btnX = (ScreenUtils.WIDTH - btnWidth) / 2;

        JButton stage1Btn = createStageButton("stg1_btn.png", btnX, ScreenUtils.scaleY(300),
                btnWidth, btnHeight, false, Color.GREEN);
        JButton stage2Btn = createStageButton(unlockedStage >= 2 ? "stg2_btn.png" : "stg2_btn_lck.png",
                btnX, ScreenUtils.scaleY(500), btnWidth, btnHeight, unlockedStage < 2, Color.MAGENTA);
        JButton stage3Btn = createStageButton(unlockedStage >= 3 ? "stg3_btn.png" : "stg3_btn_lck.png",
                btnX, ScreenUtils.scaleY(700), btnWidth, btnHeight, unlockedStage < 3, Color.RED);
        JButton backBtn = createImageButton("assets/images/buttons/back_btn_def.png",
                "assets/images/buttons/back_btn_hover.png", btnX, ScreenUtils.scaleY(870),
                btnWidth, btnHeight);

        stage1Btn.addActionListener(e -> {
            App.getInstance().addPanel(new DifficultySelectPanel(username, 1, unlockedStage), "difficultyselect");
            App.getInstance().showPanel("difficultyselect");
        });
        stage2Btn.addActionListener(e -> {
            if (unlockedStage >= 2) {
                App.getInstance().addPanel(new DifficultySelectPanel(username, 2, unlockedStage), "difficultyselect");
                App.getInstance().showPanel("difficultyselect");
            }
        });
        stage3Btn.addActionListener(e -> {
            if (unlockedStage >= 3) {
                App.getInstance().addPanel(new DifficultySelectPanel(username, 3, unlockedStage), "difficultyselect");
                App.getInstance().showPanel("difficultyselect");
            }
        });
        backBtn.addActionListener(e -> {
            App.getInstance().addPanel(new MainMenuPanel(username, unlockedStage), "mainmenu");
            App.getInstance().showPanel("mainmenu");
        });

        add(stage1Btn);
        add(stage2Btn);
        add(stage3Btn);
        add(backBtn);
    }

    private JButton createStageButton(String imagePath, int x, int y, int width, int height,
            boolean locked, Color glowColor) {

        BufferedImage stageImg = loadImage("assets/images/stages/buttons/" + imagePath);

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
                if (stageImg != null) {
                    g2d.drawImage(stageImg, 0, 0, getWidth(), getHeight(), null);
                }
                if (hovered) {
                    g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(),
                            glowColor.getBlue(), 180));
                    g2d.setStroke(new BasicStroke(6));
                    g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 30, 30);
                }
                g2d.dispose();
            }
        };

        btn.setBounds(x, y, width, height);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(locked ? Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTitle((Graphics2D) g, "SELECT STAGE", ScreenUtils.scaleY(150));
    }
}