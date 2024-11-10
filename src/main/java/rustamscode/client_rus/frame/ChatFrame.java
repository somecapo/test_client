package rustamscode.client_rus.frame;

import rustamscode.client_rus.client.Client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ChatFrame extends JFrame {
    private final String username;
    private final JTextArea messageArea;
    private final Client client;
    private final JList<String> userList;
    private final DefaultListModel<String> listModel; // Панель для списка пользователей

    public ChatFrame(String username) {
        this.username = username;

        // Инициализируем клиент
        this.client = createClient();

        // Настройки окна чата
        setTitle("Chat - " + username);
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Панель для отображения сообщений
        messageArea = new JTextArea();
        messageArea.setEditable(false);

        // Поле ввода и кнопка отправки
        JTextField messageField = new JTextField(30);
        JButton sendButton = new JButton("Send");

        // Панель для ввода и отправки
        JPanel inputPanel = new JPanel();
        inputPanel.add(messageField);
        inputPanel.add(sendButton);

        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel); // Список пользователей

        // Панель для списка пользователей
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.add(new JLabel("Users online:"), BorderLayout.NORTH);
        sidePanel.add(new JScrollPane(userList), BorderLayout.CENTER);
        sidePanel.setPreferredSize(new Dimension(150, getHeight()));

        add(sidePanel, BorderLayout.EAST); // Добавляем боковую панель справа

        // Добавление компонентов в окно
        add(new JScrollPane(messageArea), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(sidePanel, BorderLayout.EAST);

        // Обновление окна
        revalidate();
        repaint();

        // Добавляем тестовых пользователей
        listModel.addElement("User1");
        listModel.addElement("User2");
        listModel.addElement("User3");

        // Обработчик кнопки "Send"
        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                sendMessageToServer(message);
                messageField.setText("");
                messageArea.append("You: " + message + "\n"); // Локально отображаем отправленное сообщение
            }
        });

        // Запускаем поток для получения сообщений от сервера
        new Thread(this::receiveMessagesFromServer).start();
    }

    // В ChatFrame
    @Override
    public void dispose() {
        try {
            if (client != null) {
                client.close(); // Закрываем соединение
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.dispose();
    }

    private void updateUserList() {
        while (true) {
            List users = requestUserListFromServer(); // Запрос списка у сервера
            SwingUtilities.invokeLater(() -> {
                listModel.clear();
                users.forEach(element -> listModel.addElement((String) element));
            });
            try {
                Thread.sleep(5000); // Обновление каждые 5 секунд
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод-заглушка для получения списка пользователей от сервера
    private List<String> requestUserListFromServer() {
        // Реализация получения списка пользователей с сервера
        return List.of("User1", "User2", "User3"); // Пример данных
    }

    // Метод для создания клиента
    private Client createClient() {
        try {
            return new Client("localhost", 10001); // Адрес сервера и порт
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка подключения к серверу");
            dispose(); // Закрываем окно, если не удалось подключиться
            return null;
        }
    }

    // Метод для отправки сообщения на сервер
    private void sendMessageToServer(String message) {
        if (client != null) {
            try {
                client.sendMessage(username + ": " + message);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка отправки сообщения.");
                e.printStackTrace();
            }
        }
    }

    // Метод для приёма сообщений от сервера
    private void receiveMessagesFromServer() {
        if (client != null) {
            try {
                String message;
                while ((message = client.receiveMessage()) != null) {
                    messageArea.append(message + "\n"); // Отображаем сообщение в интерфейсе
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Соединение с сервером потеряно.");
            }
        }
    }
}
