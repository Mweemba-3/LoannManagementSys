import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ClientsScreen extends JPanel {
    private JTable clientsTable;
    private ClientTableModel tableModel;
    private JTextField searchField;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    private int currentUserId;
    private String currentUserRole;
    
    public ClientsScreen(int userId, String userRole) {
        this.currentUserId = userId;
        this.currentUserRole = userRole;
        initUI();
        loadClientsData();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("CLIENT MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(new Color(245, 245, 245));
        
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.putClientProperty("JTextField.placeholderText", "Search clients...");
        searchField.addActionListener(e -> searchClients());
        
        JButton searchButton = new JButton("Search");
        styleButton(searchButton, new Color(0, 120, 215), new Color(0, 100, 190));
        searchButton.setPreferredSize(new Dimension(100, 40));
        searchButton.addActionListener(e -> searchClients());
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        String[] buttonIcons = {"➕", "✏️", "🗑️", "👁️", "🏠"};
        String[] buttonTooltips = {
            "Add New Client", "Edit Selected Client", 
            "Delete Client", "View Client Details", "Return to Dashboard"
        };
        Color[] buttonColors = {
            new Color(46, 125, 50), // Green
            new Color(251, 140, 0),  // Orange
            new Color(198, 40, 40),  // Red
            new Color(21, 101, 192), // Blue
            new Color(120, 120, 120) // Gray
        };
        
        for (int i = 0; i < buttonIcons.length; i++) {
            JButton button = new JButton(buttonIcons[i]);
            button.setToolTipText(buttonTooltips[i]);
            styleButton(button, buttonColors[i], buttonColors[i].darker());
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            button.setPreferredSize(new Dimension(45, 40));
            buttonPanel.add(button);
            
            switch (i) {
                case 0: button.addActionListener(e -> showAddClientScreen()); break;
                case 1: button.addActionListener(e -> showEditClientScreen()); break;
                case 2: button.addActionListener(e -> deleteSelectedClient()); break;
                case 3: button.addActionListener(e -> showClientDetails()); break;
                case 4: button.addActionListener(e -> goBackHome()); break;
            }
        }
        
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table Panel
        tableModel = new ClientTableModel();
        clientsTable = new JTable(tableModel);
        customizeTable(clientsTable);
        
        JScrollPane scrollPane = new JScrollPane(clientsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void styleButton(JButton button, Color bgColor, Color hoverColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private void customizeTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Custom header renderer
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        
        // Custom cell renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                
                if (isSelected) {
                    setBackground(new Color(220, 235, 247));
                    setForeground(Color.BLACK);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                    setForeground(Color.BLACK);
                }
                
                // Right-align numeric columns
                if (value instanceof Number) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                // Format dates
                if (value instanceof java.util.Date) {
                    setText(dateFormat.format(value));
                }
                
                // Highlight overdue clients in red
                if (column == 8) { // Due Date column
                    try {
                        Date dueDate = (Date) table.getModel().getValueAt(row, 8);
                        if (dueDate != null && dueDate.before(new java.util.Date())) {
                            setForeground(Color.RED);
                            setFont(getFont().deriveFont(Font.BOLD));
                        }
                    } catch (Exception e) {
                        // Handle potential cast exceptions quietly
                    }
                }
                
                return this;
            }
        });
    }
    
    private void loadClientsData() {
        SwingWorker<List<Client>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Client> doInBackground() throws Exception {
                List<Client> clients = new ArrayList<>();
                String sql = "SELECT c.client_id, c.first_name, c.last_name, c.phone_number, " +
                           "c.id_number, c.date_of_birth, c.created_at, " +
                           "(SELECT MAX(due_date) FROM loan_payments lp " +
                           " JOIN loans l ON lp.loan_id = l.loan_id " +
                           " WHERE l.client_id = c.client_id) as due_date, " +
                           "COUNT(DISTINCT l.loan_id) as loan_count, " +
                           "COALESCE(SUM(l.amount), 0) as total_loans " +
                           "FROM clients c " +
                           "LEFT JOIN loans l ON c.client_id = l.client_id " +
                           "GROUP BY c.client_id " +
                           "ORDER BY c.first_name, c.last_name";
                
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    
                    while (rs.next()) {
                        clients.add(new Client(
                            rs.getInt("client_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("id_number"),
                            rs.getString("phone_number"),
                            rs.getDate("date_of_birth"),
                            rs.getTimestamp("created_at"),
                            rs.getDate("due_date"),
                            rs.getInt("loan_count"),
                            rs.getDouble("total_loans")
                        ));
                    }
                }
                return clients;
            }
            
            @Override
            protected void done() {
                try {
                    tableModel.setClients(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClientsScreen.this, 
                        "Error loading clients: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void searchClients() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadClientsData();
            return;
        }
        
        SwingWorker<List<Client>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Client> doInBackground() throws Exception {
                List<Client> clients = new ArrayList<>();
                String sql = "SELECT c.client_id, c.first_name, c.last_name, c.phone_number, " +
                           "c.id_number, c.date_of_birth, c.created_at, " +
                           "(SELECT MAX(due_date) FROM loan_payments lp " +
                           " JOIN loans l ON lp.loan_id = l.loan_id " +
                           " WHERE l.client_id = c.client_id) as due_date, " +
                           "COUNT(DISTINCT l.loan_id) as loan_count, " +
                           "COALESCE(SUM(l.amount), 0) as total_loans " +
                           "FROM clients c " +
                           "LEFT JOIN loans l ON c.client_id = l.client_id " +
                           "WHERE c.first_name LIKE ? OR c.last_name LIKE ? OR " +
                           "c.phone_number LIKE ? OR c.id_number LIKE ? " +
                           "GROUP BY c.client_id " +
                           "ORDER by c.first_name, c.last_name";
                
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    
                    stmt.setString(1, "%" + searchTerm + "%");
                    stmt.setString(2, "%" + searchTerm + "%");
                    stmt.setString(3, "%" + searchTerm + "%");
                    stmt.setString(4, "%" + searchTerm + "%");
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            clients.add(new Client(
                                rs.getInt("client_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("id_number"),
                                rs.getString("phone_number"),
                                rs.getDate("date_of_birth"),
                                rs.getTimestamp("created_at"),
                                rs.getDate("due_date"),
                                rs.getInt("loan_count"),
                                rs.getDouble("total_loans")
                            ));
                        }
                    }
                }
                return clients;
            }
            
            @Override
            protected void done() {
                try {
                    tableModel.setClients(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ClientsScreen.this, 
                        "Error searching clients: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void deleteSelectedClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a client to delete", "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Convert view index to model index (important if table is sorted)
        int modelRow = clientsTable.convertRowIndexToModel(selectedRow);
        Client client = tableModel.getClientAt(modelRow);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this client?", "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM clients WHERE client_id = ?")) {
                stmt.setInt(1, client.getClientId());
                stmt.executeUpdate();
                logAudit("Deleted client ID: " + client.getClientId());
                loadClientsData();
                JOptionPane.showMessageDialog(this, "Client deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting client: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void logAudit(String action) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO audit_logs (employee_id, action) VALUES (?, ?)")) {
            stmt.setInt(1, currentUserId);
            stmt.setString(2, action);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Audit Log Error: " + e.getMessage());
        }
    }
    
    private void showAddClientScreen() {
        ScreenManager.getInstance().showScreen(new AddClientScreen(currentUserId, currentUserRole));
    }
    
    private void showEditClientScreen() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a client to edit", "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = clientsTable.convertRowIndexToModel(selectedRow);
        Client client = tableModel.getClientAt(modelRow);

        ScreenManager.getInstance().showScreen(new EditClientScreen(client.getClientId(), currentUserId, currentUserRole));
    }
    
    private void showClientDetails() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a client to view", "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Convert view index to model index (important if table is sorted)
        int modelRow = clientsTable.convertRowIndexToModel(selectedRow);
        Client client = tableModel.getClientAt(modelRow);
        
        ScreenManager.getInstance().showScreen(new ClientDetailsScreen(client, currentUserId, currentUserRole));
    }
    
    private void goBackHome() {
        if (currentUserRole.equalsIgnoreCase("admin")) {
            ScreenManager.getInstance().showScreen(new AdminDashboard(currentUserId, "Admin"));
        } else {
            ScreenManager.getInstance().showScreen(new EmployeeDashboard(currentUserId, "Employee"));
        }
    }
    
    // Enhanced Client Table Model
    private class ClientTableModel extends AbstractTableModel {
        private List<Client> clients = new ArrayList<>();
        private String[] columnNames = {
            "ID", "First Name", "Last Name", "ID Number", "Phone", 
            "Age", "Member Since", "Loans", "Due Date", "Total Borrowed"
        };
        
        public void setClients(List<Client> clients) {
            this.clients = clients;
            fireTableDataChanged();
        }
        
        public Client getClientAt(int row) {
            return clients.get(row);
        }
        
        @Override public int getRowCount() { return clients.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int col) { return columnNames[col]; }
        
        @Override
        public Object getValueAt(int row, int col) {
            Client client = clients.get(row);
            switch (col) {
                case 0: return client.getClientId();
                case 1: return client.getFirstName();
                case 2: return client.getLastName();
                case 3: return client.getIdNumber();
                case 4: return client.getPhoneNumber();
                case 5: return calculateAge(client.getDateOfBirth());
                case 6: return client.getCreatedAt();
                case 7: return client.getLoanCount();
                case 8: return client.getDueDate();
                case 9: return String.format("ZMW %,.2f", client.getTotalLoans());
                default: return null;
            }
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: return Integer.class;
                case 5: return Integer.class;
                case 7: return Integer.class;
                case 8: return Date.class;
                case 9: return String.class;
                default: return String.class;
            }
        }
        
        private int calculateAge(Date dob) {
            if (dob == null) return 0;
            java.util.Date now = new java.util.Date();
            long ageInMillis = now.getTime() - dob.getTime();
            return (int) (ageInMillis / (1000L * 60 * 60 * 24 * 365));
        }
    }
    
    public static class Client {
        private int clientId;
        private String firstName;
        private String lastName;
        private String idNumber;
        private String phoneNumber;
        private Date dateOfBirth;
        private Timestamp createdAt;
        private Date dueDate;
        private int loanCount;
        private double totalLoans;
        
        public Client(int clientId, String firstName, String lastName, String idNumber, 
                     String phoneNumber, Date dateOfBirth, Timestamp createdAt, 
                     Date dueDate, int loanCount, double totalLoans) {
            this.clientId = clientId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.idNumber = idNumber;
            this.phoneNumber = phoneNumber;
            this.dateOfBirth = dateOfBirth;
            this.createdAt = createdAt;
            this.dueDate = dueDate;
            this.loanCount = loanCount;
            this.totalLoans = totalLoans;
        }
        
        // Getters
        public int getClientId() { return clientId; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getIdNumber() { return idNumber; }
        public String getPhoneNumber() { return phoneNumber; }
        public Date getDateOfBirth() { return dateOfBirth; }
        public Timestamp getCreatedAt() { return createdAt; }
        public Date getDueDate() { return dueDate; }
        public int getLoanCount() { return loanCount; }
        public double getTotalLoans() { return totalLoans; }
    }
}