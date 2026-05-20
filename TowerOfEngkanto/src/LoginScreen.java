import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class LoginScreen extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private DatabaseManager dbManager;

    public LoginScreen() {
        dbManager = new DatabaseManager();

        setTitle("Tower of Engkanto - Login");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        BufferedImage loginBg;
        try {
            loginBg = ImageIO.read(new File("assets/images/loginbg.png"));
        } catch (Exception e) {
            loginBg = null;
        }

        final BufferedImage finalBg = loginBg;

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBg != null) {
                    g.drawImage(finalBg, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panel.setLayout(null);
        setContentPane(panel);

        JLabel label = new JLabel("AUTHENTICATION", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 40));
        label.setForeground(Color.WHITE);
        label.setBounds(760, 300, 400, 50);
        panel.add(label);

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
        userField.setBounds(810, 400, 300, 40);
        panel.add(userField);

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
                            setForeground(Color.GRAY);
                            setEchoChar((char) 0);
                            setText("Password");
                        }
                    }
                });
            }
        };
        passField.setBounds(810, 450, 300, 40);
        panel.add(passField);

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBounds(810, 520, 145, 40);
        loginBtn.addActionListener(e -> handleLogin());
        panel.add(loginBtn);

        JButton regBtn = new JButton("REGISTER");
        regBtn.setBounds(965, 520, 145, 40);
        regBtn.addActionListener(e -> handleRegister());
        panel.add(regBtn);
    }

    private void handleLogin() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        if (dbManager.validateLogin(user, pass)) {
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome, Protector.");
            new MainMenu(user).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. The shadows grow stronger...", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        if (dbManager.registerUser(user, pass)) {
            JOptionPane.showMessageDialog(this, "Registration Successful! You may now log in.");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists. Choose a different name.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginScreen().setVisible(true);
    }
}
