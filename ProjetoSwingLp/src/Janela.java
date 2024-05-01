import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

public class Janela extends JFrame {

    private JTable table;
    private List<Room> availableRooms;
    private List<Booking> bookings;
    private JButton addBookingButton; // Botão para adicionar reserva
    private JTextField searchField;
    private JTextField statusField;


    Janela(List<Room> availableRooms, List<Booking> bookings) {
        this.availableRooms = availableRooms;
        this.bookings = bookings;
        this.setTitle("Bookings"); // Adiciona um título ao GUI
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Permite fechar o GUI no X
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Define o estado da janela como maximizado
        this.getContentPane().setBackground(new Color(123, 165, 123)); // Define a cor de fundo da janela
        this.setVisible(true); // Torna a janela visível

        // Adiciona botões para alternar entre quartos e reservas (bookings)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton homepageButton = new JButton("Homepage");
        JButton roomsButton = new JButton("Quartos");
        JButton bookingsButton = new JButton("Reservas");

        JPanel homepagePanel = new JPanel(new GridLayout(4, 1)); // GridLayout para organizar em quatro linhas
        // Tabela para reservas com check-in hoje
        DefaultTableModel todayBookingsTableModel = createCheckInTableModel(); // Usar a nova função para criar o modelo de tabela
        JTable todayBookingsTable = new JTable(todayBookingsTableModel);
        todayBookingsTable.setName("CheckInTable");
        todayBookingsTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        todayBookingsTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane todayScrollPane = new JScrollPane(todayBookingsTable);

        // Título "Checking In Today" e a tabela correspondente
        JPanel checkInPanel = new JPanel(new BorderLayout());
        JLabel checkInTitle = new JLabel("Checking In Today");
        checkInTitle.setHorizontalAlignment(SwingConstants.CENTER);
        checkInTitle.setFont(new Font("Arial", Font.BOLD, 18)); // Tamanho e estilo da fonte ajustados
        checkInPanel.add(Box.createVerticalStrut(20)); // Espaço em cima do título
        checkInPanel.add(checkInTitle, BorderLayout.NORTH);
        checkInPanel.add(todayScrollPane, BorderLayout.CENTER);
        homepagePanel.add(checkInPanel);

        // Tabela para reservas com check-out hoje
        DefaultTableModel checkOutTableModel = createCheckOutTableModel(); // Usar a nova função para criar o modelo de tabela
        JTable checkOutTable = new JTable(checkOutTableModel);
        checkOutTable.setName("CheckOutTable");
        checkOutTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        checkOutTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane checkOutScrollPane = new JScrollPane(checkOutTable);

        // Título "Checking Out Today" e a tabela correspondente
        JPanel checkOutPanel = new JPanel(new BorderLayout());
        JLabel checkOutTitle = new JLabel("Checking Out Today");
        checkOutTitle.setHorizontalAlignment(SwingConstants.CENTER);
        checkOutTitle.setFont(new Font("Arial", Font.BOLD, 18)); // Tamanho e estilo da fonte ajustados
        checkOutPanel.add(Box.createVerticalStrut(20)); // Espaço em cima do título
        checkOutPanel.add(checkOutTitle, BorderLayout.NORTH);
        checkOutPanel.add(checkOutScrollPane, BorderLayout.CENTER);
        homepagePanel.add(checkOutPanel);

        // Adicione um espaço entre as seções
        homepagePanel.add(new JPanel()); // Adiciona um painel vazio para criar um espaço

        // Crie um novo painel que conterá buttonPanel e homepagePanel
        JPanel newPanel = new JPanel(new BorderLayout());
        newPanel.add(buttonPanel, BorderLayout.NORTH); // Adiciona o buttonPanel ao novo painel na parte superior
        newPanel.add(homepagePanel, BorderLayout.CENTER); // Adiciona o homepagePanel ao novo painel no centro

        // Atualize a exibição para mostrar a página Homepage
        getContentPane().removeAll(); // Remove todos os componentes da janela atual
        getContentPane().add(newPanel, BorderLayout.CENTER); // Adiciona o novo painel à janela
        getContentPane().revalidate(); // Atualiza a exibição
        getContentPane().repaint(); // Repinta a tela para mostrar as alterações

        this.setTitle("Bookings"); // Adiciona um título ao GUI
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Permite fechar o GUI no X
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Define o estado da janela como maximizado
        this.getContentPane().setBackground(new Color(123, 165, 123)); // Define a cor de fundo da janela
        this.setVisible(true); // Torna a janela visível

        // Cria os cabeçalhos da tabela
        String[] columnNames = {"Room", "Adults Capacity", "Children Capacity", "Price"};

        // Cria o modelo da tabela
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        // Adiciona os quartos disponíveis ao modelo da tabela
        for (Room room : availableRooms) {
            model.addRow(new Object[]{room.getRoomNumber(), room.getAdultsCapacity(), room.getChildrenCapacity(), room.getPrice()});
        }

        // Cria a tabela com o modelo criado
        table = new JTable(model);

        // Adiciona a tabela a um JScrollPane para permitir a rolagem se houver muitos quartos
        JScrollPane scrollPane = new JScrollPane(table);

        // Define o layout da janela como BorderLayout
        this.setLayout(new BorderLayout());

        // Adiciona o JScrollPane ao centro da janela
        this.add(scrollPane, BorderLayout.CENTER);

        searchField = new JTextField(20);
        statusField = new JTextField(10);
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterBookings();
            }
        });

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterBookings(); // Chama o método filterBookings() quando o botão de pesquisa for clicado
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Nome do Hóspede:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Status da Reserva:"));
        searchPanel.add(statusField);
        searchPanel.add(searchButton); // Adiciona o botão de pesquisa ao painel de pesquisa
        this.add(searchPanel, BorderLayout.SOUTH);
        searchField.setVisible(false);
        statusField.setVisible(false);
        remove(searchPanel);


        homepageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JPanel homepagePanel = new JPanel(new GridLayout(4, 1)); // GridLayout para organizar em quatro linhas
                addBookingButton.setVisible(false);
                // Tabela para reservas com check-in hoje
                DefaultTableModel todayBookingsTableModel = createCheckInTableModel(); // Usar a nova função para criar o modelo de tabela
                JTable todayBookingsTable = new JTable(todayBookingsTableModel);
                todayBookingsTable.setName("CheckInTable");
                todayBookingsTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
                todayBookingsTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
                JScrollPane todayScrollPane = new JScrollPane(todayBookingsTable);

                // Título "Checking In Today" e a tabela correspondente
                JPanel checkInPanel = new JPanel(new BorderLayout());
                JLabel checkInTitle = new JLabel("Checking In Today");
                checkInTitle.setHorizontalAlignment(SwingConstants.CENTER);
                checkInTitle.setFont(new Font("Arial", Font.BOLD, 18)); // Tamanho e estilo da fonte ajustados
                checkInPanel.add(Box.createVerticalStrut(20)); // Espaço em cima do título
                checkInPanel.add(checkInTitle, BorderLayout.NORTH);
                checkInPanel.add(todayScrollPane, BorderLayout.CENTER);
                homepagePanel.add(checkInPanel);

                // Tabela para reservas com check-out hoje
                DefaultTableModel checkOutTableModel = createCheckOutTableModel(); // Usar a nova função para criar o modelo de tabela
                JTable checkOutTable = new JTable(checkOutTableModel);
                checkOutTable.setName("CheckOutTable");
                checkOutTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
                checkOutTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
                JScrollPane checkOutScrollPane = new JScrollPane(checkOutTable);

                // Título "Checking Out Today" e a tabela correspondente
                JPanel checkOutPanel = new JPanel(new BorderLayout());
                JLabel checkOutTitle = new JLabel("Checking Out Today");
                checkOutTitle.setHorizontalAlignment(SwingConstants.CENTER);
                checkOutTitle.setFont(new Font("Arial", Font.BOLD, 18)); // Tamanho e estilo da fonte ajustados
                checkOutPanel.add(Box.createVerticalStrut(20)); // Espaço em cima do título
                checkOutPanel.add(checkOutTitle, BorderLayout.NORTH);
                checkOutPanel.add(checkOutScrollPane, BorderLayout.CENTER);
                homepagePanel.add(checkOutPanel);

                // Adicione um espaço entre as seções
                homepagePanel.add(new JPanel()); // Adiciona um painel vazio para criar um espaço

                // Crie um novo painel que conterá buttonPanel e homepagePanel
                JPanel newPanel = new JPanel(new BorderLayout());
                newPanel.add(buttonPanel, BorderLayout.NORTH); // Adiciona o buttonPanel ao novo painel na parte superior
                newPanel.add(homepagePanel, BorderLayout.CENTER); // Adiciona o homepagePanel ao novo painel no centro

                // Atualize a exibição para mostrar a página Homepage
                getContentPane().removeAll(); // Remove todos os componentes da janela atual
                getContentPane().add(newPanel, BorderLayout.CENTER); // Adiciona o novo painel à janela
                getContentPane().revalidate(); // Atualiza a exibição
                getContentPane().repaint(); // Repinta a tela para mostrar as alterações
            }
        });

        roomsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Exibe a lista de quartos
                DefaultTableModel roomModel = createRoomTableModel(availableRooms);
                table.setModel(roomModel);
                // Remove o botão de adicionar reserva quando os quartos estiverem sendo exibidos
                buttonPanel.remove(addBookingButton);
                buttonPanel.revalidate();
                buttonPanel.repaint();
                searchField.setVisible(false);
                statusField.setVisible(false);
                remove(searchPanel);

                // Atualize a exibição para mostrar a página de quartos
                getContentPane().removeAll(); // Remove todos os componentes da janela atual
                getContentPane().add(buttonPanel, BorderLayout.NORTH); // Adiciona o buttonPanel de volta à parte superior
                getContentPane().add(scrollPane, BorderLayout.CENTER); // Adiciona a tabela de quartos ao centro
                getContentPane().revalidate(); // Atualiza a exibição
                getContentPane().repaint(); // Repinta a tela para mostrar as alterações
            }
        });

        bookingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBookingButton.setVisible(true);
                // Exibe a lista de reservas (bookings)
                DefaultTableModel bookingModel = createBookingTableModel(bookings);
                table.setModel(bookingModel);
                // Adiciona o botão de adicionar reserva quando as reservas estiverem sendo exibidas
                buttonPanel.add(addBookingButton);
                searchField.setVisible(true);
                statusField.setVisible(true);
                add(searchPanel, BorderLayout.SOUTH);
                buttonPanel.revalidate();
                buttonPanel.repaint();

                // Atualize a exibição para mostrar a página de reservas
                getContentPane().removeAll(); // Remove todos os componentes da janela atual
                getContentPane().add(buttonPanel, BorderLayout.NORTH); // Adiciona o buttonPanel de volta à parte superior
                getContentPane().add(scrollPane, BorderLayout.CENTER); // Adiciona a tabela de reservas ao centro
                getContentPane().add(searchPanel, BorderLayout.SOUTH); // Adiciona o painel de pesquisa na parte inferior
                getContentPane().revalidate(); // Atualiza a exibição
                getContentPane().repaint(); // Repinta a tela para mostrar as alterações
            }
        });

        buttonPanel.add(homepageButton);
        buttonPanel.add(roomsButton);
        buttonPanel.add(bookingsButton);

        // Adiciona os botões acima da tabela
        this.add(buttonPanel, BorderLayout.NORTH);

        this.add(scrollPane, BorderLayout.CENTER);

        // Adiciona um ActionListener à tabela para editar um quarto ou reserva quando selecionado
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    if (table.getModel().getRowCount() > 0) {
                        if (table.getModel().getRowCount() == availableRooms.size()) {
                            editRoom(selectedRow, availableRooms.get(selectedRow));
                        } else if (table.getModel().getRowCount() == bookings.size()) {
                            editBooking(selectedRow, bookings.get(selectedRow));
                        }
                    }
                }
            }
        });

        // Cria o botão "Adicionar Reserva"
        addBookingButton = new JButton("Adicionar Reserva");
        addBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chama o método para adicionar uma nova reserva
                addNewBooking();
            }
        });

        ImageIcon icon = new ImageIcon("logo.jpg"); // Cria um ícone
        this.setIconImage(icon.getImage()); // Adiciona o ícone
    }

    private DefaultTableModel createCheckInTableModel() {
        String[] columnNames = {"Booking ID", "Guest First Name", "Guest Last Name", "Room", "Check-Out", "Action"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Obtém a data atual
        Date currentDate = new Date();

        // Formato da data "dd-MM-yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        // Adiciona as reservas com status de Check-In ao modelo da tabela
        for (Booking booking : bookings) {
            if (booking.getStatusId() == 1 && isSameDay(currentDate, booking.getCheckInDate())) {
                Room room = availableRooms.stream()
                        .filter(r -> r.getId() == booking.getRoomId())
                        .findFirst()
                        .orElse(null);

                if (room != null) {
                    int bookingId = booking.getId();
                    String firstName = booking.getGuestFirstName();
                    String lastName = booking.getGuestLastName();
                    String roomNumber = String.valueOf(room.getRoomNumber());
                    String action = "Check-In";
                    String checkOutdate = dateFormat.format(booking.getCheckOutDate()); // Check-Out para o status de Check-In

                    // Adiciona os valores na ordem correta para cada coluna
                    model.addRow(new Object[]{bookingId, firstName, lastName, roomNumber, checkOutdate, action});
                }
            }
        }
        return model;
    }

    private DefaultTableModel createCheckOutTableModel() {
        String[] columnNames = {"Booking ID", "Guest First Name", "Guest Last Name", "Room", "Check-In", "Action"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Obtém a data atual
        Date currentDate = new Date();

        // Formato da data "dd-MM-yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        // Adiciona as reservas com status de Check-Out ao modelo da tabela
        for (Booking booking : bookings) {
            if (booking.getStatusId() == 2 && isSameDay(currentDate, booking.getCheckOutDate())) {
                Room room = availableRooms.stream()
                        .filter(r -> r.getId() == booking.getRoomId())
                        .findFirst()
                        .orElse(null);

                if (room != null) {
                    int bookingId = booking.getId();
                    String firstName = booking.getGuestFirstName();
                    String lastName = booking.getGuestLastName();
                    String roomNumber = String.valueOf(room.getRoomNumber());
                    String action = "Check-Out";
                    String checkInDate = dateFormat.format(booking.getCheckInDate()); // Check-In para o status de Check-Out

                    // Adiciona os valores na ordem correta para cada coluna
                    model.addRow(new Object[]{bookingId, firstName, lastName, roomNumber, checkInDate, action});
                }
            }
        }
        return model;
    }

    // Método para verificar se duas datas são do mesmo dia
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    // Classe ButtonRenderer para renderizar o botão na célula da tabela
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Verifica se o nome da tabela não é nulo e se é igual a "CheckInTable"
            if (table.getName() != null && table.getName().equals("CheckInTable")) {
                setText("Check-In"); // Defina o texto do botão como "Check-In" para a tabela de "Reservas com Check-In Hoje"
            } else if (table.getName() != null && table.getName().equals("CheckOutTable")) {
                setText("Check-Out"); // Defina o texto do botão como "Check-Out" para a tabela de "Reservas com Check-Out Hoje"
            }
            return this;
        }
    }

    // Classe ButtonEditor para permitir a edição do botão na célula da tabela
    class ButtonEditor extends DefaultCellEditor {

        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int bookingId = (int) table.getValueAt(row, 0); // Obtém o ID do Booking na linha clicada

                    // Encontra a reserva com o ID correspondente
                    Booking selectedBooking = null;
                    for (Booking booking : bookings) {
                        if (booking.getId() == bookingId) {
                            selectedBooking = booking;
                            break;
                        }
                    }

                    if (selectedBooking != null) {
                        if (table.getName().equals("CheckInTable")) {
                            if (selectedBooking.getStatusId() == 1) { // Verifica se o status é Booked (1)
                                selectedBooking.setStatusId(2); // Define o status como CheckedIn (2)
                                JOptionPane.showMessageDialog(null, "Check-In realizado com sucesso!");
                            } else {
                                JOptionPane.showMessageDialog(null, "Esta reserva não está no estado correto para Check-In.");
                            }
                        } else if (table.getName().equals("CheckOutTable")) {
                            if (selectedBooking.getStatusId() == 2) { // Verifica se o status é CheckedIn (2)
                                selectedBooking.setStatusId(3); // Define o status como CheckedOut (3)
                                JOptionPane.showMessageDialog(null, "Check-Out realizado com sucesso!");
                            } else {
                                JOptionPane.showMessageDialog(null, "Esta reserva não está no estado correto para Check-Out.");
                            }
                        }
                        updateTable(table, table.getName());
                    } else {
                        JOptionPane.showMessageDialog(null, "Nenhuma reserva encontrada com o ID selecionado.");
                    }
                    fireEditingStopped();
                }
            });
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

        private void updateTable(JTable table, String tableName) {
            if (tableName.equals("CheckInTable")) {
                updateTodayBookingsTable(table);
            } else if (tableName.equals("CheckOutTable")) {
                updateCheckOutTable(table);
            }
        }

        private void updateTodayBookingsTable(JTable table) {
            DefaultTableModel todayBookingsTableModel = createCheckInTableModel();
            updateTableModel(table, todayBookingsTableModel);
        }

        private void updateCheckOutTable(JTable table) {
            DefaultTableModel checkOutTableModel = createCheckOutTableModel();
            updateTableModel(table, checkOutTableModel);
        }

        private void updateTableModel(JTable table, DefaultTableModel model) {
            table.setModel(model);
            table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
            table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        }

        private JScrollPane findScrollPane(JPanel panel) {
            for (Component component : panel.getComponents()) {
                if (component instanceof JScrollPane) {
                    return (JScrollPane) component;
                }
            }
            return null;
        }
    }

    private void filterBookings() {
        String searchText = searchField.getText().trim().toLowerCase(); // Obtém o texto de pesquisa e o converte para minúsculas
        String statusText = statusField.getText().trim().toLowerCase(); // Obtém o texto do status e o converte para minúsculas

        // Mapeia os textos de status para seus respectivos IDs
        Map<String, Integer> statusMap = new HashMap<>();
        statusMap.put("booked", 1);
        statusMap.put("checkedin", 2);
        statusMap.put("checkedout", 3);
        statusMap.put("canceled", 4);

        // Obtém o ID do status a partir do texto fornecido
        int statusId = statusMap.getOrDefault(statusText, -1);

        // Filtra as reservas com base no texto de pesquisa e status
        List<Booking> filteredBookings = bookings.stream()
                .filter(booking ->
                        booking.getGuestFirstName().toLowerCase().contains(searchText) ||
                                booking.getGuestLastName().toLowerCase().contains(searchText))
                .filter(booking ->
                        statusId == -1 || booking.getStatusId() == statusId) // Filtra as reservas se o statusId for -1 (ou seja, não foi fornecido um número válido) ou se o ID do status for igual ao statusId fornecido
                .collect(Collectors.toList()); // Coleta as reservas filtradas em uma lista

        // Cria um modelo de tabela com as reservas filtradas
        DefaultTableModel bookingModel = createBookingTableModel(filteredBookings);
        // Define o modelo de tabela com as reservas filtradas na tabela
        table.setModel(bookingModel);
    }


    // Método auxiliar para criar o modelo da tabela de quartos
    private DefaultTableModel createRoomTableModel(List<Room> rooms) {
        String[] columnNames = {"Room", "Adults Capacity", "Children Capacity", "Price"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Room room : rooms) {
            Object[] rowData = {room.getRoomNumber(), room.getAdultsCapacity(), room.getChildrenCapacity(), room.getPrice()};
            model.addRow(rowData);
        }
        return model;
    }

    private DefaultTableModel createBookedBookingTableModelHomeCheckIn(List<Booking> bookings) {
        String[] columnNames = {"Guest First Name", "Guest Last Name", "Room", "Check-out"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Formato desejado para as datas

        for (Booking booking : bookings) {
            // Check if the booking status is BOOKED (status ID equals 1)
            if (booking.getStatusId() == 1) {
                // Obtém o objeto Room correspondente ao ID do quarto na reserva
                Room room = availableRooms.stream()
                        .filter(r -> r.getId() == booking.getRoomId())
                        .findFirst()
                        .orElse(null);

                // Verifica se o objeto Room foi encontrado
                String roomNumber = (room != null) ? String.valueOf(room.getRoomNumber()) : "N/A";

                // Formata as datas de check-in e check-out para o formato desejado
                String checkOutDateFormatted = dateFormat.format(booking.getCheckOutDate());

                Object[] rowData = {booking.getGuestFirstName(), booking.getGuestLastName(), roomNumber, checkOutDateFormatted};
                model.addRow(rowData);
            }
        }
        return model;
    }

    private DefaultTableModel createBookingTableModel(List<Booking> bookings) {
        String[] columnNames = {"Guest First Name", "Guest Last Name", "Room", "Check-in", "Check-out", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Formato desejado para as datas

        for (Booking booking : bookings) {
            // Obtém o objeto Room correspondente ao ID do quarto na reserva
            Room room = availableRooms.stream()
                    .filter(r -> r.getId() == booking.getRoomId())
                    .findFirst()
                    .orElse(null);

            // Verifica se o objeto Room foi encontrado
            String roomNumber = (room != null) ? String.valueOf(room.getRoomNumber()) : "N/A";

            // Formata as datas de check-in e check-out para o formato desejado
            String checkInDateFormatted = dateFormat.format(booking.getCheckInDate());
            String checkOutDateFormatted = dateFormat.format(booking.getCheckOutDate());

            // Obtém o estado da reserva
            String statusString = booking.status.getState(); // Corrigido para chamar getStatus() e então getState()

            Object[] rowData = {booking.getGuestFirstName(), booking.getGuestLastName(), roomNumber, checkInDateFormatted, checkOutDateFormatted, statusString};
            model.addRow(rowData);
        }
        return model;
    }

    // Método para editar um quarto
    private void editRoom(int rowIndex, Room room) {
        JFrame editFrame = new JFrame("Editar Quarto");
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.setSize(300, 200);
        editFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2));

        JLabel roomLabel = new JLabel("Room Number:");
        JTextField roomField = new JTextField(String.valueOf(room.getRoomNumber()));
        JLabel adultsLabel = new JLabel("Adults Capacity:");
        JTextField adultsField = new JTextField(String.valueOf(room.getAdultsCapacity()));
        JLabel childrenLabel = new JLabel("Children Capacity:");
        JTextField childrenField = new JTextField(String.valueOf(room.getChildrenCapacity()));
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(String.valueOf(room.getPrice()));

        panel.add(roomLabel);
        panel.add(roomField);
        panel.add(adultsLabel);
        panel.add(adultsField);
        panel.add(childrenLabel);
        panel.add(childrenField);
        panel.add(priceLabel);
        panel.add(priceField);

        JButton saveButton = new JButton("Salvar");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                room.setRoomNumber(Integer.parseInt(roomField.getText()));
                room.setAdultsCapacity(Integer.parseInt(adultsField.getText()));
                room.setChildrenCapacity(Integer.parseInt(childrenField.getText()));
                room.setPrice(Float.parseFloat(priceField.getText()));
                table.getModel().setValueAt(room.getRoomNumber(), rowIndex, 0);
                table.getModel().setValueAt(room.getAdultsCapacity(), rowIndex, 1);
                table.getModel().setValueAt(room.getChildrenCapacity(), rowIndex, 2);
                table.getModel().setValueAt(room.getPrice(), rowIndex, 3);
                editFrame.dispose();
            }
        });

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editFrame.dispose();
            }
        });

        panel.add(saveButton);
        panel.add(cancelButton);

        editFrame.add(panel);
        editFrame.setVisible(true);
    }


    private void editBooking(int rowIndex, Booking booking) {
        JFrame editFrame = new JFrame(booking.getGuestFirstName() + " " + booking.getGuestLastName());
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.setSize(600, 300);
        editFrame.setLocationRelativeTo(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        JPanel panel = new JPanel(new GridLayout(0, 2));

        JLabel firstNameLabel = new JLabel("Guest First Name:");
        JTextField firstNameField = new JTextField(booking.getGuestFirstName());

        JLabel lastNameLabel = new JLabel("Guest Last Name:");
        JTextField lastNameField = new JTextField(booking.getGuestLastName());

        JLabel checkInLabel = new JLabel("Check-in:");
        JTextField checkInField = new JTextField(dateFormat.format(booking.getCheckInDate()));

        JLabel checkOutLabel = new JLabel("Check-out:");
        JTextField checkOutField = new JTextField(dateFormat.format(booking.getCheckOutDate()));

        JLabel adultsLabel = new JLabel("Adults:");
        JTextField adultsField = new JTextField(String.valueOf(booking.getNumberOfAdults()));

        JLabel childrenLabel = new JLabel("Children:");
        JTextField childrenField = new JTextField(String.valueOf(booking.getNumberOfChildren()));

        JLabel roomLabel = new JLabel("Room: ");
        Room room = availableRooms.stream()
                .filter(r -> r.getId() == booking.getRoomId())
                .findFirst()
                .orElse(null);
        String roomInfo = (room != null) ? room.getRoomNumber() + " at $" + room.getPrice() + " per night" : "N/A";
        roomLabel.setText(roomLabel.getText() + roomInfo);

        // Defina todos os campos de texto inicialmente editáveis
        JTextField[] textFields = {firstNameField, lastNameField, checkInField, checkOutField, adultsField, childrenField};
        for (JTextField field : textFields) {
            field.setEditable(true);
        }

        panel.add(firstNameLabel);
        panel.add(firstNameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(checkInLabel);
        panel.add(checkInField);
        panel.add(checkOutLabel);
        panel.add(checkOutField);
        panel.add(adultsLabel);
        panel.add(adultsField);
        panel.add(childrenLabel);
        panel.add(childrenField);
        panel.add(roomLabel);

        JButton getRoomButton = new JButton("Get Available Room");
        JButton checkInButton = new JButton("Check-in");
        JButton checkOutButton = new JButton("Check-out");
        JButton cancelBookingButton = new JButton("Cancelar Reserva");
        JButton saveChangesButton = new JButton("Salvar Alterações");

        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                booking.setStatusId(2);
                table.getModel().setValueAt(booking.status.getState(), rowIndex, 5);
                // Após o check-in, torna todos os campos de texto não editáveis
                for (JTextField field : textFields) {
                    field.setEditable(false);
                }
                // Oculta o botão "Check-in" após o check-in ser feito
                checkInButton.setVisible(false);
                checkOutButton.setVisible(true);
                saveChangesButton.setVisible(false); // Esconde o botão de salvar após o check-in
                editFrame.dispose();
            }
        });

        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                booking.setStatusId(3);
                table.getModel().setValueAt(booking.status.getState(), rowIndex, 5);
                editFrame.dispose();
            }
        });

        // Define o botão "Cancelar Reserva" como visível apenas se o status for "Reservado"
        cancelBookingButton.setVisible(booking.getStatusId() == 1);
        cancelBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                booking.setStatusId(4);
                table.getModel().setValueAt(booking.status.getState(), rowIndex, 5);
                cancelBookingButton.setVisible(false);
                editFrame.dispose();
            }
        });

        // Define o botão "Salvar Alterações" como visível apenas se o status for "Reservado"
        saveChangesButton.setVisible(booking.getStatusId() == 1);
        saveChangesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Salvar as alterações aqui
                booking.setGuestFirstName(firstNameField.getText());
                booking.setGuestLastName(lastNameField.getText());
                // Você precisa tratar exceções para a conversão de data aqui
                try {
                    booking.setCheckInDate(dateFormat.parse(checkInField.getText()));
                    booking.setCheckOutDate(dateFormat.parse(checkOutField.getText()));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                    // Trate o erro de formatação de data adequadamente aqui
                }
                booking.setNumberOfAdults(Integer.parseInt(adultsField.getText()));
                booking.setNumberOfChildren(Integer.parseInt(childrenField.getText()));

                // Atualizar a informação do quarto com base nas informações atualizadas
                int numberOfAdults = Integer.parseInt(adultsField.getText());
                int numberOfChildren = Integer.parseInt(childrenField.getText());
                Date checkInDate = null;
                Date checkOutDate = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    checkInDate = dateFormat.parse(checkInField.getText());
                    checkOutDate = dateFormat.parse(checkOutField.getText());
                } catch (ParseException ex) {
                    ex.printStackTrace(); // Ou tratamento apropriado para a exceção
                }
                Room updatedRoom = getAvailableRoom(availableRooms, bookings, numberOfAdults, numberOfChildren, checkInDate, checkOutDate);
                if (updatedRoom != null) {
                    // Atualiza o quarto na reserva diretamente
                    booking.setRoomId(updatedRoom.getId());
                    String roomInfo = "Room " + updatedRoom.getRoomNumber() + " at $" + updatedRoom.getPrice() + " per night";
                    roomLabel.setText(roomInfo); // Atualiza a informação do quarto na interface do usuário
                } else {
                    String roomInfo = "N/A";
                    roomLabel.setText(roomInfo); // Atualiza a informação do quarto na interface do usuário
                }

                // Atualizar a tabela, se necessário
                // table.getModel().fireTableDataChanged(); // se necessário, depende da implementação da sua tabela

                // Feedback ou mensagem de confirmação, se desejado
                JOptionPane.showMessageDialog(editFrame, "Alterações salvas com sucesso!");
            }
        });

        // Torna o botão "Check-in" inicialmente visível se o status for "Reservado"
        // e o botão "Check-out" visível se o status for "Check-in"
        checkInButton.setVisible(booking.getStatusId() == 1);
        checkOutButton.setVisible(booking.getStatusId() == 2);

        if (booking.getStatusId() == 2 || booking.getStatusId() == 3) {
            for (JTextField field : textFields) {
                field.setEditable(false);
            }
        }

        // Adiciona um ActionListener ao botão "Get Available Room"
        getRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtém os valores atualizados dos campos de entrada
                int numberOfAdults = Integer.parseInt(adultsField.getText());
                int numberOfChildren = Integer.parseInt(childrenField.getText());
                Date checkInDate = null;
                Date checkOutDate = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    checkInDate = dateFormat.parse(checkInField.getText());
                    checkOutDate = dateFormat.parse(checkOutField.getText());
                } catch (ParseException ex) {
                    ex.printStackTrace(); // Ou tratamento apropriado para a exceção
                }

                // Obtém o quarto disponível mais barato com base nas informações atualizadas
                Room availableRoom = getAvailableRoom(availableRooms, bookings, numberOfAdults, numberOfChildren, checkInDate, checkOutDate);
                if (availableRoom != null) {
                    String roomInfo = "Room " + availableRoom.getRoomNumber() + " at $" + availableRoom.getPrice() + " per night";
                    roomLabel.setText(roomInfo); // Define a informação do quarto como texto da roomInfoLabel
                } else {
                    roomLabel.setText("N/A"); // Define "N/A" se nenhum quarto estiver disponível
                }
            }
        });

        // Define a visibilidade do botão "Get Available Room" com base no status da reserva
        if (booking.getStatusId() == 1) { // Reservado
            getRoomButton.setVisible(true);
        } else {
            getRoomButton.setVisible(false);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(getRoomButton);
        buttonPanel.add(getRoomButton);
        buttonPanel.add(checkInButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(cancelBookingButton);
        buttonPanel.add(saveChangesButton);

        editFrame.add(panel, BorderLayout.CENTER);
        editFrame.add(buttonPanel, BorderLayout.SOUTH);
        editFrame.setVisible(true);
    }

    private void addNewBooking() {
        JFrame addFrame = new JFrame("Adicionar Reserva");
        addFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addFrame.setSize(400, 400);
        addFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2));

        // Campos para os dados da reserva
        JLabel firstNameLabel = new JLabel("Guest First Name:");
        JTextField firstNameField = new JTextField();
        JLabel lastNameLabel = new JLabel("Guest Last Name:");
        JTextField lastNameField = new JTextField();
        JLabel checkInLabel = new JLabel("Check-in:");
        JTextField checkInField = new JTextField();
        JLabel checkOutLabel = new JLabel("Check-out:");
        JTextField checkOutField = new JTextField();
        JLabel adultsLabel = new JLabel("Adults:");
        JTextField adultsField = new JTextField();
        JLabel childrenLabel = new JLabel("Children:");
        JTextField childrenField = new JTextField();
        JLabel roomLabel = new JLabel("Room:");
        JLabel roomInfoLabel = new JLabel();
        JButton getRoomButton = new JButton("Get Available Room");

        // Adiciona os campos ao painel
        panel.add(firstNameLabel);
        panel.add(firstNameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(checkInLabel);
        panel.add(checkInField);
        panel.add(checkOutLabel);
        panel.add(checkOutField);
        panel.add(adultsLabel);
        panel.add(adultsField);
        panel.add(childrenLabel);
        panel.add(childrenField);
        panel.add(roomLabel);
        panel.add(roomInfoLabel); // Adiciona a label de informações do quarto


        // Botões para salvar ou cancelar
        JButton saveButton = new JButton("Salvar");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String guestFirstName = firstNameField.getText();
                String guestLastName = lastNameField.getText();
                int statusId = 1; // Assuming default status ID
                int numberOfAdults = Integer.parseInt(adultsField.getText());
                int numberOfChildren = Integer.parseInt(childrenField.getText());

                Date checkInDate = null;
                Date checkOutDate = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                try {
                    checkInDate = dateFormat.parse(checkInField.getText());
                    checkOutDate = dateFormat.parse(checkOutField.getText());
                } catch (ParseException ex) {
                    ex.printStackTrace(); // Ou tratamento apropriado para a exceção
                }

                // Obtém o quarto disponível mais barato
                Room availableRoom = getAvailableRoom(availableRooms, bookings, numberOfAdults, numberOfChildren, checkInDate, checkOutDate);
                if (availableRoom != null) {
                    int roomId = availableRoom.getId();
                    Booking newBooking = new Booking(guestFirstName, guestLastName, checkInDate, checkOutDate, numberOfAdults, numberOfChildren, roomId, statusId);
                    bookings.add(newBooking);

                    // Atualize a tabela com a nova reserva
                    updateBookingTable(); // Supondo que você tenha um método para atualizar a tabela

                    // Feche a janela de adição de reserva
                    addFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(addFrame, "Nenhum quarto disponível encontrado para as datas selecionadas.");
                }
            }
        });

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Feche a janela de adição de reserva
                addFrame.dispose();
            }
        });

        // Adiciona os botões ao painel
        panel.add(getRoomButton);
        panel.add(saveButton);
        panel.add(cancelButton);
        // Adiciona o painel ao frame e torna visível
        addFrame.add(panel);
        addFrame.setVisible(true);

        // Adiciona um ActionListener ao botão "Get Available Room"
        getRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtém os valores dos campos de entrada
                int numberOfAdults = Integer.parseInt(adultsField.getText());
                int numberOfChildren = Integer.parseInt(childrenField.getText());
                Date checkInDate = null;
                Date checkOutDate = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    checkInDate = dateFormat.parse(checkInField.getText());
                    checkOutDate = dateFormat.parse(checkOutField.getText());
                } catch (ParseException ex) {
                    ex.printStackTrace(); // Ou tratamento apropriado para a exceção
                }

                // Obtém o quarto disponível mais barato
                // Obtém o quarto disponível mais barato
                Room availableRoom = getAvailableRoom(availableRooms, bookings, numberOfAdults, numberOfChildren, checkInDate, checkOutDate);
                if (availableRoom != null) {
                    String roomInfo = "Room " + availableRoom.getRoomNumber() + " at $" + availableRoom.getPrice() + " per night";
                    roomInfoLabel.setText(roomInfo); // Define a informação do quarto como texto da roomInfoLabel
                } else {
                    roomInfoLabel.setText("N/A"); // Define "N/A" se nenhum quarto estiver disponível
                }
            }
        });
    }

    // Método para obter o quarto disponível mais barato
    private Room getAvailableRoom(List<Room> rooms, List<Booking> bookings, int numberOfAdults, int numberOfChildren, Date checkInDate, Date checkOutDate) {
        List<Room> availableRooms = searchAvailableRooms(rooms, bookings, numberOfAdults, numberOfChildren, checkInDate, checkOutDate, 0);
        if (!availableRooms.isEmpty()) {
            Room cheapestRoom = availableRooms.stream()
                    .min(Comparator.comparing(Room::getPrice))
                    .orElse(null);
            return cheapestRoom;
        }
        return null; // Retorna null se nenhum quarto disponível for encontrado
    }

    // Método auxiliar para pesquisar quartos disponíveis
    public List<Room> searchAvailableRooms(List<Room> rooms, List<Booking> bookings, int numberOfAdults, int numberOfChildren, Date checkInDate, Date checkOutDate, int canceledStatus) {
        return rooms.stream()
                .filter(room -> room.getAdultsCapacity() >= numberOfAdults && (room.getAdultsCapacity() + room.getChildrenCapacity()) >= (numberOfAdults + numberOfChildren))
                .filter(room -> {
                    // Verifica se há alguma sobreposição de datas de reserva no mesmo quarto
                    for (Booking booking : bookings) {
                        if (booking.getRoomId() == room.getId()) {
                            // Verifica se as datas da nova reserva sobrepõem as datas da reserva existente
                            if ((checkInDate.before(booking.getCheckOutDate()) || checkInDate.equals(booking.getCheckOutDate())) &&
                                    (checkOutDate.after(booking.getCheckInDate()) || checkOutDate.equals(booking.getCheckInDate()))) {
                                return false; // O quarto não está disponível para as datas selecionadas
                            }
                        }
                    }
                    return true; // O quarto está disponível para as datas selecionadas
                })
                .sorted(Comparator.comparing(Room::getPrice))
                .collect(Collectors.toList());
    }

    private void updateBookingTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Limpa todas as linhas da tabela

        // Preenche a tabela com as reservas atualizadas
        for (Booking booking : bookings) {
            // Obtém o objeto Room correspondente ao ID do quarto na reserva
            Room room = availableRooms.stream()
                    .filter(r -> r.getId() == booking.getRoomId())
                    .findFirst()
                    .orElse(null);

            // Verifica se o objeto Room foi encontrado
            String roomNumber = (room != null) ? String.valueOf(room.getRoomNumber()) : "N/A";

            Object[] rowData = {
                    booking.getGuestFirstName(),
                    booking.getGuestLastName(),
                    roomNumber, // Exibe o número real do quarto
                    formatDate(booking.getCheckInDate()), // Formata a data de check-in
                    formatDate(booking.getCheckOutDate()), // Formata a data de check-out
                    booking.getStatusId(),
                    booking.getNumberOfAdults(),
                    booking.getNumberOfChildren()
            };
            model.addRow(rowData);
        }
    }

    // Método auxiliar para formatar a data para "dd-MM-yyyy"
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

}
