import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClientDetailsScreen extends JPanel {
    private ClientsScreen.Client client;
    private int currentUserId;
    private String currentUserRole;
    private JTabbedPane tabbedPane;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    
    public ClientDetailsScreen(ClientsScreen.Client client, int userId, String userRole) {
        this.client = client;
        this.currentUserId = userId;
        this.currentUserRole = userRole;
        initUI();
        loadClientDetails();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 242, 245));
        
        JLabel titleLabel = new JLabel("CLIENT DETAILS - " + client.getFirstName() + " " + client.getLastName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(240, 242, 245));
        
        JButton backButton = new JButton("← Back to Clients");
        styleButton(backButton, new Color(120, 120, 120), new Color(100, 100, 100));
        backButton.addActionListener(e -> goBackToClients());
        
        JButton downloadButton = new JButton("📄 Download Statement");
        styleButton(downloadButton, new Color(46, 125, 50), new Color(39, 105, 42));
        downloadButton.addActionListener(e -> downloadClientStatement());
        
        buttonPanel.add(backButton);
        buttonPanel.add(downloadButton);
        
        headerPanel.add(buttonPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content - Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Personal Info Tab
        JPanel personalInfoPanel = createPersonalInfoPanel();
        tabbedPane.addTab("Personal Information", personalInfoPanel);
        
        // Loans Tab
        JPanel loansPanel = createLoansPanel();
        tabbedPane.addTab("Loans", loansPanel);
        
        // Payment History Tab
        JPanel paymentsPanel = createPaymentsPanel();
        tabbedPane.addTab("Payment History", paymentsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        String[] labels = {
            "Client ID:", "First Name:", "Last Name:", "ID Number:", "Phone Number:",
            "Date of Birth:", "Age:", "Email:", "Physical Address:",
            "Province:", "Employer:", "Job Title:", "Monthly Income:",
            "Employment Status:", "Gender:", "Marital Status:", "Member Since:"
        };
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            JLabel value = new JLabel("Loading...");
            value.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            value.setForeground(new Color(50, 50, 50));
            panel.add(value, gbc);
        }
        
        return panel;
    }
    
    private JPanel createLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Loan Number", "Product", "Amount", "Interest Rate", "Term", 
                          "Disbursement Date", "Due Date", "Status", "Balance"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class; // All columns will be treated as strings
            }
        };
        JTable loansTable = new JTable(model);
        styleTable(loansTable);
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < loansTable.getColumnCount(); i++) {
            loansTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(loansTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Payment #", "Due Date", "Amount Due", "Principal", 
                          "Interest", "Penalty", "Paid Amount", "Paid Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class; // All columns will be treated as strings
            }
        };
        JTable paymentsTable = new JTable(model);
        styleTable(paymentsTable);
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < paymentsTable.getColumnCount(); i++) {
            paymentsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                
                if (isSelected) {
                    setBackground(new Color(220, 235, 247));
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }
                
                return this;
            }
        });
    }
    
    private void styleButton(JButton button, Color bgColor, Color hoverColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
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
    
    private void loadClientDetails() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadPersonalInfo();
                loadLoans();
                loadPaymentHistory();
                return null;
            }
        };
        worker.execute();
    }
    
    private void loadPersonalInfo() {
        String sql = "SELECT * FROM clients WHERE client_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                SwingUtilities.invokeLater(() -> {
                    JPanel personalPanel = (JPanel) tabbedPane.getComponentAt(0);
                    
                    // Clear existing components and rebuild with actual data
                    personalPanel.removeAll();
                    personalPanel.setLayout(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(10, 10, 10, 10);
                    gbc.anchor = GridBagConstraints.WEST;
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    
                    // Client basic info (from Client object)
                    addInfoRow(personalPanel, gbc, "Client ID:", String.valueOf(client.getClientId()), 0);
                    addInfoRow(personalPanel, gbc, "First Name:", client.getFirstName(), 1);
                    addInfoRow(personalPanel, gbc, "Last Name:", client.getLastName(), 2);
                    addInfoRow(personalPanel, gbc, "ID Number:", client.getIdNumber(), 3);
                    addInfoRow(personalPanel, gbc, "Phone Number:", client.getPhoneNumber(), 4);
                    
                    // Calculate age
                    Date dob = client.getDateOfBirth();
                    int age = (int) ((new java.util.Date().getTime() - dob.getTime()) / (1000L * 60 * 60 * 24 * 365));
                    addInfoRow(personalPanel, gbc, "Date of Birth:", dateFormat.format(dob), 5);
                    addInfoRow(personalPanel, gbc, "Age:", age + " years", 6);
                    
                    try{
// Additional info from database
                    addInfoRow(personalPanel, gbc, "Email:", rs.getString("email"), 7);
                    addInfoRow(personalPanel, gbc, "Physical Address:", rs.getString("physical_address"), 8);
                    addInfoRow(personalPanel, gbc, "Province:", rs.getString("province"), 9);
                    addInfoRow(personalPanel, gbc, "Employer:", rs.getString("employer_name"), 10);
                    addInfoRow(personalPanel, gbc, "Job Title:", rs.getString("job_title"), 11);
                    addInfoRow(personalPanel, gbc, "Monthly Income:", String.format("ZMW %,.2f", rs.getDouble("monthly_income")), 12);
                    addInfoRow(personalPanel, gbc, "Employment Status:", rs.getString("employment_status"), 13);
                    addInfoRow(personalPanel, gbc, "Gender:", rs.getString("gender"), 14);
                    addInfoRow(personalPanel, gbc, "Marital Status:", rs.getString("marital_status"), 15);
                    addInfoRow(personalPanel, gbc, "Member Since:", dateFormat.format(client.getCreatedAt()), 16);
                    
                    personalPanel.revalidate();
                    personalPanel.repaint();
                    }catch(SQLException e){}
                    
                });
            }
        } catch (SQLException e) {
            showError("Error loading personal information: " + e.getMessage());
        }
    }
    
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(labelComp, gbc);
        
        gbc.gridx = 1;
        JLabel valueComp = new JLabel(value != null ? value : "N/A");
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComp.setForeground(new Color(50, 50, 50));
        panel.add(valueComp, gbc);
    }
    
    private void loadLoans() {
        // Fixed SQL query to correctly calculate balance and get disbursement date
        String sql = "SELECT l.loan_id, l.loan_number, lp.product_name, l.amount, l.interest_rate, " +
                   "l.loan_term_months, l.disbursement_date, l.due_date, l.status, " +
                   "(l.amount - COALESCE(SUM(lp2.principal_amount), 0)) as balance " +
                   "FROM loans l " +
                   "JOIN loan_products lp ON l.product_id = lp.product_id " +
                   "LEFT JOIN loan_payments lp2 ON l.loan_id = lp2.loan_id AND lp2.status = 'Paid' " +
                   "WHERE l.client_id = ? " +
                   "GROUP BY l.loan_id " +
                   "ORDER BY l.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            DefaultTableModel model = (DefaultTableModel) ((JTable)((JScrollPane)((JPanel)tabbedPane.getComponentAt(1))
                .getComponent(0)).getViewport().getView()).getModel();
            model.setRowCount(0);
            
            while (rs.next()) {
                double balance = rs.getDouble("balance");
                String status = rs.getString("status");
                
                // For pending loans, balance should be the full amount
                if ("Pending".equals(status)) {
                    balance = rs.getDouble("amount");
                }
                
                // Format disbursement date - if null, check if loan is approved but disbursement not set
                String disbursementDateStr = "N/A";
                Date disbursementDate = rs.getDate("disbursement_date");
                if (disbursementDate != null) {
                    disbursementDateStr = dateFormat.format(disbursementDate);
                } else if ("Approved".equals(status) || "Active".equals(status)) {
                    // If loan is approved but disbursement date is not set, use current date
                    disbursementDateStr = dateFormat.format(new java.util.Date());
                }
                
                model.addRow(new Object[]{
                    rs.getString("loan_number"),
                    rs.getString("product_name"),
                    String.format("ZMW %,.2f", rs.getDouble("amount")),
                    String.format("%.2f%%", rs.getDouble("interest_rate")),
                    rs.getInt("loan_term_months") + " months",
                    disbursementDateStr,
                    rs.getDate("due_date") != null ? dateFormat.format(rs.getDate("due_date")) : "N/A",
                    status,
                    String.format("ZMW %,.2f", balance)
                });
            }
        } catch (SQLException e) {
            showError("Error loading loans: " + e.getMessage());
        }
    }
    
    private void loadPaymentHistory() {
        String sql = "SELECT lp.payment_number, lp.scheduled_payment_date, lp.payment_amount, " +
                   "lp.principal_amount, lp.interest_amount, lp.penalty_amount, " +
                   "lp.paid_amount, lp.paid_date, lp.status, l.loan_number " +
                   "FROM loan_payments lp " +
                   "JOIN loans l ON lp.loan_id = l.loan_id " +
                   "WHERE l.client_id = ? " +
                   "ORDER BY lp.scheduled_payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            DefaultTableModel model = (DefaultTableModel) ((JTable)((JScrollPane)((JPanel)tabbedPane.getComponentAt(2))
                .getComponent(0)).getViewport().getView()).getModel();
            model.setRowCount(0);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("payment_number"),
                    dateFormat.format(rs.getDate("scheduled_payment_date")),
                    String.format("ZMW %,.2f", rs.getDouble("payment_amount")),
                    String.format("ZMW %,.2f", rs.getDouble("principal_amount")),
                    String.format("ZMW %,.2f", rs.getDouble("interest_amount")),
                    String.format("ZMW %,.2f", rs.getDouble("penalty_amount")),
                    String.format("ZMW %,.2f", rs.getDouble("paid_amount")),
                    rs.getDate("paid_date") != null ? dateFormat.format(rs.getDate("paid_date")) : "Not Paid",
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            showError("Error loading payment history: " + e.getMessage());
        }
    }
    
    private void downloadClientStatement() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Client Statement");
        
        String defaultFileName = "Client_Statement_" + client.getClientId() + "_" + 
                               LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        generateStatement(writer);
                        logAudit("Downloaded statement for client ID: " + client.getClientId());
                    }
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(ClientDetailsScreen.this,
                            "Statement successfully saved to:\n" + file.getAbsolutePath(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        showError("Error generating statement: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void generateStatement(BufferedWriter writer) throws Exception {
        // Get complete client info
        String clientInfoSql = "SELECT * FROM clients WHERE client_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(clientInfoSql)) {
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Header
                writer.write("=".repeat(60));
                writer.newLine();
                writer.write("                 CLIENT STATEMENT");
                writer.newLine();
                writer.write("=".repeat(60));
                writer.newLine();
                writer.newLine();
                
                // Client Information
                writer.write("CLIENT INFORMATION:");
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
                
                writer.write("Client ID: " + client.getClientId());
                writer.newLine();
                writer.write("Name: " + client.getFirstName() + " " + client.getLastName());
                writer.newLine();
                writer.write("ID Number: " + client.getIdNumber());
                writer.newLine();
                writer.write("Phone: " + client.getPhoneNumber());
                writer.newLine();
                writer.write("Email: " + rs.getString("email"));
                writer.newLine();
                writer.write("Address: " + rs.getString("physical_address") + ", " + rs.getString("province"));
                writer.newLine();
                writer.write("Employer: " + rs.getString("employer_name"));
                writer.newLine();
                writer.write("Position: " + rs.getString("job_title"));
                writer.newLine();
                writer.write("Monthly Income: ZMW " + String.format("%,.2f", rs.getDouble("monthly_income")));
                writer.newLine();
                
                writer.newLine();
                
                // Loan Summary
                writer.write("LOAN SUMMARY:");
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
                
                // Fixed SQL query for loan summary
                String loanSql = "SELECT l.loan_id, l.loan_number, lp.product_name, l.amount, l.interest_rate, " +
                               "l.disbursement_date, l.due_date, l.status, " +
                               "(l.amount - COALESCE(SUM(lp2.principal_amount), 0)) as balance " +
                               "FROM loans l " +
                               "JOIN loan_products lp ON l.product_id = lp.product_id " +
                               "LEFT JOIN loan_payments lp2 ON l.loan_id = lp2.loan_id AND lp2.status = 'Paid' " +
                               "WHERE l.client_id = ? " +
                               "GROUP BY l.loan_id";
                
                try (Connection conn2 = DatabaseConnection.getConnection();
                     PreparedStatement loanStmt = conn2.prepareStatement(loanSql)) {
                    loanStmt.setInt(1, client.getClientId());
                    ResultSet loanRs = loanStmt.executeQuery();
                    
                    while (loanRs.next()) {
                        double balance = loanRs.getDouble("balance");
                        String status = loanRs.getString("status");
                        
                        // For pending loans, balance should be the full amount
                        if ("Pending".equals(status)) {
                            balance = loanRs.getDouble("amount");
                        }
                        
                        // Format disbursement date
                        String disbursementDateStr = "N/A";
                        Date disbursementDate = loanRs.getDate("disbursement_date");
                        if (disbursementDate != null) {
                            disbursementDateStr = dateFormat.format(disbursementDate);
                        } else if ("Approved".equals(status) || "Active".equals(status)) {
                            // If loan is approved but disbursement date is not set, use current date
                            disbursementDateStr = dateFormat.format(new java.util.Date());
                        }
                        
                        writer.write("Loan Number: " + loanRs.getString("loan_number"));
                        writer.newLine();
                        writer.write("Product: " + loanRs.getString("product_name"));
                        writer.newLine();
                        writer.write("Amount: ZMW " + String.format("%,.2f", loanRs.getDouble("amount")));
                        writer.newLine();
                        writer.write("Interest Rate: " + loanRs.getDouble("interest_rate") + "%");
                        writer.newLine();
                        writer.write("Disbursement Date: " + disbursementDateStr);
                        writer.newLine();
                        writer.write("Due Date: " + (loanRs.getDate("due_date") != null ? 
                            dateFormat.format(loanRs.getDate("due_date")) : "N/A"));
                        writer.newLine();
                        writer.write("Status: " + status);
                        writer.newLine();
                        writer.write("Outstanding Balance: ZMW " + String.format("%,.2f", balance));
                        writer.newLine();
                        writer.write("-".repeat(30));
                        writer.newLine();
                    }
                }
                
                writer.newLine();
                
                // Payment History
                writer.write("PAYMENT HISTORY:");
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
                
                String paymentSql = "SELECT lp.payment_number, lp.scheduled_payment_date, lp.payment_amount, " +
                                  "lp.principal_amount, lp.interest_amount, lp.penalty_amount, " +
                                  "lp.paid_amount, lp.paid_date, lp.status, l.loan_number " +
                                  "FROM loan_payments lp " +
                                  "JOIN loans l ON lp.loan_id = l.loan_id " +
                                  "WHERE l.client_id = ? " +
                                  "ORDER BY l.loan_number, lp.payment_number";
                
                try (Connection conn3 = DatabaseConnection.getConnection();
                     PreparedStatement paymentStmt = conn3.prepareStatement(paymentSql)) {
                    paymentStmt.setInt(1, client.getClientId());
                    ResultSet paymentRs = paymentStmt.executeQuery();
                    
                    while (paymentRs.next()) {
                        writer.write("Loan: " + paymentRs.getString("loan_number") + " | Payment #" + paymentRs.getInt("payment_number"));
                        writer.newLine();
                        writer.write("Due Date: " + dateFormat.format(paymentRs.getDate("scheduled_payment_date")));
                        writer.newLine();
                        writer.write("Amount Due: ZMW " + String.format("%,.2f", paymentRs.getDouble("payment_amount")));
                        writer.newLine();
                        writer.write("Principal: ZMW " + String.format("%,.2f", paymentRs.getDouble("principal_amount")));
                        writer.newLine();
                        writer.write("Interest: ZMW " + String.format("%,.2f", paymentRs.getDouble("interest_amount")));
                        writer.newLine();
                        writer.write("Penalty: ZMW " + String.format("%,.2f", paymentRs.getDouble("penalty_amount")));
                        writer.newLine();
                        writer.write("Paid: ZMW " + String.format("%,.2f", paymentRs.getDouble("paid_amount")));
                        writer.newLine();
                        writer.write("Paid Date: " + (paymentRs.getDate("paid_date") != null ? 
                            dateFormat.format(paymentRs.getDate("paid_date")) : "Not Paid"));
                        writer.newLine();
                        writer.write("Status: " + paymentRs.getString("status"));
                        writer.newLine();
                        writer.write("-".repeat(30));
                        writer.newLine();
                    }
                }
                
                // Footer
                writer.newLine();
                writer.write("=".repeat(60));
                writer.newLine();
                writer.write("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                writer.newLine();
                writer.write("Generated by: Employee ID " + currentUserId);
                writer.newLine();
                writer.write("=".repeat(60));
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
    
    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    private void goBackToClients() {
        ScreenManager.getInstance().showScreen(new ClientsScreen(currentUserId, currentUserRole));
    }
}