package rustamscode.client_rus.frame;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        // Установка темы FlatDarkLaf
        FlatDarkLaf.setup();

        // Параметры окна
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Открытие окна по центру экрана

        // Поля ввода для имени пользователя и пароля
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");

        // Панель с полями ввода и кнопкой
        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);

        // Обработчик события для кнопки Login
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Пожалуйста, введите имя пользователя и пароль.");
            } else if (authenticate(username, password)) {
                new ChatFrame(username).setVisible(true);
                dispose(); // Закрываем окно авторизации
            } else {
                JOptionPane.showMessageDialog(this, "Неверные имя пользователя или пароль.");
            }
        });

    }

    // Метод для простой проверки данных (пример с тестовыми данными)
    private boolean authenticate(String username, String password) {
        return "testuser".equals(username) && "password".equals(password);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
