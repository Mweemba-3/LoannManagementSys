import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class EmployeesScreen extends JPanel {
    private int userId;
    private String userRole;
    private JTable employeesTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    
    public EmployeesScreen(int userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
        initUI();
        loadEmployeesData();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(new Color(240, 242, 245));
        headerPanel.setBorder(new EmptyBorder(10,10,10,10));
        
       
        
        
        JLabel titleLabel = new JLabel("EMPLOYEE MANAGEMENT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel,BorderLayout.NORTH);
        
        // Action Buttons Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setBackground(new Color(240, 242, 245));
        
        addButton = new JButton("Add Employee");
        styleButton(addButton, new Color(46, 125, 50), new Color(39, 105, 42));
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.addActionListener(e -> showAddEditDialog(null));
        actionPanel.add(addButton);
        
        editButton = new JButton("Edit Employee");
        styleButton(editButton, new Color(70, 130, 180), new Color(60, 120, 170));
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editSelectedEmployee());
        actionPanel.add(editButton);
        
        deleteButton = new JButton("Delete Employee");
        styleButton(deleteButton, new Color(220, 53, 69), new Color(200, 35, 51));
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteSelectedEmployee());
        actionPanel.add(deleteButton);
        
        refreshButton = new JButton("Refresh");
        styleButton(refreshButton, new Color(120, 120, 120), new Color(100, 100, 100));
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.addActionListener(e -> loadEmployeesData());
        actionPanel.add(refreshButton);

         // Back button
        backButton = new JButton("← Back to Dashboard");
        styleButton(backButton, new Color(100, 100, 100), new Color(80, 80, 80));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.addActionListener(e -> goBackToDashboard());
        actionPanel.add(backButton, BorderLayout.WEST);
        
        add(actionPanel, BorderLayout.NORTH);
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        String[] columns = {"ID", "Name", "Role", "Created Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        employeesTable = new JTable(tableModel);
        styleTable(employeesTable);
        
        // Add selection listener
        employeesTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = employeesTable.getSelectedRow() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        });
        
        JScrollPane scrollPane = new JScrollPane(employeesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(800, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
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
        
        // Custom renderer for role column
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                String role = value.toString();
                if ("admin".equals(role)) {
                    setForeground(new Color(220, 53, 69));
                    setBackground(new Color(255, 240, 240));
                } else {
                    setForeground(new Color(40, 167, 69));
                    setBackground(new Color(240, 255, 240));
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
    
    private void goBackToDashboard() {
        ScreenManager.getInstance().showScreen(new AdminDashboard(userId, getEmployeeName(userId)));
    }
    
    private String getEmployeeName(int employeeId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT name FROM employees WHERE employee_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException ex) {
            System.err.println("Error getting employee name: " + ex.getMessage());
        }
        return "User";
    }
    
    private void loadEmployeesData() {
        tableModel.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT employee_id, name, role, created_at FROM employees ORDER BY role, name";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("employee_id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    dateFormat.format(rs.getDate("created_at"))
                });
            }
        } catch (SQLException ex) {
            showError("Error loading employees: " + ex.getMessage());
        }
        
        // Disable buttons if no selection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    private void showAddEditDialog(Integer employeeId) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                   employeeId == null ? "Add Employee" : "Edit Employee", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        // Name field
        contentPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        contentPanel.add(nameField);
        
        // Role field
        contentPanel.add(new JLabel("Role:"));
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"employee", "admin"});
        contentPanel.add(roleCombo);
        
        // Password field
        contentPanel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        contentPanel.add(passwordField);
        
        // Confirm password field
        contentPanel.add(new JLabel("Confirm Password:"));
        JPasswordField confirmPasswordField = new JPasswordField();
        contentPanel.add(confirmPasswordField);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = new JButton("Save");
        styleButton(saveButton, new Color(46, 125, 50), new Color(39, 105, 42));
        saveButton.addActionListener(e -> {
            if (validateEmployeeForm(nameField, passwordField, confirmPasswordField)) {
                if (employeeId == null) {
                    addEmployee(nameField.getText(), (String) roleCombo.getSelectedItem(), 
                              new String(passwordField.getPassword()));
                } else {
                    updateEmployee(employeeId, nameField.getText(), (String) roleCombo.getSelectedItem(), 
                                 new String(passwordField.getPassword()));
                }
                dialog.dispose();
                loadEmployeesData();
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(120, 120, 120), new Color(100, 100, 100));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // If editing, load existing data
        if (employeeId != null) {
            loadEmployeeData(employeeId, nameField, roleCombo);
        }
        
        dialog.setVisible(true);
    }
    
    private void loadEmployeeData(int employeeId, JTextField nameField, JComboBox<String> roleCombo) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT name, role FROM employees WHERE employee_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                roleCombo.setSelectedItem(rs.getString("role"));
            }
        } catch (SQLException ex) {
            showError("Error loading employee data: " + ex.getMessage());
        }
    }
    
    private boolean validateEmployeeForm(JTextField nameField, JPasswordField passwordField, 
                                       JPasswordField confirmPasswordField) {
        if (nameField.getText().trim().isEmpty()) {
            showError("Please enter employee name");
            return false;
        }
        
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (password.isEmpty()) {
            showError("Please enter a password");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return false;
        }
        
        if (password.length() < 4) {
            showError("Password must be at least 4 characters long");
            return false;
        }
        
        return true;
    }
    
    private void addEmployee(String name, String role, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO employees (name, password, role) VALUES (?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, password);
            stmt.setString(3, role);
            
            int rowsInserted = stmt.executeUpdate();
            
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Employee added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Log this action
                logActivity(userId, "Add Employee", "Added new employee: " + name);
            }
        } catch (SQLException ex) {
            showError("Error adding employee: " + ex.getMessage());
        }
    }
    
    private void editSelectedEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow != -1) {
            int employeeId = (Integer) tableModel.getValueAt(selectedRow, 0);
            showAddEditDialog(employeeId);
        }
    }
    
    private void updateEmployee(int employeeId, String name, String role, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE employees SET name = ?, role = ?, password = ? WHERE employee_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, role);
            stmt.setString(3, password);
            stmt.setInt(4, employeeId);
            
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Employee updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Log this action
                logActivity(userId, "Update Employee", "Updated employee ID: " + employeeId);
            }
        } catch (SQLException ex) {
            showError("Error updating employee: " + ex.getMessage());
        }
    }
    
    private void deleteSelectedEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int employeeId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String employeeName = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Prevent self-deletion
        if (employeeId == userId) {
            showError("You cannot delete your own account");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete employee: " + employeeName + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM employees WHERE employee_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            
            int rowsDeleted = stmt.executeUpdate();
            
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeesData();
                
                // Log this action
                logActivity(userId, "Delete Employee", "Deleted employee: " + employeeName);
            }
        } catch (SQLException ex) {
            showError("Error deleting employee: " + ex.getMessage());
        }
    }
    
    private void logActivity(int employeeId, String action, String details) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO audit_logs (employee_id, action, details) VALUES (?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            stmt.setString(2, action);
            stmt.setString(3, details);
            
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error logging activity: " + ex.getMessage());
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}