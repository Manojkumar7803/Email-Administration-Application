package com.emailadmin;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EmailAdministrationApp extends JFrame {
    private final EmployeeDAO dao = new EmployeeDAO();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Employee", "Department", "Email", "Alternate Email", "Capacity (MB)"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable employeeTable = new JTable(tableModel);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            if (showLogin()) new EmailAdministrationApp().setVisible(true);
        });
    }

    private static boolean showLogin() {
        JTextField username = new JTextField("admin");
        JPasswordField password = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.add(new JLabel("Admin username"));
        panel.add(username);
        panel.add(new JLabel("Admin password"));
        panel.add(password);
        int choice = JOptionPane.showConfirmDialog(null, panel,
                "Email Administration - Login", JOptionPane.OK_CANCEL_OPTION);
        // Demo login only. Replace with database-backed admin authentication before deployment.
        return choice == JOptionPane.OK_OPTION
                && "admin".equals(username.getText().trim())
                && "admin123".equals(new String(password.getPassword()));
    }

    private EmailAdministrationApp() {
        super("Email Administration System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Create Account", buildCreatePanel());
        tabs.addTab("Manage Employees", buildManagePanel());
        tabs.addTab("SMTP Verification", buildSmtpPanel());
        add(tabs);
        refreshEmployees();
    }

    private JPanel buildCreatePanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));
        form.setBorder(BorderFactory.createTitledBorder("Create employee email account"));

        JTextField name = new JTextField();
        JComboBox<String> department = new JComboBox<>(
                new String[]{"HR", "Development", "Testing", "Finance", "Sales", "Support"});
        JTextField alternate = new JTextField();
        JSpinner capacity = new JSpinner(new SpinnerNumberModel(1024, 100, 100000, 100));

        form.add(new JLabel("Employee name")); form.add(name);
        form.add(new JLabel("Department")); form.add(department);
        form.add(new JLabel("Alternate email")); form.add(alternate);
        form.add(new JLabel("Mailbox capacity (MB)")); form.add(capacity);

        JButton create = new JButton("Generate and Create Account");
        create.addActionListener(e -> {
            String employeeName = name.getText().trim();
            String dept = (String) department.getSelectedItem();
            String alt = alternate.getText().trim();
            int mb = (Integer) capacity.getValue();

            if (employeeName.isEmpty()) {
                showError("Enter the employee name.");
                return;
            }
            if (!alt.isEmpty() && !alt.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                showError("Enter a valid alternate email address.");
                return;
            }

            try {
                String email = EmailGenerator.generate(employeeName, dept);
                String temporaryPassword = PasswordGenerator.generate(14);
                dao.create(employeeName, dept, email, temporaryPassword, alt, mb);
                refreshEmployees();
                JOptionPane.showMessageDialog(this,
                        "Account created successfully!\n\nEmail: " + email
                        + "\nTemporary password: " + temporaryPassword
                        + "\n\nCopy this password now. It is not stored in plain text.",
                        "Account Created", JOptionPane.INFORMATION_MESSAGE);
                name.setText("");
                alternate.setText("");
                capacity.setValue(1024);
            } catch (SQLException ex) {
                showError("Could not create account. The email may already exist.\n"
                        + ex.getMessage());
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1; c.insets = new Insets(15, 15, 10, 15);
        outer.add(form, c);
        c.gridy = 1; c.weightx = 0;
        outer.add(create, c);
        return outer;
    }

    private JPanel buildManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setAutoCreateRowSorter(true);
        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        JButton edit = new JButton("Update Alternate Email / Capacity");
        JButton delete = new JButton("Delete Selected");
        refresh.addActionListener(e -> refreshEmployees());
        edit.addActionListener(e -> updateSelected());
        delete.addActionListener(e -> deleteSelected());
        buttons.add(refresh); buttons.add(edit); buttons.add(delete);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildSmtpPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JTextField host = new JTextField("smtp.gmail.com", 22);
        JTextField port = new JTextField("587", 22);
        JTextField username = new JTextField(22);
        JPasswordField password = new JPasswordField(22);
        JButton verify = new JButton("Verify SMTP Credentials");

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBorder(BorderFactory.createTitledBorder("SMTP connection test"));
        form.add(new JLabel("SMTP host")); form.add(host);
        form.add(new JLabel("SMTP port")); form.add(port);
        form.add(new JLabel("SMTP username")); form.add(username);
        form.add(new JLabel("SMTP app password")); form.add(password);

        verify.addActionListener(e -> {
            try {
                int smtpPort = Integer.parseInt(port.getText().trim());
                SMTPVerifier.verify(host.getText().trim(), smtpPort,
                        username.getText().trim(), new String(password.getPassword()));
                JOptionPane.showMessageDialog(this, "SMTP authentication successful.");
            } catch (Exception ex) {
                showError("SMTP verification failed: " + ex.getMessage());
            } finally {
                password.setText("");
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.insets = new Insets(10, 10, 10, 10);
        panel.add(form, c);
        c.gridy = 1;
        panel.add(verify, c);
        return panel;
    }

    private void refreshEmployees() {
        try {
            List<Employee> employees = dao.findAll();
            tableModel.setRowCount(0);
            for (Employee employee : employees) {
                tableModel.addRow(new Object[]{employee.getId(), employee.getName(),
                        employee.getDepartment(), employee.getEmail(),
                        employee.getAlternateEmail(), employee.getCapacityMb()});
            }
        } catch (SQLException ex) {
            showError("Could not load employees. Check MySQL and database settings.\n"
                    + ex.getMessage());
        }
    }

    private void updateSelected() {
        int viewRow = employeeTable.getSelectedRow();
        if (viewRow < 0) { showError("Select an employee first."); return; }
        int row = employeeTable.convertRowIndexToModel(viewRow);
        int id = (Integer) tableModel.getValueAt(row, 0);
        String currentAlt = (String) tableModel.getValueAt(row, 4);
        int currentCapacity = (Integer) tableModel.getValueAt(row, 5);

        JTextField alternate = new JTextField(currentAlt == null ? "" : currentAlt);
        JSpinner capacity = new JSpinner(
                new SpinnerNumberModel(currentCapacity, 100, 100000, 100));
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Alternate email (leave blank to clear)"));
        form.add(alternate);
        form.add(new JLabel("Mailbox capacity (MB)"));
        form.add(capacity);

        if (JOptionPane.showConfirmDialog(this, form, "Update Employee",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        String alt = alternate.getText().trim();
        if (!alt.isEmpty() && !alt.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showError("Enter a valid alternate email address."); return;
        }
        try {
            dao.updateDetails(id, alt, (Integer) capacity.getValue());
            refreshEmployees();
        } catch (SQLException ex) { showError("Update failed: " + ex.getMessage()); }
    }

    private void deleteSelected() {
        int viewRow = employeeTable.getSelectedRow();
        if (viewRow < 0) { showError("Select an employee first."); return; }
        int row = employeeTable.convertRowIndexToModel(viewRow);
        int id = (Integer) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this employee account record?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try { dao.delete(id); refreshEmployees(); }
            catch (SQLException ex) { showError("Delete failed: " + ex.getMessage()); }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
