import javax.swing.*;
import java.awt.*;

public class DifficultySelectPanel extends BasePanel {

    private int stageNumber;
    private int unlockedStage;

    public DifficultySelectPanel(String username, int stageNumber, int unlockedStage) {
        super("assets/images/background.png", username);
        this.stageNumber = stageNumber;
        this.unlockedStage = unlockedStage;
    }

    @Override
    public String getPanelName() {
        return "difficultyselect";
    }

    @Override
    protected void initComponents() {
        int btnWidth = ScreenUtils.scaleX(720);
        int btnHeight = ScreenUtils.scaleY(180);
        int btnX = (ScreenUtils.WIDTH - btnWidth) / 2;

        JButton easyBtn = createImageButton("assets/images/stages/buttons/easy_btn_def.png",
                "assets/images/stages/buttons/easy_btn_hover.png", btnX, ScreenUtils.scaleY(320), btnWidth, btnHeight);
        JButton normalBtn = createImageButton("assets/images/stages/buttons/normal_btn_def.png",
                "assets/images/stages/buttons/normal_btn_hover.png", btnX, ScreenUtils.scaleY(500), btnWidth, btnHeight);
        JButton hardBtn = createImageButton("assets/images/stages/buttons/hard_btn_def.png",
                "assets/images/stages/buttons/hard_btn_hover.png", btnX, ScreenUtils.scaleY(680), btnWidth, btnHeight);
        JButton backBtn = createImageButton("assets/images/buttons/back_btn_def.png",
                "assets/images/buttons/back_btn_hover.png",
                btnX, ScreenUtils.scaleY(860), btnWidth, btnHeight);

        easyBtn.addActionListener(e -> startGame("easy"));
        normalBtn.addActionListener(e -> startGame("normal"));
        hardBtn.addActionListener(e -> startGame("hard"));
        backBtn.addActionListener(e -> {
            App.getInstance().addPanel(new StageSelectPanel(username, unlockedStage), "stageselect");
            App.getInstance().showPanel("stageselect");
        });

        add(easyBtn);
        add(normalBtn);
        add(hardBtn);
        add(backBtn);
    }

    private void startGame(String difficulty) {
        // TODO: replace with actual game panel
        System.out.println("Starting Stage " + stageNumber + " on " + difficulty);
        JOptionPane.showMessageDialog(this,
                "Starting Stage " + stageNumber + " - " + difficulty + "!\n(Game screen coming soon)");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawTitle(g2d, "SELECT DIFFICULTY", ScreenUtils.scaleY(150));

        // Subtitle showing which stage was selected
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(new Font("Serif", Font.PLAIN, ScreenUtils.scaleFont(36)));
        String sub = "Stage " + stageNumber;
        FontMetrics fm = g2d.getFontMetrics();
        int sx = (getWidth() - fm.stringWidth(sub)) / 2;
        g2d.setColor(new Color(200, 200, 200, 200));
        g2d.drawString(sub, sx, ScreenUtils.scaleY(210));
    }
}