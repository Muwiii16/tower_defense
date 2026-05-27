package menu;

import app.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HowToPlayPanel extends BasePanel {

    private int currentPage = 0;
    private static final int TOTAL_PAGES = 5;


    // NAVIGATION BUTTONS
    private JButton backBtn;
    private JButton prevBtn;
    private JButton nextBtn;

    public HowToPlayPanel(String username) {
        // PNG INSERT:
        super("assets/images/background.png", username);
    }

    @Override
    public String getPanelName() {
        return "howtoplay";
    }

    @Override
    protected void initComponents() {
        setLayout(null);

        // NAV BUTTON POSITIONS
        int navBtnWidth = ScreenUtils.scaleX(420);
        int navBtnHeight = ScreenUtils.scaleY(120);

        int bottomY = ScreenUtils.scaleY(880);
        int backX = ScreenUtils.scaleX(75);
        int prevX = ScreenUtils.scaleX(650);
        int nextX = ScreenUtils.scaleX(1080);

        // PNG INSERT: BACK BUTTON
        backBtn = createImageButton(
                "assets/images/buttons/back_btn_def.png",
                "assets/images/buttons/back_btn_hover.png",
                backX,
                bottomY,
                navBtnWidth,
                navBtnHeight
        );

        // PNG INSERT: PREVIOUS BUTTON
        // Default PNG: assets/images/buttons/howtoplay/next_def.png
        // Hover PNG:   assets/images/buttons/howtoplay/next_hover.png
        prevBtn = createImageButton(
                "assets/images/buttons/howtoplay/previous_def.png",
                "assets/images/buttons/howtoplay/previous_hover.png",
                prevX,
                bottomY,
                navBtnWidth,
                navBtnHeight
        );

        // PNG INSERT: NEXT BUTTON
        // Default PNG: assets/images/buttons/howtoplay/next_def.png
        // Hover PNG:   assets/images/buttons/howtoplay/next_hover.png
        nextBtn = createImageButton(
                "assets/images/buttons/howtoplay/next_def.png",
                "assets/images/buttons/howtoplay/next_hover.png",
                nextX,
                bottomY,
                navBtnWidth,
                navBtnHeight
        );

        // BACK FUNCTION
        // Returns to Main Menu.
        backBtn.addActionListener(e -> App.getInstance().showPanel("mainmenu"));

        // PREVIOUS FUNCTION
        // Moves one tutorial screen backward.
        prevBtn.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateNavigationButtons();
                repaint();
            }
        });

        // NEXT FUNCTION
        // Moves one tutorial screen forward.
        nextBtn.addActionListener(e -> {
            if (currentPage < TOTAL_PAGES - 1) {
                currentPage++;
                updateNavigationButtons();
                repaint();
            }
        });

        add(backBtn);
        add(prevBtn);
        add(nextBtn);

        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        // PDF page 1 top screen has no Previous button.
        prevBtn.setVisible(currentPage > 0);

        // PDF page 3 final screen has no Next button.
        nextBtn.setVisible(currentPage < TOTAL_PAGES - 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // ============================================================
        // PNG INSERT: HOW TO PLAY TITLE
        // This is the yellow "How To Play" title shown at the top of every PDF screen.
        //
        // Existing project likely has:
        // assets/images/titles/htp_title.png
        //
        // Adjust Y to move it higher/lower.
        // ============================================================
        drawTitle(g2d, "assets/images/titles/htp_title.png", ScreenUtils.scaleY(80));

        switch (currentPage) {
            case 0:
                drawIntroPage(g2d);
                break;
            case 1:
                drawFullMapGuidePage(g2d);
                break;
            case 2:
                drawEnemyTowerNotePage(g2d);
                break;
            case 3:
                drawHealthBarNotePage(g2d);
                break;
            case 4:
                drawGameOverPage(g2d);
                break;
            default:
                drawIntroPage(g2d);
                break;
        }

        g2d.dispose();
    }

    // PAGE 0 - INTRO PAGE
    private void drawIntroPage(Graphics2D g2d) {

        // PNG INSERT: INTRO TEXT PANEL
        // This is the large semi-transparent rounded gray box from the PDF.
        //
        // Suggested path:
        // assets/images/howtoplay/panels/intro_panel.png
        //
        // If you create this as a PNG, uncomment drawPng() and comment drawTemporaryRoundedPanel().
        // drawPng(g2d, "assets/images/howtoplay/panels/intro_panel.png", 330, 210, 1260, 560);

        drawTemporaryRoundedPanel(g2d, 330, 210, 1260, 560);

        // TEXT:
        // You can delete this if your intro panel already contains all text as PNG.
        drawCenteredText(g2d, "Welcome to Tower of Engkanto", 285, true, 34);

        drawParagraph(g2d, new String[]{
                "The game is to defend your base \"Altar\" and prevent any",
                "myth monsters to reach",
                "",
                "Enemies have three types, Kapre, Tikbalang, and",
                "Manananggal, they have different stats and abilities.",
                "",
                "Towers have three types, Bantay-bantayan, mandirigma,",
                "and Lantaka Cannon, each have a different abilities to",
                "defeat enemies"
        }, 380, 380, 44, 30);
    }

    // PAGE 1 - FULL MAP GUIDE
    private void drawFullMapGuidePage(Graphics2D g2d) {

        // PNG INSERT: CENTER GAMEPLAY MAP
        // This is the big gameplay screenshot in the middle of the PDF.
        // It should contain the map, tower shop UI, score/wave area, shrine, altar, etc.
        //
        // Suggested path:
        // assets/images/howtoplay/pages/map_guide.png
        drawPng(g2d, "assets/images/howtoplay/pages/map_guide.png", 520, 220, 880, 555);

        // PNG INSERT: LEFT TOP CALLOUT - endpoint/shrine explanation
        drawPng(g2d, "assets/images/howtoplay/callouts/map_endpoint_callout.png", 75, 120, 360, 245);

        // PNG INSERT: ARROW from left top callout to shrine / endpoint
        drawPng(g2d, "assets/images/howtoplay/arrows/map_endpoint_arrow.png", 370, 260, 300, 130);

        // PNG INSERT: LEFT MIDDLE CALLOUT - currency / tower placement explanation
        drawPng(g2d, "assets/images/howtoplay/callouts/map_currency_callout.png", 75, 430, 390, 230);

        // PNG INSERT: ARROW from currency callout to gameplay area
        drawPng(g2d, "assets/images/howtoplay/arrows/map_currency_arrow.png", 400, 470, 280, 160);

        // PNG INSERT: LEFT BOTTOM CALLOUT - enemy spawn explanation
        drawPng(g2d, "assets/images/howtoplay/callouts/map_spawn_callout.png", 75, 665, 360, 200);

        // PNG INSERT: ARROW from spawn callout to enemy starting point
        drawPng(g2d, "assets/images/howtoplay/arrows/map_spawn_arrow.png", 360, 620, 320, 160);

        // PNG INSERT: RIGHT TOP CALLOUT - currency/waves/time/score explanation
        drawPng(g2d, "assets/images/howtoplay/callouts/map_ui_callout.png", 1490, 150, 350, 220);

        // PNG INSERT: ARROW from right top callout to UI
        drawPng(g2d, "assets/images/howtoplay/arrows/map_ui_arrow.png", 1280, 280, 300, 120);

        // PNG INSERT: RIGHT MIDDLE CALLOUT - shop explanation
        drawPng(g2d, "assets/images/howtoplay/callouts/map_shop_callout.png", 1490, 420, 350, 220);

        // PNG INSERT: ARROW from shop callout to tower shop
        drawPng(g2d, "assets/images/howtoplay/arrows/map_shop_arrow.png", 1300, 475, 280, 120);

        // PNG INSERT: RIGHT BOTTOM CALLOUT - main path explanation
        drawPng(g2d, "assets/images/howtoplay/callouts/map_path_callout.png", 1450, 685, 390, 220);

        // PNG INSERT: ARROW from path callout to main path
        drawPng(g2d, "assets/images/howtoplay/arrows/map_path_arrow.png", 1190, 610, 380, 210);
    }

    // PAGE 2 - ENEMY AND TOWER NOTE
    private void drawEnemyTowerNotePage(Graphics2D g2d) {

        // PNG INSERT: GAMEPLAY SCREENSHOT
        // Use the screenshot with enemies and towers visible.
        drawPng(g2d, "assets/images/howtoplay/pages/enemy_tower_note.png", 520, 220, 880, 555);

        // PNG INSERT: LEFT CALLOUT - Take Note
        // Text in PDF:
        // "Take Note: The three enemies have a different abilities and stats.
        // The towers have also different abilities and counter their skills."
        drawPng(g2d, "assets/images/howtoplay/callouts/enemy_tower_note_callout.png", 80, 340, 380, 270);

        // PNG INSERT: ARROW from note to enemies/towers
        drawPng(g2d, "assets/images/howtoplay/arrows/enemy_tower_note_arrow.png", 400, 455, 360, 150);
    }

    // PAGE 3 - ALTAR HEALTH BAR NOTE
    private void drawHealthBarNotePage(Graphics2D g2d) {

        // PNG INSERT: GAMEPLAY SCREENSHOT
        // Use the screenshot with the right-side UI and health bar visible.
        drawPng(g2d, "assets/images/howtoplay/pages/healthbar_note.png", 520, 220, 880, 555);

        // PNG INSERT: RIGHT TOP CALLOUT - altar health note
        // "When the enemies reached the altar the altar's health will reduce."
        drawPng(g2d, "assets/images/howtoplay/callouts/healthbar_callout.png", 1450, 130, 390, 230);

        // PNG INSERT: ARROW from callout to health bar
        drawPng(g2d, "assets/images/howtoplay/arrows/healthbar_arrow.png", 1270, 255, 310, 180);
    }

    // PAGE 4 - GAME OVER NOTE
    private void drawGameOverPage(Graphics2D g2d) {

        // PNG INSERT: GAME OVER GAMEPLAY SCREENSHOT
        // Use the screenshot with GAME OVER text in the center.
        drawPng(g2d, "assets/images/howtoplay/pages/game_over_note.png", 520, 220, 880, 555);

        // PNG INSERT: TOP CENTER CALLOUT - game over explanation
        // Text in PDF:
        // "When the health bar reached to 0 then will pop up Game Over in your screen."
        drawPng(g2d, "assets/images/howtoplay/callouts/gameover_callout.png", 760, 150, 430, 210);

        // PNG INSERT: ARROW from callout to health/game over area
        drawPng(g2d, "assets/images/howtoplay/arrows/gameover_arrow.png", 900, 300, 260, 150);
    }

    // HELPER: DRAW PNG
    // All coordinates are based on a 1920 x 1080 design.
    // ScreenUtils scales them to your actual window size.
    // If the PNG is missing, a dashed placeholder box appears so you can still see where the asset should go.
    private void drawPng(Graphics2D g2d, String path, int x, int y, int width, int height) {
        int sx = ScreenUtils.scaleX(x);
        int sy = ScreenUtils.scaleY(y);
        int sw = ScreenUtils.scaleX(width);
        int sh = ScreenUtils.scaleY(height);

        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();

        if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            g2d.drawImage(img, sx, sy, sw, sh, null);
        } else {
            drawMissingAssetBox(g2d, path, sx, sy, sw, sh);
        }
    }

    // HELPER: TEMPORARY PANEL
    // Delete this when you replace intro_panel.png.
    private void drawTemporaryRoundedPanel(Graphics2D g2d, int x, int y, int width, int height) {
        int sx = ScreenUtils.scaleX(x);
        int sy = ScreenUtils.scaleY(y);
        int sw = ScreenUtils.scaleX(width);
        int sh = ScreenUtils.scaleY(height);

        g2d.setColor(new Color(150, 150, 150, 150));
        g2d.fillRoundRect(sx, sy, sw, sh, ScreenUtils.scaleX(35), ScreenUtils.scaleY(35));
    }

    // HELPER: MISSING ASSET PLACEHOLDER
    // This is useful while you are still placing PNGs. You may remove this once all PNGs are ready.
    private void drawMissingAssetBox(Graphics2D g2d, String path, int x, int y, int width, int height) {
        Stroke oldStroke = g2d.getStroke();
        Color oldColor = g2d.getColor();
        Font oldFont = g2d.getFont();

        float dash[] = {10.0f};
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        g2d.setColor(new Color(255, 230, 80, 180));
        g2d.drawRect(x, y, width, height);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, Math.max(10, ScreenUtils.scaleY(16))));
        g2d.drawString("PNG missing:", x + 8, y + 22);
        g2d.drawString(path, x + 8, y + 44);

        g2d.setStroke(oldStroke);
        g2d.setColor(oldColor);
        g2d.setFont(oldFont);
    }

    // HELPER: DRAW CENTERED TEXT
    // You can delete this if your text is already part of PNGs.
    private void drawCenteredText(Graphics2D g2d, String text, int designY, boolean bold, int fontSize) {
        Font oldFont = g2d.getFont();
        Color oldColor = g2d.getColor();

        g2d.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, ScreenUtils.scaleY(fontSize)));
        g2d.setColor(Color.WHITE);

        FontMetrics fm = g2d.getFontMetrics();
        int x = (ScreenUtils.WIDTH - fm.stringWidth(text)) / 2;
        int y = ScreenUtils.scaleY(designY);

        g2d.drawString(text, x, y);

        g2d.setFont(oldFont);
        g2d.setColor(oldColor);
    }

    // HELPER: DRAW PARAGRAPH TEXT
    // You can delete this if your text is already part of PNGs.
    private void drawParagraph(Graphics2D g2d, String[] lines, int designX, int designY, int lineHeight, int fontSize) {
        Font oldFont = g2d.getFont();
        Color oldColor = g2d.getColor();

        g2d.setFont(new Font("SansSerif", Font.PLAIN, ScreenUtils.scaleY(fontSize)));
        g2d.setColor(Color.WHITE);

        int x = ScreenUtils.scaleX(designX);
        int y = ScreenUtils.scaleY(designY);
        int lh = ScreenUtils.scaleY(lineHeight);

        for (String line : lines) {
            if (!line.isEmpty()) {
                g2d.drawString(line, x, y);
            }
            y += lh;
        }

        g2d.setFont(oldFont);
        g2d.setColor(oldColor);
    }
}