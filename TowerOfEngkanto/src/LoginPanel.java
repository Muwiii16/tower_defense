import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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
        int centerX = ScreenUtils.WIDTH / 2;

        JLabel label = new JLabel("AUTHENTICATION", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, ScreenUtils.scaleFont(40)));
        label.setForeground(Color.WHITE);
        label.setBounds(centerX - ScreenUtils.scaleX(200), ScreenUtils.scaleY(300),
                ScreenUtils.scaleX(400), ScreenUtils.scaleY(50));
        add(label);

        userField = new JTextField("Username") {
            {
                setForeground(Color.GRAY);
                addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent e) {
                        if (getText().equals("Username")) {
                            setText("");
                            setForeground(Color.BLACK);
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
        userField.setBounds(centerX - ScreenUtils.scaleX(150), ScreenUtils.scaleY(400),
                ScreenUtils.scaleX(300), ScreenUtils.scaleY(40));
        add(userField);

        passField = new JPasswordField() {
            {
                setForeground(Color.GRAY);
                setEchoChar((char) 0);
                setText("Password");
                addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusGained(java.awt.event.FocusEvent e) {
                        if (String.valueOf(getPassword()).equals("Password")) {
                            setText("");
                            setForeground(Color.BLACK);
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
        passField.setBounds(centerX - ScreenUtils.scaleX(150), ScreenUtils.scaleY(450),
                ScreenUtils.scaleX(300), ScreenUtils.scaleY(40));
        add(passField);

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBounds(centerX - ScreenUtils.scaleX(150), ScreenUtils.scaleY(520),
                ScreenUtils.scaleX(145), ScreenUtils.scaleY(40));
        loginBtn.addActionListener(e -> handleLogin());
        add(loginBtn);

        JButton regBtn = new JButton("REGISTER");
        regBtn.setBounds(centerX, ScreenUtils.scaleY(520),
                ScreenUtils.scaleX(145), ScreenUtils.scaleY(40));
        regBtn.addActionListener(e -> handleRegister());
        add(regBtn);
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
}