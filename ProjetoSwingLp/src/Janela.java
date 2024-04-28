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
        this.setResizable(true); // Permite que o usuário defina o tamanho da janela
        this.setSize(900, 600); // Define o tamanho inicial da janela
        this.getContentPane().setBackground(new Color(123, 165, 123)); // Define a cor de fundo da janela
        this.setVisible(true);

        // Define a cor de fundo da janela
        this.getContentPane().setBackground(new Color(123, 165, 123)); // Define a cor de fundo da janela

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

        // Adiciona botões para alternar entre quartos e reservas (bookings)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton homepageButton = new JButton("Homepage");
        JButton roomsButton = new JButton("Quartos");
        JButton bookingsButton = new JButton("Reservas");

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
                JPanel homepagePanel = new JPanel(new GridLayout(2, 1)); // Um GridLayout para organizar duas tabelas em colunas

                // Tabela para reservas com check-in hoje
                DefaultTableModel todayBookingsTableModel = createRestrictedBookingsTableModel(1); // statusID 1 (Booked)
                JTable todayBookingsTable = new JTable(todayBookingsTableModel);
                todayBookingsTable.setName("CheckInTable");
                todayBookingsTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                todayBookingsTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
                JScrollPane todayScrollPane = new JScrollPane(todayBookingsTable);
                homepagePanel.add(new JLabel("Reservas com Check-In Hoje (Booked)"));
                homepagePanel.add(todayScrollPane);

                // Tabela para reservas com check-out hoje
                DefaultTableModel checkInTableModel = createRestrictedBookingsTableModel(2); // statusID 2 (CheckIn)
                JTable checkInTable = new JTable(checkInTableModel);
                checkInTable.setName("CheckOutTable");
                checkInTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
                checkInTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
                JScrollPane checkInScrollPane = new JScrollPane(checkInTable);
                homepagePanel.add(new JLabel("Reservas com Check-Out Hoje (CheckIn)"));
                homepagePanel.add(checkInScrollPane);

                // Crie um novo painel que conterá buttonPanel e homepagePanel
                JPanel newPanel = new JPanel(new BorderLayout());
                newPanel.add(buttonPanel, BorderLayout.NORTH); // Adicione o buttonPanel ao novo painel na parte superior
                newPanel.add(homepagePanel, BorderLayout.CENTER); // Adicione o homepagePanel ao novo painel no centro

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

    // Método para criar o modelo da tabela restrita aos status específicos
    private DefaultTableModel createRestrictedBookingsTableModel(int statusId) {
        String[] columnNames = {"Guest First Name", "Guest Last Name", "Room", "Action"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Obtém a data atual
        Date currentDate = new Date();

        // Adiciona as reservas com status específico para o dia atual ao modelo da tabela
        for (Booking booking : bookings) {
            if (booking.getStatusId() == statusId) {
                Date checkInDate = booking.getCheckInDate();
                Date checkOutDate = booking.getCheckOutDate();

                // Verifica se a reserva tem check-in ou check-out para o dia atual
                if (isSameDay(currentDate, checkInDate) || isSameDay(currentDate, checkOutDate)) {
                    // Encontre o quarto correspondente à reserva
                    Room room = availableRooms.stream()
                            .filter(r -> r.getId() == booking.getRoomId())
                            .findFirst()
                            .orElse(null);

                    // Verifica se o quarto foi encontrado e adicione o número do quarto à tabela
                    if (room != null) {
                        model.addRow(new Object[]{booking.getGuestFirstName(), booking.getGuestLastName(), room.getRoomNumber(), ""});
                    }
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
            // Verifica se o botão está na tabela de "Reservas com Check-In Hoje" ou na tabela de "Reservas com Check-Out Hoje"
            if (table.getName().equals("CheckInTable")) {
                setText("Check-In"); // Defina o texto do botão como "Check-In" para a tabela de "Reservas com Check-In Hoje"
            } else if (table.getName().equals("CheckOutTable")) {
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

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int editingRow = table.getEditingRow();
                    if (editingRow != -1) {
                        int modelRow = table.convertRowIndexToModel(editingRow);
                        if (modelRow >= 0 && modelRow < bookings.size()) {
                            Booking selectedBooking = bookings.get(modelRow);
                            // Verifica se o botão está na tabela de "Reservas com Check-In Hoje"
                            if (table.getName().equals("CheckInTable")) {
                                selectedBooking.setStatusId(2); // Atualiza o status para Check-In
                                // Exibe uma mensagem de confirmação
                                JOptionPane.showMessageDialog(Janela.this, "Check-In realizado com sucesso!");
                            } else if (table.getName().equals("CheckOutTable")) {
                                selectedBooking.setStatusId(3); // Atualiza o status para Check-Out
                                // Exibe uma mensagem de confirmação
                                JOptionPane.showMessageDialog(Janela.this, "Check-Out realizado com sucesso!");
                            }
                            // Atualiza as tabelas
                            updateHomepageTable();
                            updateBookingsTable();
                        }
                    }
                    fireEditingStopped(); // Notifica que a edição foi interrompida
                }
            });
            return button;
        }

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
        }

        // Método para atualizar a tabela de homepage
        private void updateHomepageTable() {
            System.out.println("Atualizando tabela de homepage..."); // Adiciona mensagem de log

            DefaultTableModel todayBookingsTableModel = createRestrictedBookingsTableModel(1);
            JTable todayBookingsTable = new JTable(todayBookingsTableModel);
            todayBookingsTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
            todayBookingsTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
            JScrollPane todayScrollPane = new JScrollPane(todayBookingsTable);

            // Substitui a tabela de homepage pela nova tabela atualizada
            JPanel homepagePanel = (JPanel) getContentPane().getComponent(0);
            homepagePanel.remove(1); // Remove a tabela antiga
            homepagePanel.add(todayScrollPane); // Adiciona a nova tabela

            // Revalida e repinta o painel para garantir que a nova tabela seja exibida corretamente
            homepagePanel.revalidate();
            homepagePanel.repaint();
        }

        // Método para atualizar a tabela de reservas (bookings)
        private void updateBookingsTable() {
            System.out.println("Atualizando tabela de reservas (bookings)..."); // Adiciona mensagem de log

            DefaultTableModel bookingModel = createBookingTableModel(bookings);
            table.setModel(bookingModel);
            table.revalidate();
            table.repaint();
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
        editFrame.setSize(400, 300);
        editFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2));

        JLabel firstNameLabel = new JLabel("Guest First Name:");
        JTextField firstNameField = new JTextField(booking.getGuestFirstName());

        JLabel lastNameLabel = new JLabel("Guest Last Name:");
        JTextField lastNameField = new JTextField(booking.getGuestLastName());

        JLabel checkInLabel = new JLabel("Check-in:");
        JTextField checkInField = new JTextField(booking.getCheckInDate().toString());

        JLabel checkOutLabel = new JLabel("Check-out:");
        JTextField checkOutField = new JTextField(booking.getCheckOutDate().toString());

        JLabel statusLabel = new JLabel("Status:");
        JTextField statusField = new JTextField(booking.status.getState());

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
        JTextField[] textFields = {firstNameField, lastNameField, checkInField, checkOutField, statusField, adultsField, childrenField};
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
        panel.add(statusLabel);
        panel.add(statusField);
        panel.add(adultsLabel);
        panel.add(adultsField);
        panel.add(childrenLabel);
        panel.add(childrenField);
        panel.add(roomLabel);

        JButton checkInButton = new JButton("Check-in");
        JButton checkOutButton = new JButton("Check-out");
        JButton cancelBookingButton = new JButton("Cancelar Reserva");

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

        // Torna o botão "Check-in" inicialmente visível se o status for "Reservado"
        // e o botão "Check-out" visível se o status for "Check-in"
        checkInButton.setVisible(booking.getStatusId() == 1);
        checkOutButton.setVisible(booking.getStatusId() == 2);

        if (booking.getStatusId() == 2 || booking.getStatusId() == 3) {
            for (JTextField field : textFields) {
                field.setEditable(false);
            }

        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(checkInButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(cancelBookingButton);

        editFrame.add(panel, BorderLayout.CENTER);
        editFrame.add(buttonPanel, BorderLayout.SOUTH);
        editFrame.setVisible(true);
    }

    private void addNewBooking() {
        JFrame addFrame = new JFrame("Adicionar Reserva");
        addFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addFrame.setSize(300, 400);
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
        JTextField roomField = new JTextField();
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();
        priceField.setEditable(false);
        roomField.setEditable(false);
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
        panel.add(roomField);
        panel.add(priceLabel);
        panel.add(priceField);
        panel.add(getRoomButton); // Adiciona o botão "Get Available Room"

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
                    Booking newBooking = new Booking(0, guestFirstName, guestLastName, checkInDate, checkOutDate, numberOfAdults, numberOfChildren, roomId, statusId);
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
                Room availableRoom = getAvailableRoom(availableRooms, bookings, numberOfAdults, numberOfChildren, checkInDate, checkOutDate);
                if (availableRoom != null) {
                    roomField.setText(String.valueOf(availableRoom.getRoomNumber()));// Exibe o número real do quarto
                    priceField.setText(String.valueOf(availableRoom.getPrice())); // Exibe o preço do quarto
                } else {
                    JOptionPane.showMessageDialog(addFrame, "Nenhum quarto disponível encontrado para as datas selecionadas.");
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
                .filter(room -> bookings.stream().noneMatch(booking -> booking.getRoomId() == room.getId() &&
                        ((checkInDate.compareTo(booking.getCheckInDate()) >= 0 && checkInDate.compareTo(booking.getCheckOutDate()) <= 0) ||
                                (checkOutDate.compareTo(booking.getCheckInDate()) >= 0 && checkOutDate.compareTo(booking.getCheckOutDate()) <= 0)) &&
                        booking.getStatusId() != canceledStatus))
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
