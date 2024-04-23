import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

            }
        });

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

    private void filterBookings() {
        String searchText = searchField.getText().trim().toLowerCase(); // Obtém o texto de pesquisa e o converte para minúsculas
        String statusText = statusField.getText().trim(); // Obtém o texto do status

        // Verifica se o statusText é um número inteiro
        int statusId;
        try {
            statusId = Integer.parseInt(statusText); // Tenta converter o texto do status para um número inteiro
        } catch (NumberFormatException e) {
            // Se não for possível converter para um número, define o statusId como -1
            statusId = -1;
        }

        // Filtra as reservas com base no texto de pesquisa e status
        int finalStatusId = statusId;
        List<Booking> filteredBookings = bookings.stream()
                .filter(booking ->
                        booking.getGuestFirstName().toLowerCase().contains(searchText) ||
                                booking.getGuestLastName().toLowerCase().contains(searchText))
                .filter(booking ->
                        finalStatusId == -1 || booking.getStatusId() == finalStatusId) // Filtra as reservas se o statusId for -1 (ou seja, não foi fornecido um número válido) ou se o ID do status for igual ao statusId fornecido
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

    // Método auxiliar para criar o modelo da tabela de reservas (bookings)
    private DefaultTableModel createBookingTableModel(List<Booking> bookings) {
        String[] columnNames = {"Guest First Name", "Guest Last Name", "Room", "Check-in", "Check-out", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Formato desejado para as datas

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

            Object[] rowData = {booking.getGuestFirstName(), booking.getGuestLastName(), roomNumber, checkInDateFormatted, checkOutDateFormatted, booking.getStatusId()};
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

    // Método para editar uma reserva
    private void editBooking(int rowIndex, Booking booking) {
        JFrame editFrame = new JFrame("Editar Reserva");
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.setSize(300, 250);
        editFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2));

        JLabel firstNameLabel = new JLabel("Guest First Name:");
        JTextField firstNameField = new JTextField(booking.getGuestFirstName());
        JLabel lastNameLabel = new JLabel("Guest Last Name:");
        JTextField lastNameField = new JTextField(booking.getGuestLastName());
        JLabel roomLabel = new JLabel("Room:");
        JTextField roomField = new JTextField(String.valueOf(booking.getRoomId()));
        JLabel checkInLabel = new JLabel("Check-in:");
        JTextField checkInField = new JTextField(booking.getCheckInDate().toString());
        JLabel checkOutLabel = new JLabel("Check-out:");
        JTextField checkOutField = new JTextField(booking.getCheckOutDate().toString());
        JLabel statusLabel = new JLabel("Status:");
        JTextField statusField = new JTextField(String.valueOf(booking.getStatusId()));
        JLabel adultsLabel = new JLabel("Adults:");
        JTextField adultsField = new JTextField(String.valueOf(booking.getNumberOfAdults()));
        JLabel childrenLabel = new JLabel("Children:");
        JTextField childrenField = new JTextField(String.valueOf(booking.getNumberOfChildren()));

        panel.add(firstNameLabel);
        panel.add(firstNameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(roomLabel);
        panel.add(roomField);
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

        JButton saveButton = new JButton("Salvar");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                booking.setGuestFirstName(firstNameField.getText());
                booking.setGuestLastName(lastNameField.getText());
                booking.setRoomId(Integer.parseInt(roomField.getText()));
                // Você precisa lidar com a conversão de datas e configurá-las adequadamente
                booking.setStatusId(Integer.parseInt(statusField.getText()));
                booking.setNumberOfAdults(Integer.parseInt(adultsField.getText()));
                booking.setNumberOfChildren(Integer.parseInt(childrenField.getText()));
                table.getModel().setValueAt(booking.getGuestFirstName(), rowIndex, 0);
                table.getModel().setValueAt(booking.getGuestLastName(), rowIndex, 1);
                table.getModel().setValueAt(booking.getRoomId(), rowIndex, 2);
                table.getModel().setValueAt(booking.getCheckInDate(), rowIndex, 3);
                table.getModel().setValueAt(booking.getCheckOutDate(), rowIndex, 4);
                table.getModel().setValueAt(booking.getStatusId(), rowIndex, 5);
                table.getModel().setValueAt(booking.getNumberOfAdults(), rowIndex, 6);
                table.getModel().setValueAt(booking.getNumberOfChildren(), rowIndex, 7);
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

    // Método auxiliar para formatar a data para "YYYY-MM-DD"
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}

