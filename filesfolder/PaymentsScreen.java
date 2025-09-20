import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentsScreen extends JPanel {
    private int userId;
    private String userRole;
    private JTable paymentsTable;
    private DefaultTableModel tableModel;
    private JButton approveButton, rejectButton, initiateButton, refreshButton, backButton;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public PaymentsScreen(int userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
        initUI();
        loadPaymentsData();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(new Color(240, 242, 245));
        
        // Back button
        backButton = new JButton("← Back to Dashboard");
        styleButton(backButton, new Color(100, 100, 100), new Color(80, 80, 80));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.addActionListener(e -> goBackToDashboard());
        headerPanel.add(backButton, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel("PAYMENT MANAGEMENT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        
        // Search and Action Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(new Color(240, 242, 245));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(240, 242, 245));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(searchLabel);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setToolTipText("Search by client name, ID number, phone, or loan number");
        searchPanel.add(searchField);
        
        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(statusLabel);
        
        statusFilterCombo = new JComboBox<>(new String[]{"All", "Pending", "Approved", "Rejected"});
        statusFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilterCombo.addActionListener(e -> filterPayments());
        searchPanel.add(statusFilterCombo);
        
        JButton searchButton = new JButton("Search");
        styleButton(searchButton, new Color(70, 130, 180), new Color(60, 120, 170));
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchButton.addActionListener(e -> filterPayments());
        searchPanel.add(searchButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        
        // Action Buttons
        JPanel actionTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionTopPanel.setBackground(new Color(240, 242, 245));
        
        refreshButton = new JButton("Refresh");
        styleButton(refreshButton, new Color(120, 120, 120), new Color(100, 100, 100));
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.addActionListener(e -> loadPaymentsData());
        actionTopPanel.add(refreshButton);
        
        initiateButton = new JButton("New Payment");
        styleButton(initiateButton, new Color(46, 125, 50), new Color(39, 105, 42));
        initiateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        initiateButton.addActionListener(e -> showNewPaymentDialog());
        actionTopPanel.add(initiateButton);
        
        topPanel.add(actionTopPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        String[] columns = {"Receipt ID", "Loan Number", "Client Name", "Client ID", "Amount", "Payment Date", 
                          "Payment Mode", "Status", "Approved By"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        paymentsTable = new JTable(tableModel);
        styleTable(paymentsTable);
        
        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(800, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Admin Action Panel
        if ("admin".equals(userRole)) {
            JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            adminPanel.setBackground(new Color(240, 242, 245));
            adminPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
            
            approveButton = new JButton("Approve Payment");
            styleButton(approveButton, new Color(46, 125, 50), new Color(39, 105, 42));
            approveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            approveButton.addActionListener(e -> approvePayment());
            approveButton.setEnabled(false);
            adminPanel.add(approveButton);
            
            rejectButton = new JButton("Reject Payment");
            styleButton(rejectButton, new Color(220, 53, 69), new Color(200, 35, 51));
            rejectButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            rejectButton.addActionListener(e -> rejectPayment());
            rejectButton.setEnabled(false);
            adminPanel.add(rejectButton);
            
            mainPanel.add(adminPanel, BorderLayout.SOUTH);
        }
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Add selection listener
        paymentsTable.getSelectionModel().addListSelectionListener(e -> updateButtonStates());
    }
    
    private void goBackToDashboard() {
        if ("admin".equals(userRole)) {
            ScreenManager.getInstance().showScreen(new AdminDashboard(userId, "Admin User"));
        } else {
            ScreenManager.getInstance().showScreen(new EmployeeDashboard(userId, "Employee User"));
        }
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Custom renderer for status column
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                String status = value.toString();
                switch (status) {
                    case "Approved":
                        setForeground(new Color(40, 167, 69));
                        setBackground(new Color(240, 255, 240));
                        break;
                    case "Rejected":
                        setForeground(new Color(220, 53, 69));
                        setBackground(new Color(255, 240, 240));
                        break;
                    case "Pending":
                        setForeground(new Color(255, 193, 7));
                        setBackground(new Color(255, 250, 240));
                        break;
                    default:
                        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                }
                
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }
    
    private void styleButton(JButton button, Color bgColor, Color hoverColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
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
    
    private void loadPaymentsData() {
        tableModel.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT pr.receipt_id, pr.loan_number, " +
                       "CONCAT(c.first_name, ' ', c.last_name) as client_name, " +
                       "c.id_number, pr.amount, pr.payment_date, pr.mode_of_payment, pr.status, " +
                       "COALESCE(e.name, 'N/A') as approved_by " +
                       "FROM payment_receipts pr " +
                       "JOIN clients c ON pr.client_id = c.client_id " +
                       "LEFT JOIN employees e ON pr.approved_by = e.employee_id " +
                       "ORDER BY pr.payment_date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("receipt_id"),
                    rs.getString("loan_number"),
                    rs.getString("client_name"),
                    rs.getString("id_number"),
                    String.format("ZMW %,.2f", rs.getDouble("amount")),
                    dateFormat.format(rs.getDate("payment_date")),
                    rs.getString("mode_of_payment"),
                    rs.getString("status"),
                    rs.getString("approved_by")
                });
            }
        } catch (SQLException ex) {
            showError("Error loading payments: " + ex.getMessage());
        }
        
        updateButtonStates();
    }
    
    private void filterPayments() {
        String searchTerm = searchField.getText().trim();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder sql = new StringBuilder(
                "SELECT pr.receipt_id, pr.loan_number, " +
                "CONCAT(c.first_name, ' ', c.last_name) as client_name, " +
                "c.id_number, pr.amount, pr.payment_date, pr.mode_of_payment, pr.status, " +
                "COALESCE(e.name, 'N/A') as approved_by " +
                "FROM payment_receipts pr " +
                "JOIN clients c ON pr.client_id = c.client_id " +
                "LEFT JOIN employees e ON pr.approved_by = e.employee_id " +
                "WHERE 1=1"
            );
            
            if (!searchTerm.isEmpty()) {
                sql.append(" AND (pr.loan_number LIKE ? OR c.first_name LIKE ? OR c.last_name LIKE ? ");
                sql.append("OR c.phone_number LIKE ? OR c.id_number LIKE ?)");
            }
            
            if (!"All".equals(statusFilter)) {
                sql.append(" AND pr.status = ?");
            }
            
            sql.append(" ORDER BY pr.payment_date DESC");
            
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
            if (!searchTerm.isEmpty()) {
                String likeTerm = "%" + searchTerm + "%";
                for (int i = 0; i < 5; i++) {
                    stmt.setString(paramIndex++, likeTerm);
                }
            }
            
            if (!"All".equals(statusFilter)) {
                stmt.setString(paramIndex, statusFilter);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("receipt_id"),
                    rs.getString("loan_number"),
                    rs.getString("client_name"),
                    rs.getString("id_number"),
                    String.format("ZMW %,.2f", rs.getDouble("amount")),
                    dateFormat.format(rs.getDate("payment_date")),
                    rs.getString("mode_of_payment"),
                    rs.getString("status"),
                    rs.getString("approved_by")
                });
            }
        } catch (SQLException ex) {
            showError("Error filtering payments: " + ex.getMessage());
        }
        
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        int selectedRow = paymentsTable.getSelectedRow();
        boolean hasSelection = selectedRow != -1;
        
        if ("admin".equals(userRole) && hasSelection) {
            String status = tableModel.getValueAt(selectedRow, 7).toString();
            boolean isPending = "Pending".equals(status);
            approveButton.setEnabled(isPending);
            rejectButton.setEnabled(isPending);
        }
    }
    
    private void showNewPaymentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Initialize New Payment", true);
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("New Payment - Search Client", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Main content with search and results
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setBackground(Color.WHITE);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        
        JLabel searchLabel = new JLabel("Search Client:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(searchLabel);
        
        JTextField clientSearchField = new JTextField(25);
        clientSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clientSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        clientSearchField.setToolTipText("Search by client name, ID number, or phone number");
        searchPanel.add(clientSearchField);
        
        JButton searchButton = new JButton("Search");
        styleSmallButton(searchButton);
        searchPanel.add(searchButton);
        
        mainContent.add(searchPanel, BorderLayout.NORTH);
        
        // Results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Client Search Results"
        ));
        resultsPanel.setPreferredSize(new Dimension(800, 150));
        
        String[] columns = {"Select", "Client Name", "ID Number", "Phone", "Active Loans"};
        DefaultTableModel resultsModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
        };
        
        JTable resultsTable = new JTable(resultsModel);
        resultsTable.setRowHeight(35);
        resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add checkbox renderer and editor for the first column
        resultsTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            JCheckBox checkBox = new JCheckBox();
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Boolean) {
                    checkBox.setSelected((Boolean) value);
                }
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                return checkBox;
            }
        });
        
        resultsTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
                JCheckBox checkBox = (JCheckBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                return checkBox;
            }
        });
        
        JScrollPane resultsScroll = new JScrollPane(resultsTable);
        resultsPanel.add(resultsScroll, BorderLayout.CENTER);
        
        mainContent.add(resultsPanel, BorderLayout.CENTER);
        
        // Payment form panel (initially hidden)
        JPanel paymentFormPanel = new JPanel(new GridBagLayout());
        paymentFormPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Payment Details"
        ));
        paymentFormPanel.setVisible(false);
        
        mainContent.add(paymentFormPanel, BorderLayout.SOUTH);
        
        contentPanel.add(mainContent, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton submitButton = new JButton("Submit Payment");
        styleButton(submitButton, new Color(46, 125, 50), new Color(39, 105, 42));
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setEnabled(false);
        buttonPanel.add(submitButton);
        
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(120, 120, 120), new Color(100, 100, 100));
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        
        // Store references to form components
        JTextField[] amountField = new JTextField[1];
        JTextField[] dateField = new JTextField[1];
        JComboBox<String>[] methodCombo = new JComboBox[1];
        JTextField[] voucherField = new JTextField[1];
        String[] loanNumber = new String[1];
        int[] clientId = new int[1];
        int[] loanId = new int[1];
        
        // Search button action
        searchButton.addActionListener(e -> {
            String searchTerm = clientSearchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                searchClients(searchTerm, resultsModel);
            } else {
                showError("Please enter a search term");
            }
        });
        
        // Results table selection listener
        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = resultsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String clientIdNumber = resultsModel.getValueAt(selectedRow, 2).toString();
                    loadClientLoanDetails(clientIdNumber, paymentFormPanel, submitButton, 
                                        amountField, dateField, methodCombo, voucherField, 
                                        loanNumber, clientId, loanId, dialog);
                }
            }
        });
        
        // Submit button action
        submitButton.addActionListener(e -> {
            if (validatePayment(amountField[0].getText(), voucherField[0].getText())) {
                submitPayment(
                    loanNumber[0],
                    amountField[0].getText(),
                    (String) methodCombo[0].getSelectedItem(),
                    dateField[0].getText(),
                    voucherField[0].getText(),
                    clientId[0],
                    loanId[0]
                );
                dialog.dispose();
                loadPaymentsData();
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void styleSmallButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    private void searchClients(String searchTerm, DefaultTableModel resultsModel) {
        resultsModel.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT c.client_id, c.first_name, c.last_name, c.id_number, c.phone_number, " +
                       "(SELECT COUNT(*) FROM loans l WHERE l.client_id = c.client_id AND l.status IN ('Active', 'Approved')) as active_loans " +
                       "FROM clients c " +
                       "WHERE c.first_name LIKE ? OR c.last_name LIKE ? OR c.id_number LIKE ? OR c.phone_number LIKE ? " +
                       "ORDER BY c.first_name, c.last_name";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            stmt.setString(4, likeTerm);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                resultsModel.addRow(new Object[]{
                    false, // Select checkbox
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("id_number"),
                    rs.getString("phone_number"),
                    rs.getInt("active_loans") + " active loans"
                });
            }
            
        } catch (SQLException ex) {
            showError("Error searching clients: " + ex.getMessage());
        }
    }
    
    private void loadClientLoanDetails(String idNumber, JPanel paymentFormPanel, JButton submitButton,
                                     JTextField[] amountField, JTextField[] dateField, 
                                     JComboBox<String>[] methodCombo, JTextField[] voucherField,
                                     String[] loanNumber, int[] clientId, int[] loanId, JDialog dialog) {
        paymentFormPanel.removeAll();
        paymentFormPanel.setVisible(true);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get client details
            String clientSql = "SELECT * FROM clients WHERE id_number = ?";
            PreparedStatement clientStmt = conn.prepareStatement(clientSql);
            clientStmt.setString(1, idNumber);
            ResultSet clientRs = clientStmt.executeQuery();
            
            if (clientRs.next()) {
                clientId[0] = clientRs.getInt("client_id");
                
                // Display client information
                gbc.gridx = 0; gbc.gridy = 0;
                paymentFormPanel.add(new JLabel("Client Name:"), gbc);
                
                gbc.gridx = 1;
                JTextField nameField = new JTextField(clientRs.getString("first_name") + " " + clientRs.getString("last_name"));
                nameField.setEditable(false);
                nameField.setPreferredSize(new Dimension(200, 30));
                paymentFormPanel.add(nameField, gbc);
                
                gbc.gridx = 0; gbc.gridy = 1;
                paymentFormPanel.add(new JLabel("ID Number:"), gbc);
                
                gbc.gridx = 1;
                JTextField idField = new JTextField(clientRs.getString("id_number"));
                idField.setEditable(false);
                paymentFormPanel.add(idField, gbc);
                
                // Get active loans
                String loanSql = "SELECT l.loan_id, l.loan_number, l.amount, " +
                               "(l.amount - COALESCE(SUM(lp.paid_amount), 0)) as outstanding_balance " +
                               "FROM loans l " +
                               "LEFT JOIN loan_payments lp ON l.loan_id = lp.loan_id " +
                               "WHERE l.client_id = ? AND l.status IN ('Active', 'Approved') " +
                               "GROUP BY l.loan_id LIMIT 1";
                
                PreparedStatement loanStmt = conn.prepareStatement(loanSql);
                loanStmt.setInt(1, clientId[0]);
                ResultSet loanRs = loanStmt.executeQuery();
                
                if (loanRs.next()) {
                    loanId[0] = loanRs.getInt("loan_id");
                    loanNumber[0] = loanRs.getString("loan_number");
                    
                    gbc.gridx = 0; gbc.gridy = 2;
                    paymentFormPanel.add(new JLabel("Loan Number:"), gbc);
                    
                    gbc.gridx = 1;
                    JTextField loanNumberField = new JTextField(loanNumber[0]);
                    loanNumberField.setEditable(false);
                    paymentFormPanel.add(loanNumberField, gbc);
                    
                    gbc.gridx = 0; gbc.gridy = 3;
                    paymentFormPanel.add(new JLabel("Outstanding Balance:"), gbc);
                    
                    gbc.gridx = 1;
                    JTextField balanceField = new JTextField(String.format("ZMW %,.2f", loanRs.getDouble("outstanding_balance")));
                    balanceField.setEditable(false);
                    paymentFormPanel.add(balanceField, gbc);
                    
                    // Payment amount
                    gbc.gridx = 0; gbc.gridy = 4;
                    paymentFormPanel.add(new JLabel("Payment Amount *:"), gbc);
                    
                    gbc.gridx = 1;
                    amountField[0] = new JTextField();
                    amountField[0].setPreferredSize(new Dimension(200, 30));
                    paymentFormPanel.add(amountField[0], gbc);
                    
                    // Payment date
                    gbc.gridx = 0; gbc.gridy = 5;
                    paymentFormPanel.add(new JLabel("Payment Date *:"), gbc);
                    
                    gbc.gridx = 1;
                    dateField[0] = new JTextField(dateFormat.format(new Date()));
                    paymentFormPanel.add(dateField[0], gbc);
                    
                    // Payment method
                    gbc.gridx = 0; gbc.gridy = 6;
                    paymentFormPanel.add(new JLabel("Payment Method *:"), gbc);
                    
                    gbc.gridx = 1;
                    methodCombo[0] = new JComboBox<>(new String[]{"Cash", "Mobile", "Bank", "Other"});
                    paymentFormPanel.add(methodCombo[0], gbc);
                    
                    // Voucher number
                    gbc.gridx = 0; gbc.gridy = 7;
                    paymentFormPanel.add(new JLabel("Voucher Number *:"), gbc);
                    
                    gbc.gridx = 1;
                    voucherField[0] = new JTextField("VOU" + (System.currentTimeMillis() % 100000));
                    paymentFormPanel.add(voucherField[0], gbc);
                    
                    submitButton.setEnabled(true);
                } else {
                    gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
                    paymentFormPanel.add(new JLabel("No active loans found for this client"), gbc);
                    gbc.gridwidth = 1;
                    submitButton.setEnabled(false);
                }
                
                dialog.pack();
            }
            
        } catch (SQLException ex) {
            showError("Error loading client details: " + ex.getMessage());
        }
    }
    
    private boolean validatePayment(String amount, String voucher) {
        try {
            double paymentAmount = Double.parseDouble(amount);
            if (paymentAmount <= 0) {
                showError("Payment amount must be greater than zero");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid payment amount");
            return false;
        }
        
        if (voucher.isEmpty()) {
            showError("Please enter a voucher number");
            return false;
        }
        
        return true;
    }
    
    private void submitPayment(String loanNumber, String amount, String mode, String date, String voucher, int clientId, int loanId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get the next payment_id for this loan
            String paymentIdSql = "SELECT lp.payment_id FROM loan_payments lp " +
                                "WHERE lp.loan_id = ? AND lp.status IN ('Pending', 'Overdue') " +
                                "ORDER BY lp.scheduled_payment_date ASC LIMIT 1";
            
            int paymentId = 0;
            try (PreparedStatement stmt = conn.prepareStatement(paymentIdSql)) {
                stmt.setInt(1, loanId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    paymentId = rs.getInt("payment_id");
                }
            }
            
            if (paymentId == 0) {
                showError("No pending payments found for this loan");
                return;
            }
            
            // Insert payment receipt
            String sql = "INSERT INTO payment_receipts (payment_id, loan_number, client_id, employee_id, " +
                       "voucher_number, mode_of_payment, amount, payment_date, status) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pending')";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, paymentId);
                stmt.setString(2, loanNumber);
                stmt.setInt(3, clientId);
                stmt.setInt(4, userId);
                stmt.setString(5, voucher);
                stmt.setString(6, mode);
                stmt.setDouble(7, Double.parseDouble(amount));
                stmt.setString(8, date);
                
                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    // Update the loan payment status
                    String updateSql = "UPDATE loan_payments SET paid_amount = ?, paid_date = ?, status = 'Paid' " +
                                    "WHERE payment_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, Double.parseDouble(amount));
                        updateStmt.setString(2, date);
                        updateStmt.setInt(3, paymentId);
                        updateStmt.executeUpdate();
                    }
                    
                    JOptionPane.showMessageDialog(this, 
                        "Payment submitted successfully!\nWaiting for admin approval.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            showError("Error submitting payment: " + ex.getMessage());
        }
    }
    
    private void approvePayment() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int receiptId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = tableModel.getValueAt(selectedRow, 7).toString();
        
        if (!"Pending".equals(status)) {
            showError("Only pending payments can be approved");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to approve this payment?", 
            "Confirm Approval", JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE payment_receipts SET status = 'Approved', approved_by = ?, approval_date = NOW() " +
                       "WHERE receipt_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, receiptId);
            
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Payment approved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPaymentsData();
            } else {
                showError("Failed to approve payment");
            }
        } catch (SQLException ex) {
            showError("Error approving payment: " + ex.getMessage());
        }
    }
    
    private void rejectPayment() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int receiptId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = tableModel.getValueAt(selectedRow, 7).toString();
        
        if (!"Pending".equals(status)) {
            showError("Only pending payments can be rejected");
            return;
        }
        
        String reason = JOptionPane.showInputDialog(this, "Enter rejection reason:");
        if (reason == null || reason.trim().isEmpty()) {
            showError("Rejection reason is required");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
             // First get the payment_id from the receipt
            String getPaymentIdSql = "SELECT payment_id FROM payment_receipts WHERE receipt_id = ?";
            int paymentId = 0;
            
            try (PreparedStatement stmt = conn.prepareStatement(getPaymentIdSql)) {
                stmt.setInt(1, receiptId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    paymentId = rs.getInt("payment_id");
                }
            }
            
            // Update payment receipt status
            String sql = "UPDATE payment_receipts SET status = 'Rejected', approved_by = ?, approval_date = NOW() " +
                       "WHERE receipt_id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, receiptId);
                stmt.executeUpdate();
            }
            
            // Reset the loan payment status
            if (paymentId > 0) {
                String updateSql = "UPDATE loan_payments SET paid_amount = 0, paid_date = NULL, " +
                                "status = CASE WHEN scheduled_payment_date < CURDATE() THEN 'Overdue' ELSE 'Pending' END " +
                                "WHERE payment_id = ?";
                
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, paymentId);
                    updateStmt.executeUpdate();
                }
            }
            
            JOptionPane.showMessageDialog(this, "Payment rejected successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadPaymentsData();
            
        } catch (SQLException ex) {
            showError("Error rejecting payment: " + ex.getMessage());
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}