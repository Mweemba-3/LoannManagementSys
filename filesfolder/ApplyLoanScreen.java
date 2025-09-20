import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ApplyLoanScreen extends JPanel {
    private int employeeId;
    private String userRole;
    private JTextField searchField;
    private JTextField clientNameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> productComboBox;
    private JTextField interestRateField;
    private JComboBox<String> calculationMethodComboBox;
    private JTextField loanTermField;
    private JTextField amountField;
    private JComboBox<String> installmentTypeComboBox;
    private JComboBox<String> loanFeeComboBox;
    private JComboBox<String> category1ComboBox;
    private JComboBox<String> category2ComboBox;
    private JTextArea collateralArea;
    private JTextArea guarantorsArea;
    private JTextArea calculationDetailsArea;
    
    private Integer selectedClientId = null;
    private Map<String, Integer> clientSearchResults = new HashMap<>();
    private JDialog searchResultsDialog;
    
    // New fields to handle term units
    private JLabel loanTermLabel;
    private String currentTermUnit = "Months"; // Default

    public ApplyLoanScreen(int employeeId, String userRole) {
        this.employeeId = employeeId;
        this.userRole = userRole;
        initializeLoanSequenceTable();
        initUI();
        loadLoanProducts();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));

        JPanel clientSearchPanel = createClientSearchPanel();
        mainPanel.add(clientSearchPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Loan Details", createLoanDetailsPanel());
        tabbedPane.addTab("Collateral", createCollateralPanel());
        tabbedPane.addTab("Guarantors", createGuarantorsPanel());
        tabbedPane.addTab("Calculation", createCalculationPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonsPanel = createButtonsPanel();
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("APPLY FOR LOAN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = createStyledButton("Back to Loans", new Color(57, 62, 70));
        backBtn.addActionListener(e -> goBackToLoans());
        headerPanel.add(backBtn, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createClientSearchPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Client Search"));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(800, 120));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton searchBtn = createStyledButton("Search Client", new Color(0, 173, 181));
        searchBtn.addActionListener(e -> searchClients());

        clientNameField = new JTextField();
        clientNameField.setEditable(false);
        clientNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        phoneField = new JTextField();
        phoneField.setEditable(false);
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        emailField = new JTextField();
        emailField.setEditable(false);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(new JLabel("Search Client (Name/Phone/ID):"));
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(new JLabel(""));
        panel.add(new JLabel("Client Name:"));
        panel.add(clientNameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        return panel;
    }

    private JPanel createLoanDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("Loan Product (Optional):"));
        productComboBox = new JComboBox<>();
        productComboBox.addItem("Select Product");
        productComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productComboBox.addActionListener(e -> autoFillFromProduct());
        panel.add(productComboBox);

        panel.add(new JLabel("Interest Rate (%):"));
        interestRateField = new JTextField();
        interestRateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(interestRateField);

        panel.add(new JLabel("Calculation Method:"));
        calculationMethodComboBox = new JComboBox<>(new String[]{"FLAT", "REDUCING"});
        calculationMethodComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(calculationMethodComboBox);

        // Modified loan term label that will change based on product
        loanTermLabel = new JLabel("Loan Term:");
        panel.add(loanTermLabel);
        loanTermField = new JTextField();
        loanTermField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(loanTermField);

        panel.add(new JLabel("Amount (ZMW):"));
        amountField = new JTextField();
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        amountField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { calculateLoan(); }
            public void removeUpdate(DocumentEvent e) { calculateLoan(); }
            public void insertUpdate(DocumentEvent e) { calculateLoan(); }
        });
        panel.add(amountField);

        panel.add(new JLabel("Installment Type:"));
        installmentTypeComboBox = new JComboBox<>(new String[]{"Weekly", "Monthly", "Quarterly", "Annually"});
        installmentTypeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        installmentTypeComboBox.addActionListener(e -> calculateLoan());
        panel.add(installmentTypeComboBox);

        panel.add(new JLabel("Loan Fee Type:"));
        loanFeeComboBox = new JComboBox<>(new String[]{"Cash", "Mobile", "Bank"});
        loanFeeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(loanFeeComboBox);

        panel.add(new JLabel("Category 1:"));
        category1ComboBox = new JComboBox<>(new String[]{"Personal", "Business", "Education"});
        category1ComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(category1ComboBox);

        panel.add(new JLabel("Category 2:"));
        category2ComboBox = new JComboBox<>(new String[]{"Short-Term", "Long-Term", "Microloan"});
        category2ComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(category2ComboBox);

        return panel;
    }

    private JPanel createCollateralPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Collateral Details (Optional)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        collateralArea = new JTextArea(8, 50);
        collateralArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        collateralArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        collateralArea.setText("Format: Description - Value (ZMW)\nExample: Car - 50000.00\nHouse - 150000.00");

        JScrollPane scrollPane = new JScrollPane(collateralArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGuarantorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Guarantors (Optional)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        guarantorsArea = new JTextArea(8, 50);
        guarantorsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        guarantorsArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        guarantorsArea.setText("Format: Name - Phone - Relationship - Amount Guaranteed\nExample: John Doe - 0971234567 - Friend - 10000.00");

        JScrollPane scrollPane = new JScrollPane(guarantorsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCalculationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Loan Calculation Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        calculationDetailsArea = new JTextArea(12, 50);
        calculationDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        calculationDetailsArea.setEditable(false);
        calculationDetailsArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        calculationDetailsArea.setText("Fill in loan details to see calculation results...");

        JScrollPane scrollPane = new JScrollPane(calculationDetailsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(245, 245, 245));

        JButton clearBtn = createStyledButton("Clear Form", new Color(108, 117, 125));
        clearBtn.addActionListener(e -> clearForm());

        JButton calculateBtn = createStyledButton("Calculate", new Color(40, 167, 69));
        calculateBtn.addActionListener(e -> calculateLoan());

        JButton submitBtn = createStyledButton("Submit Application", new Color(0, 123, 255));
        submitBtn.addActionListener(e -> submitApplication());

        panel.add(clearBtn);
        panel.add(calculateBtn);
        panel.add(submitBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    private void initializeLoanSequenceTable() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkTableSQL = "SELECT COUNT(*) FROM loan_sequence";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkTableSQL)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String initSQL = "INSERT INTO loan_sequence (last_loan_number) VALUES (0)";
                    try (Statement initStmt = conn.createStatement()) {
                        initStmt.executeUpdate(initSQL);
                    }
                }
            }
        } catch (SQLException ex) {
            showError("Error initializing loan sequence: " + ex.getMessage());
        }
    }

    private void loadLoanProducts() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT product_name FROM loan_products ORDER BY product_name";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    productComboBox.addItem(rs.getString("product_name"));
                }
            }
        } catch (SQLException ex) {
            showError("Error loading loan products: " + ex.getMessage());
        }
    }

    private void searchClients() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showError("Please enter a search term");
            return;
        }

        clientSearchResults.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT client_id, first_name, middle_name, last_name, phone_number, email " +
                        "FROM clients WHERE first_name LIKE ? OR last_name LIKE ? OR phone_number LIKE ? OR id_number LIKE ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String likeTerm = "%" + searchTerm + "%";
                stmt.setString(1, likeTerm);
                stmt.setString(2, likeTerm);
                stmt.setString(3, likeTerm);
                stmt.setString(4, likeTerm);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.isBeforeFirst()) {
                        showError("No clients found matching: " + searchTerm);
                        return;
                    }
                    
                    // Create search results dialog
                    if (searchResultsDialog != null) {
                        searchResultsDialog.dispose();
                    }
                    
                    searchResultsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Search Results", true);
                    searchResultsDialog.setLayout(new BorderLayout());
                    searchResultsDialog.setSize(500, 300);
                    searchResultsDialog.setLocationRelativeTo(this);
                    
                    JPanel resultsPanel = new JPanel(new BorderLayout());
                    DefaultListModel<String> listModel = new DefaultListModel<>();
                    JList<String> resultsList = new JList<>(listModel);
                    
                    while (rs.next()) {
                        int clientId = rs.getInt("client_id");
                        String firstName = rs.getString("first_name");
                        String middleName = rs.getString("middle_name");
                        String lastName = rs.getString("last_name");
                        String phone = rs.getString("phone_number");
                        
                        String displayName = firstName + " " + (middleName != null ? middleName + " " : "") + lastName + " - " + phone;
                        listModel.addElement(displayName);
                        clientSearchResults.put(displayName, clientId);
                    }
                    
                    resultsList.addListSelectionListener(e -> {
                        if (!e.getValueIsAdjusting()) {
                            String selected = resultsList.getSelectedValue();
                            if (selected != null && clientSearchResults.containsKey(selected)) {
                                selectedClientId = clientSearchResults.get(selected);
                                loadClientDetails(selectedClientId);
                                searchResultsDialog.dispose();
                            }
                        }
                    });
                    
                    resultsPanel.add(new JScrollPane(resultsList), BorderLayout.CENTER);
                    searchResultsDialog.add(resultsPanel, BorderLayout.CENTER);
                    searchResultsDialog.setVisible(true);
                }
            }
        } catch (SQLException ex) {
            showError("Error searching clients: " + ex.getMessage());
        }
    }

    private void loadClientDetails(int clientId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT first_name, middle_name, last_name, phone_number, email FROM clients WHERE client_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, clientId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String firstName = rs.getString("first_name");
                        String middleName = rs.getString("middle_name");
                        String lastName = rs.getString("last_name");
                        String phone = rs.getString("phone_number");
                        String email = rs.getString("email");
                        
                        clientNameField.setText(firstName + " " + (middleName != null ? middleName + " " : "") + lastName);
                        phoneField.setText(phone);
                        emailField.setText(email != null ? email : "");
                    }
                }
            }
        } catch (SQLException ex) {
            showError("Error loading client details: " + ex.getMessage());
        }
    }

    private void autoFillFromProduct() {
        String selectedProduct = (String) productComboBox.getSelectedItem();
        if (selectedProduct == null || selectedProduct.equals("Select Product")) {
            // Enable all fields if no product is selected
            enableLoanDetailsFields(true);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT interest_rate, calculation_method, loan_term_months, installment_type, " +
                        "loan_fee_type, category_1, category_2 FROM loan_products WHERE product_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, selectedProduct);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Fill fields from the loan product
                        interestRateField.setText(String.valueOf(rs.getDouble("interest_rate")));
                        calculationMethodComboBox.setSelectedItem(rs.getString("calculation_method"));
                        loanTermField.setText(String.valueOf(rs.getInt("loan_term_months")));
                        installmentTypeComboBox.setSelectedItem(rs.getString("installment_type"));
                        loanFeeComboBox.setSelectedItem(rs.getString("loan_fee_type"));
                        category1ComboBox.setSelectedItem(rs.getString("category_1"));
                        category2ComboBox.setSelectedItem(rs.getString("category_2"));
                        
                        // Update term unit based on installment type
                        String installmentType = rs.getString("installment_type");
                        if ("Weekly".equals(installmentType)) {
                            loanTermLabel.setText("Loan Term (Weeks):");
                            currentTermUnit = "Weeks";
                        } else {
                            loanTermLabel.setText("Loan Term (Months):");
                            currentTermUnit = "Months";
                        }
                        
                      // Disable fields except amount
                        enableLoanDetailsFields(false);
                        amountField.setEnabled(true);
                        
                        // Calculate loan based on product details
                        calculateLoan();
                    }
                }
            }
        } catch (SQLException ex) {
            showError("Error loading product details: " + ex.getMessage());
        }
    }
    
    private void enableLoanDetailsFields(boolean enabled) {
        interestRateField.setEnabled(enabled);
        calculationMethodComboBox.setEnabled(enabled);
        loanTermField.setEnabled(enabled);
        installmentTypeComboBox.setEnabled(enabled);
        loanFeeComboBox.setEnabled(enabled);
        category1ComboBox.setEnabled(enabled);
        category2ComboBox.setEnabled(enabled);
    }

    private String generateLoanNumber(Connection conn) throws SQLException {
        String updateSQL = "UPDATE loan_sequence SET last_loan_number = last_loan_number + 1";
        String selectSQL = "SELECT last_loan_number FROM loan_sequence";
        
        try (Statement updateStmt = conn.createStatement(); Statement selectStmt = conn.createStatement()) {
            updateStmt.executeUpdate(updateSQL);
            try (ResultSet rs = selectStmt.executeQuery(selectSQL)) {
                if (rs.next()) {
                    int loanNumber = rs.getInt("last_loan_number");
                    return String.format("LN%06d", loanNumber);
                }
            }
        }
        throw new SQLException("Failed to generate loan number");
    }

    private void calculateLoan() {
        try {
            double principal = Double.parseDouble(amountField.getText());
            double annualInterestRate = Double.parseDouble(interestRateField.getText());
            int loanTerm = Integer.parseInt(loanTermField.getText());
            String calculationMethod = (String) calculationMethodComboBox.getSelectedItem();
            String installmentType = (String) installmentTypeComboBox.getSelectedItem();

            double totalInterest = 0;
            double totalAmount = 0;
            double installmentAmount = 0;
            int numberOfInstallments = 1;

            // Check if this is a short-term loan (1 month or 1-4 weeks)
            boolean isShortTerm = ("Weekly".equals(installmentType) && loanTerm <= 4) || 
                                 ("Monthly".equals(installmentType) && loanTerm == 1);

            if ("FLAT".equals(calculationMethod)) {
                // FLAT INTEREST: Principal × Annual Rate × (Term in Years)
                if ("Weekly".equals(installmentType)) {
                    // For weekly loans, calculate interest based on weeks
                    totalInterest = principal * (annualInterestRate / 100);
                } else {
                    // For monthly/quarterly/annual loans, calculate based on months
                    totalInterest = principal * (annualInterestRate / 100);
                }
                totalAmount = principal + totalInterest;
                
                // For short-term loans (1-4 weeks or 1 month), use single payment
                if (isShortTerm) {
                    numberOfInstallments = 1;
                    installmentAmount = totalAmount;
                } else {
                    // Determine number of installments based on installment type
                    if ("Weekly".equals(installmentType)) {
                        numberOfInstallments = loanTerm;
                        installmentAmount = totalAmount / numberOfInstallments;
                    } else if ("Monthly".equals(installmentType)) {
                        numberOfInstallments = loanTerm;
                        installmentAmount = totalAmount / numberOfInstallments;
                    } else if ("Quarterly".equals(installmentType)) {
                        numberOfInstallments = (int) Math.ceil(loanTerm / 3.0);
                        installmentAmount = totalAmount / numberOfInstallments;
                    } else if ("Annually".equals(installmentType)) {
                        numberOfInstallments = (int) Math.ceil(loanTerm / 12.0);
                        installmentAmount = totalAmount / numberOfInstallments;
                    }
                }
            } else {
                // REDUCING BALANCE calculation
                double periodicInterestRate;
                
                // Calculate number of installments and periodic interest rate
                if ("Weekly".equals(installmentType)) {
                    numberOfInstallments = isShortTerm ? 1 : loanTerm;
                    periodicInterestRate = annualInterestRate / 100 / 52; // Weekly interest rate
                } else if ("Monthly".equals(installmentType)) {
                    numberOfInstallments = isShortTerm ? 1 : loanTerm;
                    periodicInterestRate = annualInterestRate / 100 / 12; // Monthly interest rate
                } else if ("Quarterly".equals(installmentType)) {
                    numberOfInstallments = (int) Math.ceil(loanTerm / 3.0);
                    periodicInterestRate = annualInterestRate / 100 / 4; // Quarterly interest rate
                } else {
                    numberOfInstallments = (int) Math.ceil(loanTerm / 12.0);
                    periodicInterestRate = annualInterestRate / 100; // Annual interest rate
                }
                
                if (isShortTerm) {
                    // Single payment for short-term loans
                    installmentAmount = principal * (1 + periodicInterestRate * numberOfInstallments);
                    totalAmount = installmentAmount;
                    totalInterest = totalAmount - principal;
                } else {
                    // Standard reducing balance calculation for longer terms
                    double power = Math.pow(1 + periodicInterestRate, numberOfInstallments);
                    installmentAmount = principal * periodicInterestRate * power / (power - 1);
                    
                    totalAmount = installmentAmount * numberOfInstallments;
                    totalInterest = totalAmount - principal;
                }
            }

            // Round to 2 decimal places for currency
            installmentAmount = Math.round(installmentAmount * 100.0) / 100.0;
            totalAmount = Math.round(totalAmount * 100.0) / 100.0;
            totalInterest = Math.round(totalInterest * 100.0) / 100.0;

            // Build results display
            StringBuilder details = new StringBuilder();
            details.append("LOAN CALCULATION DETAILS\n");
            details.append("=======================\n");
            details.append(String.format("Principal Amount:  ZMW %,.2f%n", principal));
            details.append(String.format("Interest Rate:     %.2f%% %s%n", annualInterestRate, calculationMethod));
            details.append(String.format("Term:              %d %s%n", loanTerm, currentTermUnit.toLowerCase()));
            details.append(String.format("Installment Type:  %s%n", installmentType));
            details.append("-----------------------\n");
            
            if (isShortTerm) {
                details.append("NOTE: Single payment (short-term loan)\n");
                details.append("-----------------------\n");
            }
            
            details.append(String.format("Installment Amount: ZMW %,.2f%n", installmentAmount));
            details.append(String.format("Total Interest:     ZMW %,.2f%n", totalInterest));
            details.append(String.format("Total Repayment:    ZMW %,.2f%n", totalAmount));
            details.append(String.format("Number of Payments: %d%n", numberOfInstallments));

            calculationDetailsArea.setText(details.toString());
            
        } catch (NumberFormatException e) {
            calculationDetailsArea.setText("Please fill all required fields with valid numbers to calculate...");
        } catch (Exception e) {
            calculationDetailsArea.setText("Error in calculation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void submitApplication() {
        if (selectedClientId == null) {
            showError("Please select a client first");
            return;
        }

        if (!validateLoanDetails()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to submit this loan application?", "Confirm Submission", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement loanStmt = null;
            
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false);

                String loanNumber = generateLoanNumber(conn);

                java.util.Date currentDate = new java.util.Date();
                java.sql.Date applicationDate = new java.sql.Date(currentDate.getTime());
                
                // Calculate due date based on term unit
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                
                if ("Weeks".equals(currentTermUnit)) {
                    cal.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(loanTermField.getText()));
                } else {
                    cal.add(Calendar.MONTH, Integer.parseInt(loanTermField.getText()));
                }
                
                java.sql.Date dueDate = new java.sql.Date(cal.getTime().getTime());

                String loanSql = "INSERT INTO loans (loan_number, client_id, product_id, amount, interest_rate, calculation_method, loan_term_months, grace_period_months, installment_type, application_date, due_date, status, processed_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending', ?)";
                
                loanStmt = conn.prepareStatement(loanSql);
                
                Integer productId = getProductIdIfSelected(conn);
                
                loanStmt.setString(1, loanNumber);
                loanStmt.setInt(2, selectedClientId);
                if (productId != null) {
                    loanStmt.setInt(3, productId);
                } else {
                    loanStmt.setNull(3, Types.INTEGER);
                }
                loanStmt.setDouble(4, Double.parseDouble(amountField.getText()));
                loanStmt.setDouble(5, Double.parseDouble(interestRateField.getText()));
                loanStmt.setString(6, (String) calculationMethodComboBox.getSelectedItem());
                
                // Store the term as months in database (convert weeks to months)
                int loanTermMonths = Integer.parseInt(loanTermField.getText());
                if ("Weeks".equals(currentTermUnit)) {
                    loanTermMonths = (int) Math.ceil(loanTermMonths / 4.0);
                }
                loanStmt.setInt(7, loanTermMonths);
                
                loanStmt.setInt(8, 0); // No grace period
                loanStmt.setString(9, (String) installmentTypeComboBox.getSelectedItem());
                loanStmt.setDate(10, applicationDate);
                loanStmt.setDate(11, dueDate);
                loanStmt.setInt(12, employeeId);

                int rowsAffected = loanStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    String getLoanIdSql = "SELECT loan_id FROM loans WHERE loan_number = ?";
                    try (PreparedStatement getIdStmt = conn.prepareStatement(getLoanIdSql)) {
                        getIdStmt.setString(1, loanNumber);
                        try (ResultSet rs = getIdStmt.executeQuery()) {
                            if (rs.next()) {
                                int loanId = rs.getInt("loan_id");
                                processCollateral(conn, loanId);
                                processGuarantors(conn, loanId);
                                conn.commit();
                                JOptionPane.showMessageDialog(this, "Loan application submitted successfully with Loan Number: " + loanNumber);
                                clearForm();
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                try {
                    if (conn != null) conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                showError("Error submitting application: " + ex.getMessage());
            } finally {
                try {
                    if (loanStmt != null) loanStmt.close();
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    private Integer getProductIdIfSelected(Connection conn) {
        String selectedProduct = (String) productComboBox.getSelectedItem();
        if (selectedProduct == null || selectedProduct.equals("Select Product")) {
            return null;
        }

        String sql = "SELECT product_id FROM loan_products WHERE product_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, selectedProduct);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("product_id");
                }
            }
        } catch (SQLException ex) {
            showError("Error getting product ID: " + ex.getMessage());
        }
        return null;
    }

    private void processCollateral(Connection conn, int loanId) throws SQLException {
        String collateralText = collateralArea.getText().trim();
        if (collateralText.isEmpty() || collateralText.startsWith("Format:")) {
            return;
        }

        String[] lines = collateralText.split("\n");
        String sql = "INSERT INTO collaterals (loan_id, description, amount, currency) VALUES (?, ?, ?, 'ZMW')";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String line : lines) {
                if (line.contains("-")) {
                    String[] parts = line.split("-");
                    if (parts.length >= 2) {
                        String description = parts[0].trim();
                        try {
                            double amount = Double.parseDouble(parts[1].trim());
                            stmt.setInt(1, loanId);
                            stmt.setString(2, description);
                            stmt.setDouble(3, amount);
                            stmt.executeUpdate();
                        } catch (NumberFormatException e) {
                            // Skip invalid entries
                        }
                    }
                }
            }
        }
    }

    private void processGuarantors(Connection conn, int loanId) throws SQLException {
        String guarantorsText = guarantorsArea.getText().trim();
        if (guarantorsText.isEmpty() || guarantorsText.startsWith("Format:")) {
            return;
        }

        String[] lines = guarantorsText.split("\n");
        String sql = "INSERT INTO guarantors (loan_id, name, phone_number, relationship, amount_guaranteed) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String line : lines) {
                if (line.contains("-")) {
                    String[] parts = line.split("-");
                    if (parts.length >= 4) {
                        try {
                            stmt.setInt(1, loanId);
                            stmt.setString(2, parts[0].trim());
                            stmt.setString(3, parts[1].trim());
                            stmt.setString(4, parts[2].trim());
                            stmt.setDouble(5, Double.parseDouble(parts[3].trim()));
                            stmt.executeUpdate();
                        } catch (NumberFormatException e) {
                            // Skip invalid entries
                        }
                    }
                }
            }
        }
    }

    private boolean validateLoanDetails() {
        try {
            if (interestRateField.getText().trim().isEmpty()) {
                showError("Please enter interest rate");
                return false;
            }
            if (loanTermField.getText().trim().isEmpty()) {
                showError("Please enter loan term");
                return false;
            }
            if (amountField.getText().trim().isEmpty()) {
                showError("Please enter loan amount");
                return false;
            }

            Double.parseDouble(interestRateField.getText());
            Integer.parseInt(loanTermField.getText());
            Double.parseDouble(amountField.getText());

            return true;
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for interest rate, loan term, and amount");
            return false;
        }
    }

    private void clearForm() {
        selectedClientId = null;
        searchField.setText("");
        clientNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        productComboBox.setSelectedIndex(0);
        interestRateField.setText("");
        calculationMethodComboBox.setSelectedIndex(0);
        loanTermField.setText("");
        amountField.setText("");
        installmentTypeComboBox.setSelectedIndex(0);
        loanFeeComboBox.setSelectedIndex(0);
        category1ComboBox.setSelectedIndex(0);
        category2ComboBox.setSelectedIndex(0);
        collateralArea.setText("Format: Description - Value (ZMW)\nExample: Car - 50000.00\nHouse - 150000.00");
        guarantorsArea.setText("Format: Name - Phone - Relationship - Amount Guaranteed\nExample: John Doe - 0971234567 - Friend - 10000.00");
        calculationDetailsArea.setText("Fill in loan details to see calculation results...");
        
        // Enable all fields when clearing form
        enableLoanDetailsFields(true);
        // Reset to default term unit
        loanTermLabel.setText("Loan Term (Months):");
        currentTermUnit = "Months";
    }

    private void goBackToLoans() {
        ScreenManager.getInstance().showScreen(new LoansScreen(employeeId, userRole));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
} 