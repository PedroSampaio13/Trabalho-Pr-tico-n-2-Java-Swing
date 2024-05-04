import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowLoginForm();
            }
        });
    }

    private static void createAndShowLoginForm() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(350, 200);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel titleLabel = new JLabel("BookingSystem");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();


        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Virificar as credenciais do user aqui
                // Se as credenciais estiverem corretas, cria a janela principal (Dashboard)
                // Caso contrário mensagem de erro
                if (username.equals("admin") && password.equals("admin")) {
                    loginFrame.dispose(); // Fecha o formulário de login

                    // criar uma lista de quartos disponíveis
                    List<Room> availableRooms = createSampleAvailableRooms();

                    //  criar uma lista de reservas
                    List<Booking> bookings = createSampleBookings();

                    // Cria a janela com a lista de quartos disponíveis e de reservas
                    Janela janela = new Janela(availableRooms, bookings);
                    janela.setVisible(true); // Torna a janela principal visível
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Credenciais inválidas", "Erro de login", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Adicionar uma tecla de atalho para o botão de login quando "Enter" é pressionado
        loginFrame.getRootPane().setDefaultButton(loginButton);

        // Adicionar os botões ao painel de formulário
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }

       // Método para criar uma lista de quartos disponíveis
        private static List<Room> createSampleAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();

        // Adiciona alguns quartos disponíveis à lista
        availableRooms.add(new Room(1, 101, 1, 0, 34.0f));
        availableRooms.add(new Room(2, 102, 2, 0, 48.0f));
        availableRooms.add(new Room(3, 103, 2, 1, 52.0f));
        availableRooms.add(new Room(4, 104, 2, 2, 64.0f));
        availableRooms.add(new Room(5, 201, 1, 0, 34.0f));
        availableRooms.add(new Room(6, 202, 2, 0, 48.0f));
        availableRooms.add(new Room(7, 203, 2, 1, 52.0f));
        availableRooms.add(new Room(8, 204, 2, 2, 64.0f));
        availableRooms.add(new Room(9, 301, 4, 2, 107.0f));
        availableRooms.add(new Room(10, 302, 4, 2, 107.0f));

        return availableRooms;
    }

    // Método de exemplo para criar uma lista de reservas
    private static List<Booking> createSampleBookings() {
        List<Booking> bookings = new ArrayList<>();
        return bookings;
    }
}
