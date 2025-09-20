import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class AddClientScreen extends JPanel {
    private int userId;
    private String userRole;
    private Map<String, JComponent> fields = new HashMap<>();
    private JComboBox<String> titleCombo, genderCombo, maritalCombo, employmentCombo, provinceCombo, branchCombo;
    private JButton saveButton, cancelButton;

    public AddClientScreen(int userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("ADD NEW CLIENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Main form with scrolling
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Branch Selection at the TOP
        addSectionTitle(formPanel, "Branch Selection");
        String[] zambianProvinces = {
            "Lusaka", "Copperbelt", "Southern", "Northern", 
            "Eastern", "Western", "Luapula", "North-Western", "Muchinga"
        };
        branchCombo = addComboField(formPanel, "Branch *", "branch", zambianProvinces);

        // Personal Information Section
        addSectionTitle(formPanel, "Personal Information");
        titleCombo = addComboField(formPanel, "Title *", "title", 
            new String[]{"Mr", "Ms", "Mrs", "Dr", "Prof", "Other"});
        addTextField(formPanel, "First Name *", "first_name");
        addTextField(formPanel, "Middle Name", "middle_name");
        addTextField(formPanel, "Last Name *", "last_name");
        addTextField(formPanel, "Date of Birth (YYYY-MM-DD) *", "date_of_birth");
        genderCombo = addComboField(formPanel, "Gender *", "gender", 
            new String[]{"Male", "Female", "Other"});
        maritalCombo = addComboField(formPanel, "Marital Status *", "marital_status", 
            new String[]{"Single", "Married", "Divorced", "Widowed"});

        // Contact Information Section
        addSectionTitle(formPanel, "Contact Information");
        addTextField(formPanel, "Phone Number *", "phone_number");
        addTextField(formPanel, "Email", "email");
        addTextField(formPanel, "Physical Address *", "physical_address");
        provinceCombo = addComboField(formPanel, "Province *", "province", zambianProvinces);
        addTextField(formPanel, "Postal Address", "postal_address");

        // Identification Section
        addSectionTitle(formPanel, "Identification");
        addTextField(formPanel, "ID Type *", "id_type");
        addTextField(formPanel, "ID Number *", "id_number");
        JTextField idPlaceField = addTextFieldWithDefault(formPanel, "ID Place", "id_place", "GRZ");

        // Employment Information Section
        addSectionTitle(formPanel, "Employment Information");
        employmentCombo = addComboField(formPanel, "Employment Status *", "employment_status", 
            new String[]{"Employed", "Self-Employed", "Unemployed"});
        addTextField(formPanel, "Employer Name", "employer_name");
        addTextField(formPanel, "Employee Number", "employee_number");
        addTextField(formPanel, "Job Title", "job_title");
        addTextField(formPanel, "Monthly Income", "monthly_income");

        // Next of Kin Section
        addSectionTitle(formPanel, "Next of Kin");
        addTextField(formPanel, "Next of Kin Name *", "kin_name");
        addComboField(formPanel, "Relationship *", "kin_relationship", 
            new String[]{"Spouse", "Parent", "Sibling", "Other"});
        addTextField(formPanel, "Next of Kin Phone *", "kin_phone");
        addTextField(formPanel, "Next of Kin Address", "kin_address");
        addTextField(formPanel, "Next of Kin ID", "kin_id");

        // Bank Details Section
        addSectionTitle(formPanel, "Bank Details");
        addTextField(formPanel, "Bank Name", "bank_name");
        addTextField(formPanel, "Account Number", "account_number");
        addTextField(formPanel, "Account Name", "account_name");
        addTextField(formPanel, "Branch Code", "branch_code");
        addTextField(formPanel, "Branch Name", "branch_name");

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        saveButton = new JButton("Save Client");
        cancelButton = new JButton("Cancel");

        styleButton(saveButton, new Color(46, 125, 50), new Color(35, 100, 40));
        styleButton(cancelButton, new Color(198, 40, 40), new Color(170, 30, 30));

        saveButton.addActionListener(e -> saveClient());
        cancelButton.addActionListener(e -> goBack());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addSectionTitle(JPanel panel, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(70, 130, 180));
        label.setBorder(BorderFactory.createEmptyBorder(15, 0, 8, 0));
        panel.add(label);
    }

    private void addTextField(JPanel panel, String label, String fieldName) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        
        JLabel labelField = new JLabel(label);
        labelField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fieldPanel.add(labelField, BorderLayout.WEST);
        
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        fields.put(fieldName, textField);
        fieldPanel.add(textField, BorderLayout.CENTER);
        panel.add(fieldPanel);
    }

    private JTextField addTextFieldWithDefault(JPanel panel, String label, String fieldName, String defaultValue) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        
        JLabel labelField = new JLabel(label);
        labelField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fieldPanel.add(labelField, BorderLayout.WEST);
        
        JTextField textField = new JTextField(defaultValue, 20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        fields.put(fieldName, textField);
        fieldPanel.add(textField, BorderLayout.CENTER);
        panel.add(fieldPanel);
        return textField;
    }

    private JComboBox<String> addComboField(JPanel panel, String label, String fieldName, String[] options) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        
        JLabel labelField = new JLabel(label);
        labelField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fieldPanel.add(labelField, BorderLayout.WEST);
        
        JComboBox<String> combo = new JComboBox<>(options);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        fields.put(fieldName, combo);
        fieldPanel.add(combo, BorderLayout.CENTER);
        panel.add(fieldPanel);
        return combo;
    }

    private void styleButton(JButton button, Color bgColor, Color hoverColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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

    private boolean validateFields() {
        // Required fields validation
        String[] requiredFields = {
            "branch", "first_name", "last_name", "phone_number", "physical_address",
            "id_type", "id_number", "kin_name", "kin_phone"
        };
        
        for (String field : requiredFields) {
            JComponent component = fields.get(field);
            String value = "";
            
            if (component instanceof JTextField) {
                value = ((JTextField) component).getText().trim();
            } else if (component instanceof JComboBox) {
                value = (String) ((JComboBox<?>) component).getSelectedItem();
            }
            
            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all required fields (marked with *)", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                component.requestFocus();
                return false;
            }
        }
        
        // Phone number validation
        String phone = ((JTextField) fields.get("phone_number")).getText().trim();
        if (!phone.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid phone number (10-15 digits)", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            fields.get("phone_number").requestFocus();
            return false;
        }
        
        // ID number validation
        String idNumber = ((JTextField) fields.get("id_number")).getText().trim();
        if (idNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "ID Number is required", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            fields.get("id_number").requestFocus();
            return false;
        }
        
        // Date validation
        String dob = ((JTextField) fields.get("date_of_birth")).getText().trim();
        if (!dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, 
                "Please enter date in YYYY-MM-DD format", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            fields.get("date_of_birth").requestFocus();
            return false;
        }
        
        return true;
    }

    private void saveClient() {
        if (!validateFields()) {
            return;
        }
        
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Connection conn = null;
                try {
                    conn = DatabaseConnection.getConnection();
                    conn.setAutoCommit(false);
                    
                    // Insert into clients table
                    int clientId = insertClient(conn);
                    if (clientId == -1) {
                        conn.rollback();
                        return false;
                    }
                    
                    // Insert next of kin
                    if (!insertNextOfKin(conn, clientId)) {
                        conn.rollback();
                        return false;
                    }
                    
                    // Insert bank details
                    insertBankDetails(conn, clientId);
                    
                    // Log the activity
                    logActivity(conn, "Client Added", "Added new client: " + 
                        ((JTextField) fields.get("first_name")).getText() + " " + 
                        ((JTextField) fields.get("last_name")).getText());
                    
                    conn.commit();
                    return true;
                    
                } catch (SQLException e) {
                    if (conn != null) {
                        conn.rollback();
                    }
                    throw e;
                } finally {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                    }
                }
            }
            
            private int insertClient(Connection conn) throws SQLException {
                String sql = "INSERT INTO clients (" +
                    "branch, title, first_name, middle_name, last_name, " +
                    "date_of_birth, phone_number, email, physical_address, " +
                    "province, postal_address, id_type, id_number, id_place, " +
                    "employer_name, employee_number, job_title, monthly_income, " +
                    "employment_status, gender, marital_status" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    // Get selected branch from dropdown
                    stmt.setString(1, (String) branchCombo.getSelectedItem());
                    stmt.setString(2, (String) titleCombo.getSelectedItem());
                    stmt.setString(3, ((JTextField) fields.get("first_name")).getText());
                    stmt.setString(4, ((JTextField) fields.get("middle_name")).getText());
                    stmt.setString(5, ((JTextField) fields.get("last_name")).getText());
                    stmt.setDate(6, Date.valueOf(((JTextField) fields.get("date_of_birth")).getText()));
                    stmt.setString(7, ((JTextField) fields.get("phone_number")).getText());
                    stmt.setString(8, ((JTextField) fields.get("email")).getText());
                    stmt.setString(9, ((JTextField) fields.get("physical_address")).getText());
                    stmt.setString(10, (String) provinceCombo.getSelectedItem());
                    stmt.setString(11, ((JTextField) fields.get("postal_address")).getText());
                    stmt.setString(12, ((JTextField) fields.get("id_type")).getText());
                    stmt.setString(13, ((JTextField) fields.get("id_number")).getText());
                    
                    // Set ID Place - use value from field or default to "GRZ"
                    String idPlace = ((JTextField) fields.get("id_place")).getText().trim();
                    stmt.setString(14, idPlace.isEmpty() ? "GRZ" : idPlace);
                    
                    stmt.setString(15, ((JTextField) fields.get("employer_name")).getText());
                    stmt.setString(16, ((JTextField) fields.get("employee_number")).getText());
                    stmt.setString(17, ((JTextField) fields.get("job_title")).getText());
                    
                    String monthlyIncome = ((JTextField) fields.get("monthly_income")).getText();
                    stmt.setBigDecimal(18, monthlyIncome.isEmpty() ? null : new java.math.BigDecimal(monthlyIncome));
                    
                    stmt.setString(19, (String) employmentCombo.getSelectedItem());
                    stmt.setString(20, (String) genderCombo.getSelectedItem());
                    stmt.setString(21, (String) maritalCombo.getSelectedItem());
                    
                    int affected = stmt.executeUpdate();
                    if (affected > 0) {
                        ResultSet rs = stmt.getGeneratedKeys();
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    }
                    return -1;
                }
            }
            
            private boolean insertNextOfKin(Connection conn, int clientId) throws SQLException {
                String kinName = ((JTextField) fields.get("kin_name")).getText();
                if (kinName.isEmpty()) {
                    return true; // No next of kin to insert
                }
                
                String sql = "INSERT INTO next_of_kin (" +
                    "client_id, name, relationship, phone_number, physical_address, id_number" +
                    ") VALUES (?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, clientId);
                    stmt.setString(2, kinName);
                    stmt.setString(3, (String) ((JComboBox<?>) fields.get("kin_relationship")).getSelectedItem());
                    stmt.setString(4, ((JTextField) fields.get("kin_phone")).getText());
                    stmt.setString(5, ((JTextField) fields.get("kin_address")).getText());
                    stmt.setString(6, ((JTextField) fields.get("kin_id")).getText());
                    
                    return stmt.executeUpdate() > 0;
                }
            }
            
            private boolean insertBankDetails(Connection conn, int clientId) throws SQLException {
                String bankName = ((JTextField) fields.get("bank_name")).getText();
                if (bankName.isEmpty()) {
                    return true; // No bank details to insert
                }
                
                String sql = "INSERT INTO client_bank_details (" +
                    "client_id, bank_name, account_number, account_name, branch_code, branch_name" +
                    ") VALUES (?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, clientId);
                    stmt.setString(2, bankName);
                    stmt.setString(3, ((JTextField) fields.get("account_number")).getText());
                    stmt.setString(4, ((JTextField) fields.get("account_name")).getText());
                    stmt.setString(5, ((JTextField) fields.get("branch_code")).getText());
                    stmt.setString(6, ((JTextField) fields.get("branch_name")).getText());
                    
                    return stmt.executeUpdate() > 0;
                }
            }
            
            private void logActivity(Connection conn, String action, String details) throws SQLException {
                String sql = "INSERT INTO audit_logs (employee_id, action, details) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, action);
                    stmt.setString(3, details);
                    stmt.executeUpdate();
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(AddClientScreen.this,
                            "Client added successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        goBack();
                    } else {
                        JOptionPane.showMessageDialog(AddClientScreen.this,
                            "Failed to add client. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AddClientScreen.this,
                        "Error adding client: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    saveButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void goBack() {
        ScreenManager.getInstance().showScreen(new ClientsScreen(userId, userRole));
    }
}