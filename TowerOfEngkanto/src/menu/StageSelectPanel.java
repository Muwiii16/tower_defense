package menu;

import app.*;
import javax.swing.*;
import java.awt.*;

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
        int btnWidth = ScreenUtils.scaleX(720);
        int btnHeight = ScreenUtils.scaleY(180);
        int btnX = (ScreenUtils.WIDTH - btnWidth) / 2;

        JButton stage1Btn = createImageButton("assets/images/stages/buttons/stg1_btn_def.png",
                "assets/images/stages/buttons/stg1_btn_hover.png", btnX, ScreenUtils.scaleY(200),
                btnWidth, btnHeight);
        JButton stage2Btn = unlockedStage >= 2
                ? createImageButton("assets/images/stages/buttons/stg2_btn_def.png",
                        "assets/images/stages/buttons/stg2_btn_hover.png",
                        btnX, ScreenUtils.scaleY(400), btnWidth, btnHeight)
                : createFadeButton("assets/images/stages/buttons/stg2_btn_lck.png",
                        btnX, ScreenUtils.scaleY(400), btnWidth, btnHeight);
        JButton stage3Btn = unlockedStage >= 3
                ? createImageButton("assets/images/stages/buttons/stg3_btn_def.png",
                        "assets/images/stages/buttons/stg3_btn_hover.png",
                        btnX, ScreenUtils.scaleY(600), btnWidth, btnHeight)
                : createFadeButton("assets/images/stages/buttons/stg3_btn_lck.png",
                        btnX, ScreenUtils.scaleY(600), btnWidth, btnHeight);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTitle((Graphics2D) g, "SELECT STAGE", ScreenUtils.scaleY(150));
    }
}