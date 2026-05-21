import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private static App instance;
    private CardLayout cardLayout;
    private JPanel container;

    private App() {
        setTitle("Tower of Engkanto");
        setSize(ScreenUtils.WIDTH, ScreenUtils.HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        container.setBackground(Color.BLACK);
        setContentPane(container);
    }

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    public void addPanel(JPanel panel, String name) {
        container.add(panel, name);
    }

    public void showPanel(String name) {
        cardLayout.show(container, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = App.getInstance();
            app.addPanel(new LoginPanel(), "login");
            app.showPanel("login");
            app.setVisible(true);
        });
    }
}
