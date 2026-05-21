import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends BasePanel {

    private int unlockedStage;

    public MainMenuPanel(String username, int unlockedStage) {
        super("assets/images/background.png", username);
        this.unlockedStage = unlockedStage;
    }

    @Override
    public String getPanelName() {
        return "mainmenu";
    }

    @Override
    protected void initComponents() {
        int btnWidth = ScreenUtils.scaleX(720);
        int btnHeight = ScreenUtils.scaleY(180);
        int btnX = (ScreenUtils.WIDTH - btnWidth) / 2;
        int startY = ScreenUtils.scaleY(330);
        int spacing = ScreenUtils.scaleY(130);

        JButton startBtn = createImageButton("assets/images/buttons/start_btn_def.png",
                "assets/images/buttons/start_btn_hover.png", btnX, startY, btnWidth, btnHeight);
        JButton howToBtn = createImageButton("assets/images/buttons/htp_btn_def.png",
                "assets/images/buttons/htp_btn_hover.png", btnX, startY + spacing, btnWidth, btnHeight);
        JButton codexBtn = createImageButton("assets/images/buttons/codex_btn_def.png",
                "assets/images/buttons/codex_btn_hover.png", btnX, startY + spacing * 2, btnWidth, btnHeight);
        JButton ldbBtn = createImageButton("assets/images/buttons/ldb_btn_def.png",
                "assets/images/buttons/ldb_btn_hover.png", btnX, startY + spacing * 3, btnWidth, btnHeight);
        JButton exitBtn = createImageButton("assets/images/buttons/exit_btn_def.png",
                "assets/images/buttons/exit_btn_hover.png", btnX, startY + spacing * 4, btnWidth, btnHeight);

        startBtn.addActionListener(e -> {
            App.getInstance().addPanel(new StageSelectPanel(username, unlockedStage), "stageselect");
            App.getInstance().showPanel("stageselect");
        });

        codexBtn.addActionListener(e -> {
            App.getInstance().addPanel(new codex(username), "codex");
            App.getInstance().showPanel("codex");
        });

        exitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION)
                System.exit(0);
        });

        add(startBtn);
        add(howToBtn);
        add(codexBtn);
        add(ldbBtn);
        add(exitBtn);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTitle((Graphics2D) g, "Tower of Engkanto", ScreenUtils.scaleY(155));
    }
}