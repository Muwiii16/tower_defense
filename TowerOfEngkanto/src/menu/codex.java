package menu;

import app.*;
import database.DatabaseManager;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.*;

public class codex extends BasePanel {

    private DatabaseManager dbManager;

    @Override
    public String getPanelName() {
        return "codex";
    }

    @Override
    protected void initComponents() {
        int bigBtnWidth = ScreenUtils.scaleX(900);
        int bigBtnHeight = ScreenUtils.scaleY(280);

        int backBtnWidth = ScreenUtils.scaleX(720);
        int backBtnHeight = ScreenUtils.scaleY(180);

        int totalWidth = bigBtnWidth * 2 + ScreenUtils.scaleX(50);
        int startX = (ScreenUtils.WIDTH - totalWidth) / 2;

        JButton enemiesBtn = createImageButton(
                "assets/images/codex/enemies_btn_def.png",
                "assets/images/codex/enemies_btn_hover.png",
                startX, ScreenUtils.scaleY(300),
                bigBtnWidth, bigBtnHeight);

        JButton towersBtn = createImageButton(
                "assets/images/codex/towers_btn_def.png",
                "assets/images/codex/towers_btn_hover.png",
                startX + bigBtnWidth + ScreenUtils.scaleX(50), ScreenUtils.scaleY(300),
                bigBtnWidth, bigBtnHeight);

        JButton backBtn = createImageButton(
                "assets/images/buttons/back_btn_def.png",
                "assets/images/buttons/back_btn_hover.png",
                (ScreenUtils.WIDTH - backBtnWidth) / 2,
                ScreenUtils.scaleY(750), backBtnWidth, backBtnHeight);

        enemiesBtn.addActionListener(e -> {
            App.getInstance().addPanel(new EnemyCodexPanel(username), "enemycodex");
            App.getInstance().showPanel("enemycodex");
        });
        towersBtn.addActionListener(e -> {
            App.getInstance().addPanel(new TowerCodexPanel(username), "towercodex");
            App.getInstance().showPanel("towercodex");
        });
        backBtn.addActionListener(e -> {
            int unlockedStage = dbManager.getUnlockedStage(username);
            App.getInstance().addPanel(new MainMenuPanel(username, unlockedStage), "mainmenu");
            App.getInstance().showPanel("mainmenu");
        });

        add(enemiesBtn);
        add(towersBtn);
        add(backBtn);
    }

    public codex(String username) {
        super("assets/images/background.png", username);
        this.dbManager = new DatabaseManager();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTitle((Graphics2D) g, "assets/images/titles/codex_title.png", ScreenUtils.scaleY(110));
    }

}
