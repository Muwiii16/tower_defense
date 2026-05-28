package menu;

import app.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HowToPlayPanel extends BasePanel {

    private int currentPage = 0;
    private static final int TOTAL_PAGES = 4; // Updated to match your 4 custom guide images

    // Array to store full-page background images
    private BufferedImage[] pageImages;
    // Graphical title image asset (htp_title.jpg)
    private BufferedImage titleGraphic;

    // Navigation Buttons
    private JButton backBtn;
    private JButton prevBtn;
    private JButton nextBtn;

    public HowToPlayPanel(String username) {
        // Pass null for default background to save memory, as we load specific page
        // imagery instead
        super(null, username);
    }

    @Override
    public String getPanelName() {
        return "howtoplay";
    }

    @Override
    protected void initComponents() {
        setLayout(null);

        // 1. Load your 4 page background images
        // Ensure these files are placed inside your project's assets folder path!
        pageImages = new BufferedImage[TOTAL_PAGES];
        pageImages[0] = loadImage("assets/images/HowToPlay1.png");
        pageImages[1] = loadImage("assets/images/HowToPlay2.png");
        pageImages[2] = loadImage("assets/images/HowToPlay3.png");
        pageImages[3] = loadImage("assets/images/HowToPlay4.png");

        // 1. Load your graphical title image (htp_title.jpg)
        titleGraphic = loadImage("assets/images/titles/htp_title.png");
        if (titleGraphic == null) {
            System.err.println("Error: Failed to load htp_title.png");
        }

        // 2. Button Dimensions & Position Scaling (Top Layout placement)
        int btnWidth = ScreenUtils.scaleX(260);
        int btnHeight = ScreenUtils.scaleY(75);
        int topY = ScreenUtils.scaleY(40);

        int backX = ScreenUtils.scaleX(60);
        int prevX = ScreenUtils.WIDTH - ScreenUtils.scaleX(620);
        int nextX = ScreenUtils.WIDTH - ScreenUtils.scaleX(320);

        // 3. Initialize Buttons with Hover States
        backBtn = createImageButton(
                "assets/images/buttons/back_btn_def.png",
                "assets/images/buttons/back_btn_hover.png",
                backX, topY, btnWidth, btnHeight);

        prevBtn = createImageButton(
                "assets/images/buttons/prev_btn_def.png", // Ensure these navigation assets exist
                "assets/images/buttons/prev_btn_hover.png",
                prevX, topY, btnWidth, btnHeight);

        nextBtn = createImageButton(
                "assets/images/buttons/next_btn_def.png", // Ensure these navigation assets exist
                "assets/images/buttons/next_btn_hover.png",
                nextX, topY, btnWidth, btnHeight);

        // 4. Button Action Listeners & Page Control Logic
        backBtn.addActionListener(e -> {
            // Securely return to the main menu panel layout
            App.getInstance().showPanel("mainmenu");
        });

        prevBtn.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateButtonVisibility();
                repaint();
            }
        });

        nextBtn.addActionListener(e -> {
            if (currentPage < TOTAL_PAGES - 1) {
                currentPage++;
                updateButtonVisibility();
                repaint();
            }
        });

        // Add components to layout container
        add(backBtn);
        add(prevBtn);
        add(nextBtn);

        // Initialize initial visibility states
        updateButtonVisibility();
    }

    /**
     * Dynamically hides/shows navigation buttons based on current page index
     * limits.
     */
    private void updateButtonVisibility() {
        prevBtn.setVisible(currentPage > 0);
        nextBtn.setVisible(currentPage < TOTAL_PAGES - 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Draw the current page image scaled full screen
        if (pageImages[currentPage] != null) {
            g2d.drawImage(pageImages[currentPage], 0, 0, getWidth(), getHeight(), null);
        } else {
            // Fallback clear option if an image asset path fails to load smoothly
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Render the graphical title image asset (htp_title.jpg)
        if (titleGraphic != null) {
            // Determine a scaled width for prominent display (e.g., logical 700px on 1920
            // canvas)
            int scaledTitleWidth = ScreenUtils.scaleX(700);

            // Dynamically calculate height to maintain aspect ratio
            double ar = (double) titleGraphic.getWidth() / titleGraphic.getHeight();
            int scaledTitleHeight = (int) (scaledTitleWidth / ar);

            // Define horizontal and vertical center point relative to header
            int centerX = getWidth() / 2;
            int targetCenterY = ScreenUtils.scaleY(90);

            // Calculate Top-Left drawing coordinates based on the center point
            int titleX = centerX - (scaledTitleWidth / 2);
            int titleY = targetCenterY - (scaledTitleHeight / 2);

            g2d.drawImage(titleGraphic, titleX, titleY, scaledTitleWidth, scaledTitleHeight, null);
        } else {
            // Basic fallback text drawing if graphic fails to load
            drawTitle(g2d, "HOW TO PLAY", ScreenUtils.scaleY(90), true);
        }

        // 3. Render a subtle page counter text overlay (Optional indicator)
        g2d.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(22)));
        g2d.setColor(new Color(255, 215, 0)); // Gold Accent color
        String pageIndicator = (currentPage + 1) + " / " + TOTAL_PAGES;
        FontMetrics fm = g2d.getFontMetrics();
        int indicatorX = (getWidth() - fm.stringWidth(pageIndicator)) / 2;
        g2d.drawString(pageIndicator, indicatorX, ScreenUtils.scaleY(140));
    }
}