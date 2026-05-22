import javax.swing.*;
import java.awt.*;

public class LoginPanel extends BasePanel {

    private JTextField userField;
    private JPasswordField passField;
    private DatabaseManager dbManager;

    public LoginPanel() {
        super("assets/images/loginbg.png", null);
        dbManager = new DatabaseManager();
    }

    @Override
    public String getPanelName() {
        return "login";
    }

    @Override
    protected void initComponents() {

        int frameWidth = ScreenUtils.scaleX(500);
        int frameHeight = ScreenUtils.scaleY(730);
        int frameX = (ScreenUtils.WIDTH - frameWidth) / 2;
        int frameY = (ScreenUtils.HEIGHT - frameHeight) / 2;

        userField = new JTextField("Username") {
            {
                setForeground(Color.GRAY);
                addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent e) {
                        if (getText().equals("Username")) {
                            setText("");
                            setForeground(Color.WHITE);
                        }
                    }

                    public void focusLost(java.awt.event.FocusEvent e) {
                        if (getText().isEmpty()) {
                            setText("Username");
                            setForeground(Color.GRAY);
                        }
                    }
                });
            }
        };

        passField = new JPasswordField() {
            {
                setForeground(Color.GRAY);
                setEchoChar((char) 0);
                setText("Password");
                addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent e) {
                        if (String.valueOf(getPassword()).equals("Password")) {
                            setText("");
                            setForeground(Color.WHITE);
                            setEchoChar('•');
                        }
                    }

                    public void focusLost(java.awt.event.FocusEvent e) {
                        if (String.valueOf(getPassword()).isEmpty()) {
                            setText("Password");
                            setForeground(Color.GRAY);
                            setEchoChar((char) 0);
                        }
                    }
                });
            }
        };

        int fieldWidth = ScreenUtils.scaleX(340);
        int fieldHeight = ScreenUtils.scaleY(80);
        int fieldX = (ScreenUtils.WIDTH - fieldWidth) / 2;

        addFieldWithBackground("assets/images/login/textbox.png",
                userField, fieldX, frameY + ScreenUtils.scaleY(200), fieldWidth, fieldHeight);
        addFieldWithBackground("assets/images/login/textbox.png",
                passField, fieldX, frameY + ScreenUtils.scaleY(300), fieldWidth, fieldHeight);

        int btnWidth = ScreenUtils.scaleX(153);
        int btnHeight = ScreenUtils.scaleY(51);

        int totalWidth = btnWidth * 2 + ScreenUtils.scaleX(50);
        int startX = (ScreenUtils.WIDTH - totalWidth) / 2;

        JButton loginBtn = createImageButton("assets/images/login/login_btn_def.png",
                "assets/images/login/login_btn_hover.png",
                startX + ScreenUtils.scaleX(20), ScreenUtils.scaleY(560), btnWidth, btnHeight);
        loginBtn.addActionListener(e -> handleLogin());
        add(loginBtn);

        JButton regBtn = createImageButton("assets/images/login/reg_btn_def.png",
                "assets/images/login/reg_btn_hover.png",
                startX + ScreenUtils.scaleX(190), ScreenUtils.scaleY(560), btnWidth, btnHeight);
        regBtn.addActionListener(e -> handleRegister());
        add(regBtn);

        JLabel loginFrame = new JLabel(new ImageIcon(loadImage("assets/images/login/atc_brd.png")
                .getScaledInstance(frameWidth, frameHeight, Image.SCALE_SMOOTH)));
        loginFrame.setBounds(frameX, frameY, frameWidth, frameHeight);
        add(loginFrame);
    }

    private void handleLogin() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        if (user.equals("Username") || user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pass.equals("Password") || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (dbManager.validateLogin(user, pass)) {
            // Get unlocked stage from DB then go to main menu
            int unlockedStage = dbManager.getUnlockedStage(user);
            App.getInstance().addPanel(new MainMenuPanel(user, unlockedStage), "mainmenu");
            App.getInstance().showPanel("mainmenu");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. The shadows grow stronger...",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        if (user.equals("Username") || user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pass.equals("Password") || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (dbManager.registerUser(user, pass)) {
            JOptionPane.showMessageDialog(this, "Registration Successful! You may now log in.");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists. Choose a different name.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFieldWithBackground(String bgPath, JTextField field, int x, int y, int width, int height) {
        JLabel bg = new JLabel(new ImageIcon(
                loadImage(bgPath).getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        bg.setBounds(x, y, width, height);

        field.setOpaque(false);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setForeground(Color.LIGHT_GRAY);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        field.setBounds(x, y, width, height);

        add(field);
        add(bg);
    }
}