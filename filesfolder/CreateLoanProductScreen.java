import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class CreateLoanProductScreen extends JPanel {
    private int employeeId;
    private String userRole;
    
    private JTextField productNameField;
    private JTextField interestRateField;
    private JComboBox<String> calculationMethodComboBox;
    private JTextField loanTermField;
    private JTextField minAmountField;
    private JTextField maxAmountField;
    private JTextField gracePeriodField;
    private JComboBox<String> installmentTypeComboBox;
    private JComboBox<String> loanFeeTypeComboBox;
    private JComboBox<String> category1ComboBox;
    private JComboBox<String> category2ComboBox;
    private JComboBox<String> refinanceComboBox;

    public CreateLoanProductScreen(int employeeId, String userRole) {
        this.employeeId = employeeId;
        this.userRole = userRole;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Form Panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = createButtonsPanel();
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("CREATE LOAN PRODUCT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = createStyledButton("⬅️ Back to Loans", new Color(57, 62, 70));
        backBtn.addActionListener(e -> goBackToLoans());
        headerPanel.add(backBtn, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(Color.WHITE);

        // Product Name
        formPanel.add(new JLabel("Product Name:"));
        productNameField = new JTextField();
        productNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(productNameField);

        // Interest Rate
        formPanel.add(new JLabel("Interest Rate (%):"));
        interestRateField = new JTextField();
        interestRateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(interestRateField);

        // Calculation Method
        formPanel.add(new JLabel("Calculation Method:"));
        calculationMethodComboBox = new JComboBox<>(new String[]{"FLAT", "REDUCING"});
        calculationMethodComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(calculationMethodComboBox);

        // Loan Term
        formPanel.add(new JLabel("Loan Term:"));
        loanTermField = new JTextField();
        loanTermField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(loanTermField);

        // Minimum Amount
        formPanel.add(new JLabel("Minimum Amount (ZMW):"));
        minAmountField = new JTextField();
        minAmountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(minAmountField);

        // Maximum Amount
        formPanel.add(new JLabel("Maximum Amount (ZMW):"));
        maxAmountField = new JTextField();
        maxAmountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(maxAmountField);

        // Grace Period
        formPanel.add(new JLabel("Grace Period (Months):"));
        gracePeriodField = new JTextField();
        gracePeriodField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(gracePeriodField);

        // Installment Type
        formPanel.add(new JLabel("Installment Type:"));
        installmentTypeComboBox = new JComboBox<>(new String[]{"Weekly", "Monthly", "Quarterly", "Annually"});
        installmentTypeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(installmentTypeComboBox);

        // Loan Fee Type
        formPanel.add(new JLabel("Loan Fee Type:"));
        loanFeeTypeComboBox = new JComboBox<>(new String[]{"Cash", "Mobile", "Bank"});
        loanFeeTypeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(loanFeeTypeComboBox);

        // Category 1
        formPanel.add(new JLabel("Category 1:"));
        category1ComboBox = new JComboBox<>(new String[]{"Personal", "Business", "Education"});
        category1ComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(category1ComboBox);

        // Category 2
        formPanel.add(new JLabel("Category 2:"));
        category2ComboBox = new JComboBox<>(new String[]{"Short-Term", "Long-Term", "Microloan"});
        category2ComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(category2ComboBox);

        // Refinance
        formPanel.add(new JLabel("Refinance:"));
        refinanceComboBox = new JComboBox<>(new String[]{"Yes", "No"});
        refinanceComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(refinanceComboBox);

        return formPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonsPanel.setBackground(new Color(245, 245, 245));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton clearBtn = createStyledButton("🗑️ Clear Form", new Color(255, 159, 67));
        clearBtn.addActionListener(e -> clearForm());

        JButton createBtn = createStyledButton("✅ Create Product", new Color(97, 218, 121));
        createBtn.addActionListener(e -> createLoanProduct());

        buttonsPanel.add(clearBtn);
        buttonsPanel.add(createBtn);

        return buttonsPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void createLoanProduct() {
        if (!validateForm()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to create this loan product?", "Confirm Creation", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO loan_products (product_name, interest_rate, calculation_method, " +
                           "loan_term_months, min_amount, max_amount, grace_period_months, installment_type, " +
                           "loan_fee_type, category_1, category_2, refinance, created_by) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, productNameField.getText().trim());
                    stmt.setDouble(2, Double.parseDouble(interestRateField.getText()));
                    stmt.setString(3, (String) calculationMethodComboBox.getSelectedItem());
                    stmt.setInt(4, Integer.parseInt(loanTermField.getText()));
                    stmt.setDouble(5, Double.parseDouble(minAmountField.getText()));
                    stmt.setDouble(6, Double.parseDouble(maxAmountField.getText()));
                    stmt.setInt(7, Integer.parseInt(gracePeriodField.getText()));
                    stmt.setString(8, (String) installmentTypeComboBox.getSelectedItem());
                    stmt.setString(9, (String) loanFeeTypeComboBox.getSelectedItem());
                    stmt.setString(10, (String) category1ComboBox.getSelectedItem());
                    stmt.setString(11, (String) category2ComboBox.getSelectedItem());
                    stmt.setString(12, (String) refinanceComboBox.getSelectedItem());
                    stmt.setInt(13, employeeId);

                    stmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, "Loan product created successfully!");
                    clearForm();
                }
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) {
                    showError("A loan product with this name already exists. Please choose a different name.");
                } else {
                    showError("Error creating loan product: " + ex.getMessage());
                }
            }
        }
    }

    private boolean validateForm() {
        try {
            // Check required fields
            if (productNameField.getText().trim().isEmpty()) {
                showError("Please enter product name");
                return false;
            }
            if (interestRateField.getText().trim().isEmpty()) {
                showError("Please enter interest rate");
                return false;
            }
            if (loanTermField.getText().trim().isEmpty()) {
                showError("Please enter loan term");
                return false;
            }
            if (minAmountField.getText().trim().isEmpty()) {
                showError("Please enter minimum amount");
                return false;
            }
            if (maxAmountField.getText().trim().isEmpty()) {
                showError("Please enter maximum amount");
                return false;
            }
            if (gracePeriodField.getText().trim().isEmpty()) {
                showError("Please enter grace period");
                return false;
            }

            // Validate numeric values
            double interestRate = Double.parseDouble(interestRateField.getText());
            int loanTerm = Integer.parseInt(loanTermField.getText());
            double minAmount = Double.parseDouble(minAmountField.getText());
            double maxAmount = Double.parseDouble(maxAmountField.getText());
            int gracePeriod = Integer.parseInt(gracePeriodField.getText());

            if (interestRate <= 0 || interestRate > 100) {
                showError("Interest rate must be between 0.01 and 100");
                return false;
            }
            if (loanTerm <= 0) {
                showError("Loan term must be greater than 0");
                return false;
            }
            if (minAmount <= 0) {
                showError("Minimum amount must be greater than 0");
                return false;
            }
            if (maxAmount <= minAmount) {
                showError("Maximum amount must be greater than minimum amount");
                return false;
            }
            if (gracePeriod < 0) {
                showError("Grace period cannot be negative");
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for interest rate, loan term, amounts, and grace period");
            return false;
        }
    }

    private void clearForm() {
        productNameField.setText("");
        interestRateField.setText("");
        calculationMethodComboBox.setSelectedIndex(0);
        loanTermField.setText("");
        minAmountField.setText("");
        maxAmountField.setText("");
        gracePeriodField.setText("");
        installmentTypeComboBox.setSelectedIndex(0);
        loanFeeTypeComboBox.setSelectedIndex(0);
        category1ComboBox.setSelectedIndex(0);
        category2ComboBox.setSelectedIndex(0);
        refinanceComboBox.setSelectedIndex(0);
    }

    private void goBackToLoans() {
        ScreenManager.getInstance().showScreen(new LoansScreen(employeeId, userRole));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}