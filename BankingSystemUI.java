import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class BankingSystemSwingUI {
    private JFrame frame;
    private Map<String, Account> accounts = new HashMap<>();
    private java.util.List<String> scamComplaints = new ArrayList<>();
    private java.util.List<String> otherComplaints = new ArrayList<>();

    public BankingSystemSwingUI() {
        showRoleSelection();
    }

    private void showRoleSelection() {
        frame = new JFrame("BT Bank - Select Role");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 220);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 87, 153));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel label = new JLabel("Welcome to BT Bank");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JButton userBtn = new JButton("User");
        JButton accountantBtn = new JButton("Accountant");
        JButton exitBtn = new JButton("Exit");

        styleMainButton(userBtn);
        styleMainButton(accountantBtn);
        styleMainButton(exitBtn);

        userBtn.addActionListener(e -> {
            frame.dispose();
            showUserMenu();
        });
        accountantBtn.addActionListener(e -> {
            frame.dispose();
            showAccountantMenu();
        });
        exitBtn.addActionListener(e -> System.exit(0));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10,0,25,0);
        panel.add(label, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10,25,10,25);
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(userBtn, gbc);
        gbc.gridx = 1;
        panel.add(accountantBtn, gbc);

        gbc.insets = new Insets(20,25,10,25);
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(exitBtn, gbc);

        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private void showUserMenu() {
        frame = new JFrame("BT Bank - User Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 420);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 248, 255));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 30, 10, 30);

        JLabel header = new JLabel("User Menu");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(new Color(30, 87, 153));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(header, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        JButton createSavingsBtn = new JButton("Create Savings Account");
        JButton createCurrentBtn = new JButton("Create Current Account");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton checkBalanceBtn = new JButton("Check Balance");
        JButton registerComplaintBtn = new JButton("Register Complaint");
        JButton backBtn = new JButton("Back to Role Select");

        styleMenuButton(createSavingsBtn);
        styleMenuButton(createCurrentBtn);
        styleMenuButton(depositBtn);
        styleMenuButton(withdrawBtn);
        styleMenuButton(checkBalanceBtn);
        styleMenuButton(registerComplaintBtn);
        styleMenuButton(backBtn);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createSavingsBtn, gbc);
        gbc.gridx = 1;
        panel.add(createCurrentBtn, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(depositBtn, gbc);
        gbc.gridx = 1;
        panel.add(withdrawBtn, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(checkBalanceBtn, gbc);
        gbc.gridx = 1;
        panel.add(registerComplaintBtn, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(backBtn, gbc);

        createSavingsBtn.addActionListener(e -> showCreateAccountDialog("Savings"));
        createCurrentBtn.addActionListener(e -> showCreateAccountDialog("Current"));
        depositBtn.addActionListener(e -> showDepositDialog());
        withdrawBtn.addActionListener(e -> showWithdrawDialog());
        checkBalanceBtn.addActionListener(e -> showCheckBalanceDialog());
        registerComplaintBtn.addActionListener(e -> showRegisterComplaintDialog());
        backBtn.addActionListener(e -> {
            frame.dispose();
            showRoleSelection();
        });

        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private void showAccountantMenu() {
        frame = new JFrame("BT Bank - Accountant Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 440);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 245, 255));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 30, 10, 30);

        JLabel header = new JLabel("Accountant Menu");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(new Color(10, 50, 120));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(header, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;

        JButton createSavingsBtn = new JButton("Create Savings Account");
        JButton createCurrentBtn = new JButton("Create Current Account");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton checkBalanceBtn = new JButton("Check Balance");
        JButton viewComplaintsBtn = new JButton("View Complaints");
        JButton backBtn = new JButton("Back to Role Select");

        styleMenuButton(createSavingsBtn);
        styleMenuButton(createCurrentBtn);
        styleMenuButton(depositBtn);
        styleMenuButton(withdrawBtn);
        styleMenuButton(checkBalanceBtn);
        styleMenuButton(viewComplaintsBtn);
        styleMenuButton(backBtn);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createSavingsBtn, gbc);
        gbc.gridx = 1;
        panel.add(createCurrentBtn, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(depositBtn, gbc);
        gbc.gridx = 1;
        panel.add(withdrawBtn, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(checkBalanceBtn, gbc);
        gbc.gridx = 1;
        panel.add(viewComplaintsBtn, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(backBtn, gbc);

        createSavingsBtn.addActionListener(e -> showCreateAccountDialog("Savings"));
        createCurrentBtn.addActionListener(e -> showCreateAccountDialog("Current"));
        depositBtn.addActionListener(e -> showDepositDialog());
        withdrawBtn.addActionListener(e -> showWithdrawDialog());
        checkBalanceBtn.addActionListener(e -> showCheckBalanceDialog());
        viewComplaintsBtn.addActionListener(e -> showViewComplaintsDialog());
        backBtn.addActionListener(e -> {
            frame.dispose();
            showRoleSelection();
        });

        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private void showCreateAccountDialog(String type) {
        JTextField nameField = new JTextField();
        JTextField accNumField = new JTextField();
        JTextField balanceField = new JTextField();

        Object[] fields = {
            "Customer Name:", nameField,
            "Account Number:", accNumField,
            "Initial Balance (min 1500):", balanceField
        };

        int result = JOptionPane.showConfirmDialog(frame, fields,
                "Create " + type + " Account", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String accNum = accNumField.getText().trim();
            String balanceStr = balanceField.getText().trim();

            if (name.isEmpty() || accNum.isEmpty() || balanceStr.isEmpty()) {
                showError("All fields are required.");
                return;
            }
            double balance;
            try {
                balance = Double.parseDouble(balanceStr);
            } catch (NumberFormatException e) {
                showError("Balance must be a valid number.");
                return;
            }
            if (balance < 1500) {
                showError("Minimum initial balance is 1500.");
                return;
            }
            if (accounts.containsKey(accNum)) {
                showError("Account number already exists.");
                return;
            }
            Account acc;
            if ("Savings".equals(type)) {
                acc = new SavingsAccount(name, accNum, balance);
            } else {
                acc = new CurrentAccount(name, accNum, balance);
            }
            accounts.put(accNum, acc);
            showInfo(type + " account created successfully for " + name + ".");
        }
    }

    private void showDepositDialog() {
        JTextField accNumField = new JTextField();
        JTextField amountField = new JTextField();

        Object[] fields = {
            "Account Number:", accNumField,
            "Amount to Deposit:", amountField
        };

        int result = JOptionPane.showConfirmDialog(frame, fields,
                "Deposit", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String accNum = accNumField.getText().trim();
            String amountStr = amountField.getText().trim();

            if (accNum.isEmpty() || amountStr.isEmpty()) {
                showError("All fields are required.");
                return;
            }
            Account acc = accounts.get(accNum);
            if (acc == null) {
                showError("Account not found.");
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                showError("Amount must be a valid number.");
                return;
            }
            if (amount <= 0) {
                showError("Amount must be positive.");
                return;
            }
            acc.deposit(amount);
            showInfo(String.format("Successfully deposited %.2f to account %s.", amount, accNum));
        }
    }

    private void showWithdrawDialog() {
        JTextField accNumField = new JTextField();
        JTextField amountField = new JTextField();

        Object[] fields = {
            "Account Number:", accNumField,
            "Amount to Withdraw:", amountField
        };

        int result = JOptionPane.showConfirmDialog(frame, fields,
                "Withdraw", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String accNum = accNumField.getText().trim();
            String amountStr = amountField.getText().trim();

            if (accNum.isEmpty() || amountStr.isEmpty()) {
                showError("All fields are required.");
                return;
            }
            Account acc = accounts.get(accNum);
            if (acc == null) {
                showError("Account not found.");
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                showError("Amount must be a valid number.");
                return;
            }
            if (amount <= 0) {
                showError("Amount must be positive.");
                return;
            }
            boolean success = acc.tryWithdraw(amount);
            if (success) {
                showInfo(String.format("Successfully withdrew %.2f from account %s.", amount, accNum));
            } else {
                showError(acc.getLastError());
            }
        }
    }

    private void showCheckBalanceDialog() {
        String accNum = JOptionPane.showInputDialog(frame,
                "Enter Account Number:", "Check Balance",
                JOptionPane.QUESTION_MESSAGE);
        if (accNum == null) return; // Cancel pressed
        accNum = accNum.trim();
        if (accNum.isEmpty()) {
            showError("Account number required.");
            return;
        }
        Account acc = accounts.get(accNum);
        if (acc == null) {
            showError("Account not found.");
        } else {
            showInfo(String.format("Balance for account %s: %.2f", accNum, acc.balance));
        }
    }

    private void showRegisterComplaintDialog() {
        String[] options = {"Scam Complaint", "Other Complaint"};
        int choice = JOptionPane.showOptionDialog(frame,
                "Select complaint type:", "Register Complaint",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (choice == -1) return; // Cancel pressed

        String complaint = JOptionPane.showInputDialog(frame,
                "Enter complaint details:", "Complaint Details",
                JOptionPane.PLAIN_MESSAGE);
        if (complaint == null) return;
        complaint = complaint.trim();
        if (complaint.isEmpty()) {
            showError("Complaint details cannot be empty.");
            return;
        }
        if (choice == 0) { // Scam
            scamComplaints.add(complaint);
        } else {
            otherComplaints.add(complaint);
        }
        showInfo("Complaint registered successfully.");
    }

    private void showViewComplaintsDialog() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scam Complaints:\n");
        if (scamComplaints.isEmpty()) {
            sb.append(" - None\n");
        } else {
            for (String c : scamComplaints) {
                sb.append(" - ").append(c).append("\n");
            }
        }
        sb.append("\nOther Complaints:\n");
        if (otherComplaints.isEmpty()) {
            sb.append(" - None\n");
        } else {
            for (String c : otherComplaints) {
                sb.append(" - ").append(c).append("\n");
            }
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setBackground(new Color(248, 248, 248));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(frame, scrollPane,
                "View Complaints", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(frame, message,
                "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // Styling for main buttons
    private void styleMainButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(new Color(41, 128, 185));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Styling for menu buttons
    private void styleMenuButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Account classes and logic

    private abstract static class Account {
        String customerName;
        String accountNumber;
        double balance;
        private String lastError = "";

        public Account(String customerName, String accountNumber, double balance) {
            this.customerName = customerName;
            this.accountNumber = accountNumber;
            this.balance = balance;
        }

        public void deposit(double amount) {
            this.balance += amount;
        }

        public boolean tryWithdraw(double amount) {
            if (amount <= 0) {
                lastError = "Withdrawal amount must be positive.";
                return false;
            }
            return withdraw(amount);
        }

        protected abstract boolean withdraw(double amount);

        public String getLastError() {
            return lastError;
        }

        protected void setLastError(String error) {
            this.lastError = error;
        }
    }

    private static class SavingsAccount extends Account {
        public SavingsAccount(String customerName, String accountNumber, double balance) {
            super(customerName, accountNumber, balance);
        }

        @Override
        protected boolean withdraw(double amount) {
            if (balance - amount >= 1000) {
                balance -= amount;
                setLastError("");
                return true;
            } else {
                setLastError("Cannot withdraw. Minimum balance of 1000 must be maintained.");
                return false;
            }
        }
    }

    private static class CurrentAccount extends Account {
        private final double overdraftLimit = 5000;

        public CurrentAccount(String customerName, String accountNumber, double balance) {
            super(customerName, accountNumber, balance);
        }

        @Override
        protected boolean withdraw(double amount) {
            if (balance + overdraftLimit >= amount) {
                balance -= amount;
                setLastError("");
                return true;
            } else {
                setLastError("Exceeded overdraft limit.");
                return false;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankingSystemSwingUI::new);
    }
}

