import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class LoansScreen extends JPanel {
    private int employeeId;
    private String userRole;
    private JTable loansTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    private JTextField searchField;
    
    public LoansScreen(int employeeId, String userRole) {
        this.employeeId = employeeId;
        this.userRole = userRole;
        initUI();
        loadLoansData("All Loans");
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Search and Filter Panel
        JPanel searchFilterPanel = createSearchFilterPanel();
        add(searchFilterPanel, BorderLayout.NORTH);
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Buttons Panel
        JPanel buttonsPanel = createButtonsPanel();
        add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel("LOANS MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Dashboard button
        JButton dashboardBtn = createStyledButton("🏠 Dashboard", new Color(57, 62, 70));
        dashboardBtn.addActionListener(e -> navigateToDashboard());
        headerPanel.add(dashboardBtn, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSearchFilterPanel() {
        JPanel searchFilterPanel = new JPanel(new BorderLayout(10, 0));
        searchFilterPanel.setBackground(new Color(245, 245, 245));
        searchFilterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(new Color(245, 245, 245));
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        JButton searchBtn = createStyledButton("🔍 Search", new Color(0, 173, 181));
        searchBtn.addActionListener(e -> searchLoans());
        
        JButton backBtn = createStyledButton("⬅️ Back to Dashboard", new Color(108, 117, 125));
        backBtn.addActionListener(e -> navigateToDashboard());
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(backBtn);
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(new Color(245, 245, 245));
        
        String[] filters = {"All Loans", "Pending", "Approved", "Active", "Rejected", "Closed", "Due Loans", "Overdue"};
        filterComboBox = new JComboBox<>(filters);
        filterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterComboBox.setBackground(Color.WHITE);
        filterComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        filterComboBox.addActionListener(e -> filterLoans());
        
        filterPanel.add(new JLabel("Filter by Status:"));
        filterPanel.add(filterComboBox);
        
        // Add both panels to main panel
        searchFilterPanel.add(searchPanel, BorderLayout.NORTH);
        searchFilterPanel.add(filterPanel, BorderLayout.SOUTH);
        
        return searchFilterPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        tablePanel.setBackground(new Color(245, 245, 245));
        
        String[] columns = {"Loan ID", "Loan Number", "Client Name", "Amount", "Status", "Application Date", "Due Date", "Issued By"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                if (columnIndex == 3) return Double.class;
                return String.class;
            }
        };
        
        loansTable = new JTable(tableModel);
        loansTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loansTable.setRowHeight(30);
        loansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loansTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        loansTable.setBackground(Color.WHITE);
        loansTable.setGridColor(new Color(220, 220, 220));
        loansTable.setShowGrid(true);
        loansTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Set column widths and center all columns
        TableColumnModel columnModel = loansTable.getColumnModel();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        columnModel.getColumn(0).setPreferredWidth(60);
        columnModel.getColumn(0).setCellRenderer(centerRenderer);
        
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(1).setCellRenderer(centerRenderer);
        
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(2).setCellRenderer(centerRenderer);
        
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    setText(String.format("ZMW %,.2f", (Double) value));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });
        
        columnModel.getColumn(4).setPreferredWidth(80);
        columnModel.getColumn(4).setCellRenderer(centerRenderer);
        
        columnModel.getColumn(5).setPreferredWidth(100);
        columnModel.getColumn(5).setCellRenderer(centerRenderer);
        
        columnModel.getColumn(6).setPreferredWidth(100);
        columnModel.getColumn(6).setCellRenderer(centerRenderer);
        
        columnModel.getColumn(7).setPreferredWidth(120);
        columnModel.getColumn(7).setCellRenderer(centerRenderer);
        
        // Add row selection listener
        loansTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
        
        // Add custom renderer for status column to color code statuses
        loansTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "Pending":
                            c.setBackground(new Color(255, 243, 205)); // Light yellow
                            c.setForeground(new Color(133, 100, 4));   // Dark yellow
                            break;
                        case "Approved":
                            c.setBackground(new Color(212, 237, 218)); // Light green
                            c.setForeground(new Color(21, 87, 36));    // Dark green
                            break;
                        case "Active":
                            c.setBackground(new Color(209, 231, 255)); // Light blue
                            c.setForeground(new Color(12, 67, 125));   // Dark blue
                            break;
                        case "Rejected":
                            c.setBackground(new Color(248, 215, 218)); // Light red
                            c.setForeground(new Color(114, 28, 36));   // Dark red
                            break;
                        case "Closed":
                            c.setBackground(new Color(226, 227, 229)); // Light gray
                            c.setForeground(new Color(73, 80, 87));    // Dark gray
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                    }
                }
                
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(loansTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(new Color(245, 245, 245));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton applyLoanBtn = createStyledButton("➕ Apply Loan", new Color(97, 218, 121));
        applyLoanBtn.addActionListener(e -> applyLoan());
        
        JButton viewBtn = createStyledButton("👁️ View Details", new Color(0, 173, 181));
        viewBtn.addActionListener(e -> viewLoan());
        
        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(108, 117, 125));
        refreshBtn.addActionListener(e -> filterLoans());
        
        buttonsPanel.add(applyLoanBtn);
        buttonsPanel.add(viewBtn);
        buttonsPanel.add(refreshBtn);
        
        if ("admin".equals(userRole)) {
            JButton createProductBtn = createStyledButton("📝 Create Product", new Color(255, 159, 67));
            createProductBtn.addActionListener(e -> createLoanProduct());
            
            JButton approveBtn = createStyledButton("✅ Approve", new Color(97, 218, 121));
            approveBtn.addActionListener(e -> approveLoan());
            
            JButton rejectBtn = createStyledButton("❌ Reject", new Color(255, 107, 107));
            rejectBtn.addActionListener(e -> rejectLoan());
            
            JButton deleteBtn = createStyledButton("🗑️ Delete", new Color(255, 77, 77));
            deleteBtn.addActionListener(e -> deleteLoan());
            
            buttonsPanel.add(createProductBtn);
            buttonsPanel.add(approveBtn);
            buttonsPanel.add(rejectBtn);
            buttonsPanel.add(deleteBtn);
        } else {
            // Employee can only delete pending loans
            JButton deleteBtn = createStyledButton("🗑️ Delete", new Color(255, 77, 77));
            deleteBtn.addActionListener(e -> deleteLoan());
            buttonsPanel.add(deleteBtn);
        }
        
        return buttonsPanel;
    }
    
    private void updateButtonStates() {
        int selectedRow = loansTable.getSelectedRow();
        boolean rowSelected = selectedRow != -1;
        
        if (rowSelected) {
            String status = (String) tableModel.getValueAt(selectedRow, 4);
            
            Component[] components = ((JPanel)getComponent(3)).getComponents();
            for (Component comp : components) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    String text = button.getText();
                    
                    if (text.contains("Approve") || text.contains("Reject")) {
                        button.setEnabled("Pending".equals(status));
                    } else if (text.contains("Delete")) {
                        if ("admin".equals(userRole)) {
                            // Admin can delete any loan
                            button.setEnabled(true);
                        } else {
                            // Employee can only delete pending loans
                            button.setEnabled("Pending".equals(status));
                        }
                    }
                }
            }
        }
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bgColor.darker().darker(), 1),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bgColor.darker(), 1),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });
        
        return button;
    }
    
    private void loadLoansData(String filter) {
        tableModel.setRowCount(0);
        String sql = "SELECT l.loan_id, l.loan_number, CONCAT(c.first_name, ' ', c.last_name) as client_name, " +
                    "l.amount, l.status, l.application_date, l.due_date, e.name as issued_by " +
                    "FROM loans l " +
                    "JOIN clients c ON l.client_id = c.client_id " +
                    "JOIN employees e ON l.processed_by = e.employee_id ";
        
        switch (filter) {
            case "Pending":
                sql += "WHERE l.status = 'Pending'";
                break;
            case "Approved":
                sql += "WHERE l.status = 'Approved'";
                break;
            case "Active":
                sql += "WHERE l.status = 'Active'";
                break;
            case "Rejected":
                sql += "WHERE l.status = 'Rejected'";
                break;
            case "Closed":
                sql += "WHERE l.status = 'Closed'";
                break;
            case "Due Loans":
                sql += "WHERE l.status = 'Active' AND l.due_date <= CURDATE()";
                break;
            case "Overdue":
                sql += "WHERE l.status = 'Active' AND l.due_date < CURDATE()";
                break;
            default:
                break;
        }
        
        sql += " ORDER BY l.application_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("loan_id"),
                        rs.getString("loan_number"),
                        rs.getString("client_name"),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        formatDate(rs.getDate("application_date")),
                        formatDate(rs.getDate("due_date")),
                        rs.getString("issued_by")
                    };
                    tableModel.addRow(row);
                }
            }
            
        } catch (SQLException ex) {
            showError("Error loading loans: " + ex.getMessage());
        }
    }
    
   
    private String formatDate(java.sql.Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    
    private void searchLoans() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            filterLoans();
            return;
        }
        
        tableModel.setRowCount(0);
        String sql = "SELECT l.loan_id, l.loan_number, CONCAT(c.first_name, ' ', c.last_name) as client_name, " +
                    "l.amount, l.status, l.application_date, l.due_date, e.name as issued_by " +
                    "FROM loans l " +
                    "JOIN clients c ON l.client_id = c.client_id " +
                    "JOIN employees e ON l.processed_by = e.employee_id " +
                    "WHERE l.loan_number LIKE ? OR c.first_name LIKE ? OR c.last_name LIKE ? OR " +
                    "e.name LIKE ? OR l.status LIKE ? OR l.amount LIKE ? " +
                    "ORDER BY l.application_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            stmt.setString(4, likeTerm);
            stmt.setString(5, likeTerm);
            stmt.setString(6, likeTerm);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("loan_id"),
                        rs.getString("loan_number"),
                        rs.getString("client_name"),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        formatDate(rs.getDate("application_date")),
                        formatDate(rs.getDate("due_date")),
                        rs.getString("issued_by")
                    };
                     tableModel.addRow(row);
                }
            }
            
        } catch (SQLException ex) {
            showError("Error searching loans: " + ex.getMessage());
        }
    }
    
    private void filterLoans() {
        String filter = (String) filterComboBox.getSelectedItem();
        loadLoansData(filter);
    }
    
    private void applyLoan() {
        ScreenManager.getInstance().showScreen(new ApplyLoanScreen(employeeId, userRole));
    }
    
    private void createLoanProduct() {
        ScreenManager.getInstance().showScreen(new CreateLoanProductScreen(employeeId, userRole));
    }
    
    private void viewLoan() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a loan to view");
            return;
        }
        
        int loanId = (Integer) tableModel.getValueAt(selectedRow, 0);
        showLoanDetails(loanId);
    }
    
    private void approveLoan() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a loan to approve");
            return;
        }
        
        int loanId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 4);
        
        if (!"Pending".equals(status)) {
            showError("Only pending loans can be approved");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to approve this loan?", "Confirm Approval", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE loans SET status = 'Approved', processed_by = ? WHERE loan_id = ?")) {
                
                stmt.setInt(1, employeeId);
                stmt.setInt(2, loanId);
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Loan approved successfully");
                filterLoans();
                
            } catch (SQLException ex) {
                showError("Error approving loan: " + ex.getMessage());
            }
        }
    }
    
    private void rejectLoan() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a loan to reject");
            return;
        }
        
        int loanId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 4);
        
        if (!"Pending".equals(status)) {
            showError("Only pending loans can be rejected");
            return;
        }
        
        String comment = JOptionPane.showInputDialog(this, 
            "Enter rejection reason:", "Rejection Reason", JOptionPane.QUESTION_MESSAGE);
        
        if (comment != null && !comment.trim().isEmpty()) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE loans SET status = 'Rejected', processed_by = ?, rejection_reason = ? WHERE loan_id = ?")) {
                
                stmt.setInt(1, employeeId);
                stmt.setString(2, comment.trim());
                stmt.setInt(3, loanId);
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Loan rejected successfully");
                filterLoans();
                
            } catch (SQLException ex) {
                showError("Error rejecting loan: " + ex.getMessage());
            }
        }
    }
    
    private void deleteLoan() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a loan to delete");
            return;
        }
        
        int loanId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 4);
        
        // Check permissions
        if (!"admin".equals(userRole) && !"Pending".equals(status)) {
            showError("Employees can only delete pending loans");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this loan?", "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // First delete related records to maintain referential integrity
                String[] deleteQueries = {
                    "DELETE FROM collaterals WHERE loan_id = ?",
                    "DELETE FROM guarantors WHERE loan_id = ?",
                    "DELETE FROM loan_payments WHERE loan_id = ?",
                    "DELETE FROM loans WHERE loan_id = ?"
                };
                
                for (String query : deleteQueries) {
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setInt(1, loanId);
                        stmt.executeUpdate();
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Loan deleted successfully");
                filterLoans();
                
            } catch (SQLException ex) {
                showError("Error deleting loan: " + ex.getMessage());
            }
        }
    }
    
    private void showLoanDetails(int loanId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // First get the main loan details
            String loanSql = "SELECT l.*, c.first_name, c.last_name, c.phone_number, " +
                           "lp.product_name, lp.interest_rate, lp.interest_type, " +
                           "lp.calculation_method, lp.loan_term_months, lp.installment_type, " +
                           "l.rejection_reason, e.name as issued_by " +
                           "FROM loans l " +
                           "JOIN clients c ON l.client_id = c.client_id " +
                           "LEFT JOIN loan_products lp ON l.product_id = lp.product_id " +
                           "JOIN employees e ON l.processed_by = e.employee_id " +
                           "WHERE l.loan_id = ?";
            
            StringBuilder details = new StringBuilder();
            double amount = 0;
            double interestRate = 0;
            int loanTermMonths = 0;
            String calculationMethod = "";
            String installmentType = "";

            try (PreparedStatement loanStmt = conn.prepareStatement(loanSql)) {
                loanStmt.setInt(1, loanId);
                try (ResultSet rs = loanStmt.executeQuery()) {
                    if (rs.next()) {
                        amount = rs.getDouble("amount");
                        interestRate = rs.getDouble("interest_rate");
                        loanTermMonths = rs.getInt("loan_term_months");
                        calculationMethod = rs.getString("calculation_method");
                        installmentType = rs.getString("installment_type");
                        
                        // Calculate total amount with interest
                        double totalInterest = 0;
                        double totalAmount = amount;
                        
                        if ("FLAT".equals(calculationMethod)) {
                            totalInterest = amount * (interestRate / 100) * (loanTermMonths / 12.0);
                            totalAmount = amount + totalInterest;
                        } else if ("REDUCING".equals(calculationMethod)) {
                            double monthlyInterest = interestRate / 100 / 12;
                            double monthlyPayment = amount * monthlyInterest * 
                                Math.pow(1 + monthlyInterest, loanTermMonths) /
                                (Math.pow(1 + monthlyInterest, loanTermMonths) - 1);
                            totalInterest = (monthlyPayment * loanTermMonths) - amount;
                            totalAmount = amount + totalInterest;
                        }
                        
                        // Calculate installment amount
                        int numberOfInstallments = loanTermMonths;
                        if ("Weekly".equals(installmentType)) {
                            numberOfInstallments = loanTermMonths * 4; // Convert months to weeks
                        } else if ("Quarterly".equals(installmentType)) {
                            numberOfInstallments = loanTermMonths / 3;
                        } else if ("Annually".equals(installmentType)) {
                            numberOfInstallments = loanTermMonths / 12;
                        }
                        
                        double installmentAmount = totalAmount / numberOfInstallments;
                        
                        details.append("LOAN DETAILS\n");
                        details.append("============\n\n");
                        
                        details.append("Basic Information:\n");
                        details.append("• Loan Number: ").append(rs.getString("loan_number")).append("\n");
                        details.append("• Client: ").append(rs.getString("first_name")).append(" ")
                               .append(rs.getString("last_name")).append("\n");
                        details.append("• Phone: ").append(rs.getString("phone_number")).append("\n");
                        details.append("• Product: ").append(rs.getString("product_name")).append("\n");
                        details.append("• Status: ").append(rs.getString("status")).append("\n");
                        details.append("• Issued By: ").append(rs.getString("issued_by")).append("\n");
                        details.append("• Application Date: ").append(formatDate(rs.getDate("application_date"))).append("\n");
                        details.append("• Due Date: ").append(formatDate(rs.getDate("due_date"))).append("\n\n");
                        
                        details.append("Financial Details:\n");
                        details.append("• Principal Amount: ZMW ").append(String.format("%,.2f", amount)).append("\n");
                        details.append("• Interest Rate: ").append(interestRate).append("%\n");
                        details.append("• Interest Type: ").append(rs.getString("interest_type")).append("\n");
                        details.append("• Calculation Method: ").append(calculationMethod).append("\n");
                        details.append("• Loan Term: ").append(loanTermMonths).append(" months\n");
                        details.append("• Installment Type: ").append(installmentType).append("\n");
                        details.append("• Total Interest: ZMW ").append(String.format("%,.2f", totalInterest)).append("\n");
                        details.append("• Total Amount Payable: ZMW ").append(String.format("%,.2f", totalAmount)).append("\n");
                        details.append("• Number of Installments: ").append(numberOfInstallments).append("\n");
                        details.append("• Installment Amount: ZMW ").append(String.format("%,.2f", installmentAmount)).append("\n");
                        
                        if ("Rejected".equals(rs.getString("status")) && rs.getString("rejection_reason") != null) {
                            details.append("\nRejection Reason:\n");
                            details.append("• ").append(rs.getString("rejection_reason")).append("\n");
                        }
                    }
                }
            }

            // Now get collateral details
            String collateralSql = "SELECT description, amount, currency, valuation_date FROM collaterals WHERE loan_id = ?";
            double totalCollateralValue = 0;
            int collateralCount = 0;
            
            details.append("\nCOLLATERAL DETAILS\n");
            details.append("==================\n");
            
            try (PreparedStatement collateralStmt = conn.prepareStatement(collateralSql)) {
                collateralStmt.setInt(1, loanId);
                try (ResultSet rs = collateralStmt.executeQuery()) {
                    if (!rs.isBeforeFirst()) {
                        details.append("No collateral registered for this loan.\n");
                    } else {
                        while (rs.next()) {
                            collateralCount++;
                            double collateralAmount = rs.getDouble("amount");
                            totalCollateralValue += collateralAmount;
                            
                            details.append("\nCollateral ").append(collateralCount).append(":\n");
                            details.append("• Description: ").append(rs.getString("description")).append("\n");
                            details.append("• Value: ").append(rs.getString("currency")).append(" ")
                                   .append(String.format("%,.2f", collateralAmount)).append("\n");
                            
                            if (rs.getDate("valuation_date") != null) {
                                details.append("• Valuation Date: ").append(formatDate(rs.getDate("valuation_date"))).append("\n");
                            }
                        }
                        
                        details.append("\nTotal Collateral Value: ").append("ZMW ") // Assuming all in ZMW
                               .append(String.format("%,.2f", totalCollateralValue)).append("\n");
                        details.append("Loan-to-Value Ratio: ").append(String.format("%.1f%%", (amount / totalCollateralValue) * 100)).append("\n");
                    }
                }
            }

            // Create and display the details dialog
            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setBackground(new Color(240, 240, 240));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(700, 500)); // Increased size for collateral info
            
            JOptionPane.showMessageDialog(this, scrollPane, "Loan Details with Collateral", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            showError("Error loading loan details: " + ex.getMessage());
        }
    }
    
    private void navigateToDashboard() {
        if ("admin".equals(userRole)) {
            ScreenManager.getInstance().showScreen(new AdminDashboard(employeeId, "Admin"));
        } else {
            ScreenManager.getInstance().showScreen(new EmployeeDashboard(employeeId, "Employee"));
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}