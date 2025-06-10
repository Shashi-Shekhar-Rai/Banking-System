
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


import org.mindrot.jbcrypt.BCrypt;


public class BankingSystemGUI {

    private static final Logger logger = Logger.getLogger(BankingSystemGUI.class.getName());

    
    private static final Font FONT_PRIMARY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_TITLE = new Font("Segoe UI Semibold", Font.BOLD, 32);
    private static final Color COLOR_BUTTON_BG = new Color(245, 245, 245);
    private static final Color COLOR_BUTTON_HOVER_BG = new Color(225, 225, 225);
    private static final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    private static final Color COLOR_TEXT_DARK = Color.BLACK;

    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private final AccountDAO accountDAO;
    private Account loggedInAccount = null;

    
    private JTextField savingsCustName, savingsAddress, savingsContact, savingsGovId, savingsAge, savingsBalance;
    private JPasswordField savingsPasswordField, savingsConfirmPasswordField;
    private JLabel savingsPhotoPreview;
    private byte[] savingsProfilePicBytes;

    private JTextField currentCustName, currentAddress, currentContact, currentGovId, currentAge, currentBalance;
    private JPasswordField currentPasswordField, currentConfirmPasswordField;
    private JLabel currentPhotoPreview;
    private byte[] currentProfilePicBytes;

    private JTextField loginAccNumField;
    private JPasswordField loginPasswordField;

    private JPasswordField oldPasswordField, newPasswordField, confirmNewPasswordField;

    private JTextField depositAmountField;
    private JTextField withdrawAmountField;
    private JTextArea balanceResultArea;
    private JLabel balancePhotoLabel;

    private JTextField toAccField, transferAmountField;
    private JTextArea complaintArea;
    private ButtonGroup complaintTypeGroup;
    private JTextArea historyArea;

    private JLabel dashboardNameLabel, dashboardAccNumLabel, dashboardBalanceLabel;
    private JLabel dashboardPhotoLabel;


    public static void main(String[] args) {
        setupLogger();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new BankingSystemGUI().createAndShowGUI();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Application startup failed.", e);
                JOptionPane.showMessageDialog(null, "A critical error occurred during startup.", "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("banking_system.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not set up logger file handler.", e);
        }
    }

    public BankingSystemGUI() {
        this.accountDAO = new AccountDAOImpl();
    }

    public void createAndShowGUI() {
        mainFrame = new JFrame("BT Bank System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(950, 800);
        mainFrame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        BackgroundPanel wrapperPanel = new BackgroundPanel("background_img.png");
        wrapperPanel.setLayout(new GridBagLayout());
        wrapperPanel.add(cardPanel, new GridBagConstraints());
        mainFrame.setContentPane(wrapperPanel);

        createAllScreens();
        createMenuBar();

        mainFrame.setVisible(true);
        logger.info("GUI Initialized and Visible.");
    }

    private void createAllScreens() {
        createMainMenu();
        createLoginScreen();
        createDashboardScreen();
        createAccountantMenu();
        createAccountCreationScreen("Savings");
        createAccountCreationScreen("Current");
        createDepositScreen();
        createWithdrawScreen();
        createBalanceScreen();
        createFundTransferScreen();
        createChangePasswordScreen();
        createComplaintScreen();
        createViewComplaintsScreen();
        createTransactionHistoryScreen();
        createViewAllAccountsScreen();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu themeMenu = new JMenu("Theme");

        JMenuItem metalTheme = new JMenuItem("Metal");
        metalTheme.addActionListener(e -> switchTheme("javax.swing.plaf.metal.MetalLookAndFeel"));

        JMenuItem nimbusTheme = new JMenuItem("Nimbus");
        nimbusTheme.addActionListener(e -> switchTheme("javax.swing.plaf.nimbus.NimbusLookAndFeel"));

        JMenuItem systemTheme = new JMenuItem("System Default");
        systemTheme.addActionListener(e -> switchTheme(UIManager.getSystemLookAndFeelClassName()));

        themeMenu.add(metalTheme);
        themeMenu.add(nimbusTheme);
        themeMenu.add(systemTheme);
        menuBar.add(themeMenu);
        mainFrame.setJMenuBar(menuBar);
    }

    private void switchTheme(String themeClassName) {
        try {
            UIManager.setLookAndFeel(themeClassName);
            SwingUtilities.updateComponentTreeUI(mainFrame);
            logger.info("Switched theme to: " + themeClassName);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Failed to switch theme.", ex);
            showErrorDialog("Theme Error", "Could not apply the selected theme.");
        }
    }

    

    private JPanel createPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        return panel;
    }

    private void addFormRow(JPanel panel, JLabel label, Component component, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 10);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 5);
        panel.add(component, gbc);

        gbc.gridy++;
    }

    private void createMainMenu() {
        JPanel panel = createPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel title = new JLabel("Welcome to BT Bank");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_TEXT_DARK);
        panel.add(title, gbc);

        gbc.insets = new Insets(15, 0, 15, 0);
        JButton userBtn = createButton("👤 Customer Login", "LoginScreen");
        panel.add(userBtn, gbc);

        JButton createAccBtn = createButton("➕ Open a New Account");
        createAccBtn.addActionListener(e -> {
            Object[] options = {"Savings Account", "Current Account"};
            int choice = JOptionPane.showOptionDialog(mainFrame, "Which type of account would you like to create?",
                    "Select Account Type", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);
            if (choice == 0) {
                cardLayout.show(cardPanel, "CreateSavingsAccount");
            } else if (choice == 1) {
                cardLayout.show(cardPanel, "CreateCurrentAccount");
            }
        });
        panel.add(createAccBtn, gbc);

        JButton accountantBtn = createButton("💼 Accountant Login");
        accountantBtn.addActionListener(this::handleAccountantLogin);
        panel.add(accountantBtn, gbc);

        JButton exitBtn = createButton("❌ Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        panel.add(exitBtn, gbc);

        cardPanel.add(panel, "MainMenu");
    }

    private void createLoginScreen() {
        JPanel formPanel = createFormPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = createLabel("Customer Login");
        title.setFont(FONT_TITLE);
        formPanel.add(title, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        loginAccNumField = createTextField();
        loginPasswordField = new JPasswordField(20);
        loginPasswordField.setMaximumSize(new Dimension(300, 30));
        loginPasswordField.setFont(FONT_PRIMARY);

        addFormRow(formPanel, createLabel("Account Number:"), loginAccNumField, gbc);
        addFormRow(formPanel, createLabel("Password:"), loginPasswordField, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        addForgotAccountButton(formPanel, true, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 10, 0);

        JButton loginBtn = createButton("🔑 Login");
        loginBtn.addActionListener(e -> handleLogin());
        formPanel.add(loginBtn, gbc);
        gbc.gridy++;

        JButton backBtn = createButton("⬅️ Back to Main Menu");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));
        formPanel.add(backBtn, gbc);

        cardPanel.add(formPanel, "LoginScreen");
    }

    private void createDashboardScreen() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        headerPanel.setOpaque(false);
        dashboardPhotoLabel = new JLabel();
        dashboardPhotoLabel.setPreferredSize(new Dimension(100, 100));
        dashboardPhotoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        headerPanel.add(dashboardPhotoLabel);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        dashboardNameLabel = new JLabel("Welcome, User!");
        dashboardNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dashboardNameLabel.setForeground(COLOR_TEXT_DARK);
        dashboardAccNumLabel = new JLabel("Account: ###########");
        dashboardAccNumLabel.setFont(FONT_PRIMARY);
        dashboardAccNumLabel.setForeground(COLOR_TEXT_DARK);
        dashboardBalanceLabel = new JLabel("Balance: ₹ 0.00");
        dashboardBalanceLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 18));
        dashboardBalanceLabel.setForeground(COLOR_TEXT_DARK);

        infoPanel.add(dashboardNameLabel);
        infoPanel.add(dashboardAccNumLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(dashboardBalanceLabel);
        headerPanel.add(infoPanel);
        panel.add(headerPanel, BorderLayout.NORTH);

       
        JPanel menuGrid = new JPanel(new GridLayout(0, 2, 20, 20));
        menuGrid.setOpaque(false);
        menuGrid.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        menuGrid.add(createDashboardButton("💰 Deposit", "Deposit"));
        menuGrid.add(createDashboardButton("💸 Withdraw", "Withdraw"));
        menuGrid.add(createDashboardButton("📊 View Balance", "Balance"));
        menuGrid.add(createDashboardButton("🔁 Fund Transfer", "FundTransfer"));
        menuGrid.add(createDashboardButton("📜 Transaction History", "TransactionHistory"));
        menuGrid.add(createDashboardButton("🔐 Change Password", "ChangePassword"));
        menuGrid.add(createDashboardButton("✍️ Register Complaint", "Complaint"));
        menuGrid.add(createDashboardButton("🚪 Logout", "MainMenu"));

        panel.add(menuGrid, BorderLayout.CENTER);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateDashboard();
            }
        });

        cardPanel.add(panel, "Dashboard");
    }

    private void createAccountantMenu() {
        JPanel panel = createPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel title = new JLabel("Accountant Menu");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_TEXT_DARK);
        panel.add(title, gbc);

        String[] buttonLabels = {
                "👥 View All Accounts", "🗒️ View Complaints", "🚪 Logout"
        };
        String[] cardNames = {
                "ViewAllAccounts", "ViewComplaints", "MainMenu"
        };

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = createButton(buttonLabels[i]);
            final String cardName = cardNames[i];
            button.addActionListener(e -> {
                if (cardName.equals("MainMenu")) {
                    handleLogout();
                } else {
                    cardLayout.show(cardPanel, cardName);
                }
            });
            panel.add(button, gbc);
        }

        cardPanel.add(panel, "AccountantMenu");
    }

    private void createViewAllAccountsScreen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = createLabel("All Customer Accounts");
        title.setFont(FONT_TITLE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search by Account No:");
        searchLabel.setFont(FONT_BOLD);
        searchLabel.setForeground(COLOR_TEXT_DARK);
        searchPanel.add(searchLabel);

        JTextField searchField = new JTextField(20);
        searchField.setFont(FONT_PRIMARY);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(FONT_BOLD);
        searchPanel.add(searchBtn);
        JButton clearBtn = new JButton("Show All");
        clearBtn.setFont(FONT_BOLD);
        searchPanel.add(clearBtn);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Acc. Number", "Customer Name", "Type", "Balance", "Address", "Contact No.", "Govt ID", "Age"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setFont(FONT_PRIMARY);
        table.getTableHeader().setFont(FONT_BOLD);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int viewRow = table.getSelectedRow();
                    if (viewRow >= 0) {
                        int modelRow = table.convertRowIndexToModel(viewRow);
                        String accNum = (String) tableModel.getValueAt(modelRow, 0);
                        showAccountDetailsDialog(accNum);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String text = searchField.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0));
            }
        });
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton refreshBtn = createButton("🔄 Refresh Data");
        refreshBtn.addActionListener(e -> populateAllAccountsTable(tableModel));
        buttonPanel.add(refreshBtn);

        JButton backBtn = createButton("⬅️ Back");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "AccountantMenu"));
        buttonPanel.add(backBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                populateAllAccountsTable(tableModel);
            }
        });

        cardPanel.add(panel, "ViewAllAccounts");
    }

    private void showAccountDetailsDialog(String accNum) {
        Account acc = accountDAO.findAccountByNumber(accNum);
        if (acc == null) {
            showErrorDialog("Error", "Could not find details for account: " + accNum);
            return;
        }

        JDialog dialog = new JDialog(mainFrame, "Account Details - " + acc.customerName, true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel photoPanel = new JPanel();
        photoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(150, 150));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (acc.profilePicture != null) {
            ImageIcon icon = new ImageIcon(acc.profilePicture);
            Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            photoLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            photoLabel.setText("No Photo");
        }
        photoPanel.add(photoLabel);
        dialog.add(photoPanel, BorderLayout.WEST);

        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        detailsPanel.add(createLabel("Account Number:"));
        detailsPanel.add(createLabel(acc.accountNumber, false));
        detailsPanel.add(createLabel("Customer Name:"));
        detailsPanel.add(createLabel(acc.customerName, false));
        detailsPanel.add(createLabel("Account Type:"));
        detailsPanel.add(createLabel(acc.accountType, false));
        detailsPanel.add(createLabel("Balance:"));
        detailsPanel.add(createLabel(String.format("₹ %.2f", acc.balance), false));
        detailsPanel.add(createLabel("Address:"));
        detailsPanel.add(createLabel("<html>" + acc.address.replaceAll("\n", "<br>") + "</html>", false));
        detailsPanel.add(createLabel("Contact Number:"));
        detailsPanel.add(createLabel(acc.contactNumber, false));
        detailsPanel.add(createLabel("Government ID:"));
        detailsPanel.add(createLabel(acc.govtID, false));
        detailsPanel.add(createLabel("Age:"));
        detailsPanel.add(createLabel(String.valueOf(acc.age), false));

        dialog.add(detailsPanel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(FONT_BOLD);
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void createAccountCreationScreen(String accountType) {
        JPanel formPanel = createFormPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = createLabel("Create " + accountType + " Account");
        title.setFont(FONT_TITLE);
        formPanel.add(title, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        String[] labels = {"Customer Name:", "Address:", "Contact Number:", "Government ID:", "Age:", "Initial Balance (min 1500):", "Set Password:", "Confirm Password:"};
        Component[] components = new Component[labels.length];

        for (int i = 0; i < labels.length; i++) {
            if (i < labels.length - 2) {
                components[i] = createTextField();
            } else {
                components[i] = new JPasswordField(20);
                ((JPasswordField)components[i]).setMaximumSize(new Dimension(300,30));
                ((JPasswordField)components[i]).setAlignmentX(Component.CENTER_ALIGNMENT);
            }
            addFormRow(formPanel, createLabel(labels[i]), components[i], gbc);
        }

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel photoPreviewLabel = new JLabel("No Photo Uploaded");
        photoPreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoPreviewLabel.setPreferredSize(new Dimension(100, 100));
        addFormRow(formPanel, createLabel("Profile Photo (Optional):"), photoPreviewLabel, gbc);

        JButton uploadBtn = createButton("Upload Photo");
        uploadBtn.addActionListener(e -> handlePhotoUpload(accountType));
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 20, 5);
        formPanel.add(uploadBtn, gbc);
        gbc.gridy++;

        if ("Savings".equals(accountType)) {
            savingsCustName = (JTextField)components[0]; savingsAddress = (JTextField)components[1]; savingsContact = (JTextField)components[2];
            savingsGovId = (JTextField)components[3]; savingsAge = (JTextField)components[4]; savingsBalance = (JTextField)components[5];
            savingsPasswordField = (JPasswordField)components[6]; savingsConfirmPasswordField = (JPasswordField)components[7];
            savingsPhotoPreview = photoPreviewLabel;
        } else {
            currentCustName = (JTextField)components[0]; currentAddress = (JTextField)components[1]; currentContact = (JTextField)components[2];
            currentGovId = (JTextField)components[3]; currentAge = (JTextField)components[4]; currentBalance = (JTextField)components[5];
            currentPasswordField = (JPasswordField)components[6]; currentConfirmPasswordField = (JPasswordField)components[7];
            currentPhotoPreview = photoPreviewLabel;
        }

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        JButton createBtn = createButton("➕ Create Account");
        createBtn.addActionListener(e -> handleAccountCreation(accountType));
        formPanel.add(createBtn, gbc);
        gbc.gridy++;

        JButton backBtn = createButton("⬅️ Back to Main Menu");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "MainMenu"));
        formPanel.add(backBtn, gbc);

        cardPanel.add(formPanel, "Create" + accountType + "Account");
    }

    private void addForgotAccountButton(JPanel panel, boolean forPassword, GridBagConstraints gbc) {
        String text = forPassword ? "Forgot Password?" : "Forgot Account Number?";
        JButton forgotBtn = new JButton(text);
        forgotBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotBtn.setForeground(Color.BLUE.darker());
        forgotBtn.setBorderPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (forPassword) {
            forgotBtn.addActionListener(e -> handlePasswordRecovery());
        } else {
            forgotBtn.addActionListener(e -> handleAccountNumberRecovery());
        }
        panel.add(forgotBtn, gbc);
    }

    private void createDepositScreen() {
        JPanel formPanel = createFormPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 30, 0);

        JLabel title = createLabel("Deposit Funds");
        title.setFont(FONT_TITLE);
        formPanel.add(title, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        depositAmountField = createTextField();
        addFormRow(formPanel, createLabel("Amount to Deposit:"), depositAmountField, gbc);

        gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 10, 0);

        JButton depositBtn = createButton("💰 Deposit");
        depositBtn.addActionListener(e -> handleDeposit());
        formPanel.add(depositBtn, gbc);
        gbc.gridy++;

        JButton backBtn = createButton("⬅️ Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));
        formPanel.add(backBtn, gbc);

        cardPanel.add(formPanel, "Deposit");
    }

    private void createWithdrawScreen() {
        JPanel formPanel = createFormPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 30, 0);

        JLabel title = createLabel("Withdraw Funds");
        title.setFont(FONT_TITLE);
        formPanel.add(title, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        withdrawAmountField = createTextField();
        addFormRow(formPanel, createLabel("Amount to Withdraw:"), withdrawAmountField, gbc);

        gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 10, 0);

        JButton withdrawBtn = createButton("💸 Withdraw");
        withdrawBtn.addActionListener(e -> handleWithdraw());
        formPanel.add(withdrawBtn, gbc);
        gbc.gridy++;

        JButton backBtn = createButton("⬅️ Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));
        formPanel.add(backBtn, gbc);

        cardPanel.add(formPanel, "Withdraw");
    }

    private void createBalanceScreen() {
        JPanel panel = createPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel title = createLabel("Account Summary");
        title.setFont(FONT_TITLE);
        panel.add(title, gbc);

        JPanel detailsPanel = new JPanel(new BorderLayout(15, 0));
        detailsPanel.setOpaque(false);
        detailsPanel.setMaximumSize(new Dimension(500, 200));

        balancePhotoLabel = new JLabel();
        balancePhotoLabel.setPreferredSize(new Dimension(150, 150));
        balancePhotoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        balancePhotoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailsPanel.add(balancePhotoLabel, BorderLayout.WEST);

        balanceResultArea = createTextArea();
        JScrollPane scrollPane = new JScrollPane(balanceResultArea);
        detailsPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(detailsPanel, gbc);

        JButton backBtn = createButton("⬅️ Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));
        panel.add(backBtn, gbc);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                handleCheckBalance();
            }
        });

        cardPanel.add(panel, "Balance");
    }

    private void createFundTransferScreen() {
        JPanel formPanel = createFormPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 30, 0);

        JLabel title = createLabel("Fund Transfer");
        title.setFont(FONT_TITLE);
        formPanel.add(title, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        toAccField = createTextField();
        transferAmountField = createTextField();

        addFormRow(formPanel, createLabel("Recipient's Account Number:"), toAccField, gbc);
        addFormRow(formPanel, createLabel("Amount to Transfer:"), transferAmountField, gbc);

        gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 10, 0);

        JButton transferBtn = createButton("🔁 Transfer Funds");
        transferBtn.addActionListener(e -> handleFundTransfer());
        formPanel.add(transferBtn, gbc);
        gbc.gridy++;

        JButton backBtn = createButton("⬅️ Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));
        formPanel.add(backBtn, gbc);

        cardPanel.add(formPanel, "FundTransfer");
    }

    private void createChangePasswordScreen() {
        JPanel formPanel = createFormPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 30, 0);

        JLabel title = createLabel("Change Password");
        title.setFont(FONT_TITLE);
        formPanel.add(title, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        oldPasswordField = new JPasswordField(20);
        oldPasswordField.setMaximumSize(new Dimension(300, 30));
        newPasswordField = new JPasswordField(20);
        newPasswordField.setMaximumSize(new Dimension(300, 30));
        confirmNewPasswordField = new JPasswordField(20);
        confirmNewPasswordField.setMaximumSize(new Dimension(300, 30));

        addFormRow(formPanel, createLabel("Old Password:"), oldPasswordField, gbc);
        addFormRow(formPanel, createLabel("New Password:"), newPasswordField, gbc);
        addFormRow(formPanel, createLabel("Confirm New Password:"), confirmNewPasswordField, gbc);

        gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 10, 0);

        JButton changeBtn = createButton("🔐 Change Password");
        changeBtn.addActionListener(e -> handleChangePassword());
        formPanel.add(changeBtn, gbc);
        gbc.gridy++;

        JButton backBtn = createButton("⬅️ Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));
        formPanel.add(backBtn, gbc);

        cardPanel.add(formPanel, "ChangePassword");
    }

    private void createComplaintScreen() {
        JPanel panel = createPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel title = createLabel("Register Complaint");
        title.setFont(FONT_TITLE);
        panel.add(title, gbc);

        complaintTypeGroup = new ButtonGroup();
        JRadioButton scamRadio = new JRadioButton("Scam Complaint");
        JRadioButton otherRadio = new JRadioButton("Other Complaint");
        scamRadio.setFont(FONT_PRIMARY);
        scamRadio.setForeground(COLOR_TEXT_DARK);
        scamRadio.setOpaque(false);
        otherRadio.setFont(FONT_PRIMARY);
        otherRadio.setForeground(COLOR_TEXT_DARK);
        otherRadio.setOpaque(false);
        complaintTypeGroup.add(scamRadio);
        complaintTypeGroup.add(otherRadio);

        JPanel radioPanel = new JPanel();
        radioPanel.setOpaque(false);
        radioPanel.add(scamRadio);
        radioPanel.add(otherRadio);
        panel.add(radioPanel, gbc);

        panel.add(createLabel("Complaint Details:"), gbc);
        complaintArea = new JTextArea(5, 30);
        complaintArea.setFont(FONT_PRIMARY);
        JScrollPane scrollPane = new JScrollPane(complaintArea);
        scrollPane.setMaximumSize(new Dimension(320, 100));
        panel.add(scrollPane, gbc);

        JButton registerBtn = createButton("✍️ Register Complaint");
        registerBtn.addActionListener(e -> handleComplaintRegistration(scamRadio.isSelected()));
        panel.add(registerBtn, gbc);

        JButton backBtn = createButton("⬅️ Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));
        panel.add(backBtn, gbc);

        cardPanel.add(panel, "Complaint");
    }

    private void createViewComplaintsScreen() {
        JPanel panel = createPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel title = createLabel("View Complaints");
        title.setFont(FONT_TITLE);
        panel.add(title, gbc);

        JTextArea scamArea = createTextArea();
        JTextArea otherArea = createTextArea();

        panel.add(createLabel("Scam Complaints:"), gbc);
        panel.add(new JScrollPane(scamArea), gbc);

        panel.add(createLabel("Other Complaints:"), gbc);
        panel.add(new JScrollPane(otherArea), gbc);

        JButton refreshBtn = createButton("🔄 Refresh Complaints");
        refreshBtn.addActionListener(e -> handleViewComplaints(scamArea, otherArea));
        panel.add(refreshBtn, gbc);

        panel.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent evt) {
                handleViewComplaints(scamArea, otherArea);
            }
        });

        JButton backBtn = createButton("⬅️ Back");
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "AccountantMenu"));
        panel.add(backBtn, gbc);

        cardPanel.add(panel, "ViewComplaints");
    }

    private void createTransactionHistoryScreen() {
        JPanel panel = createPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel title = createLabel("Transaction History");
        title.setFont(FONT_TITLE);
        panel.add(title, gbc);

        JButton exportBtn = createButton("📄 Export to PDF");
        panel.add(exportBtn, gbc);

        historyArea = createTextArea();
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setMaximumSize(new Dimension(600, 400));
        panel.add(historyScroll, gbc);

        exportBtn.addActionListener(e -> handleExportToPdf());

        JButton backBtn = createButton("⬅️ Back to Dashboard");
        panel.add(backBtn, gbc);
        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                handleViewHistory();
            }
        });

        cardPanel.add(panel, "TransactionHistory");
    }

    // --- Event Handling Logic ---

    private void updateDashboard() {
        if (loggedInAccount == null) return;
        Account refreshedAccount = accountDAO.findAccountByNumber(loggedInAccount.accountNumber);
        if (refreshedAccount != null) {
            loggedInAccount = refreshedAccount;
        }

        dashboardNameLabel.setText("Welcome, " + loggedInAccount.customerName);
        dashboardAccNumLabel.setText("Account: " + loggedInAccount.accountNumber);
        dashboardBalanceLabel.setText(String.format("Balance: ₹ %.2f", loggedInAccount.balance));

        if (loggedInAccount.profilePicture != null) {
            ImageIcon icon = new ImageIcon(loggedInAccount.profilePicture);
            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            dashboardPhotoLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            dashboardPhotoLabel.setIcon(null);
            dashboardPhotoLabel.setText("No Photo");
        }
    }

    private String generateUniqueAccountNumber() {
        String newAccNum;
        do {
            long timestamp = System.currentTimeMillis() % 10000000;
            int randomSuffix = new Random().nextInt(900) + 100;
            newAccNum = "BT" + timestamp + randomSuffix;
        } while (accountDAO.accountExists(newAccNum));
        return newAccNum;
    }

    private void populateAllAccountsTable(DefaultTableModel tableModel) {
        new SwingWorker<List<Account>, Void>() {
            @Override
            protected List<Account> doInBackground() throws Exception {
                return accountDAO.findAllAsList();
            }

            @Override
            protected void done() {
                try {
                    List<Account> allAccounts = get();
                    tableModel.setRowCount(0);
                    for (Account acc : allAccounts) {
                        Object[] row = {
                                acc.accountNumber, acc.customerName, acc.accountType,
                                String.format("%.2f", acc.balance), acc.address, acc.contactNumber,
                                acc.govtID, acc.age
                        };
                        tableModel.addRow(row);
                    }
                    logger.info("Displayed all customer accounts in the table.");
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Failed to populate all accounts table", ex);
                    showErrorDialog("Data Error", "Could not load and display account data.");
                }
            }
        }.execute();
    }

    private void handleAccountantLogin(ActionEvent e) {
        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(mainFrame, pf, "Enter Accountant Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            String password = new String(pf.getPassword());
            if ("BugTards321".equals(password)) {
                cardLayout.show(cardPanel, "AccountantMenu");
                logger.info("Accountant login successful.");
            } else {
                showErrorDialog("Access Denied", "Incorrect password.");
                logger.warning("Failed accountant login attempt.");
            }
        }
    }

    private void handleLogin() {
        String accNum = loginAccNumField.getText();
        char[] password = loginPasswordField.getPassword();

        if (accNum.trim().isEmpty() || password.length == 0) {
            showErrorDialog("Login Failed", "Account number and password cannot be empty.");
            return;
        }

        new SwingWorker<Account, Void>() {
            @Override
            protected Account doInBackground() throws Exception {
                return accountDAO.findAndAuthenticate(accNum, new String(password));
            }

            @Override
            protected void done() {
                try {
                    loggedInAccount = get();
                    if (loggedInAccount != null) {
                        logger.info("User logged in successfully: " + loggedInAccount.accountNumber);
                        cardLayout.show(cardPanel, "Dashboard");
                        loginAccNumField.setText("");
                        loginPasswordField.setText("");
                    } else {
                        showErrorDialog("Login Failed", "Invalid account number or password.");
                        logger.warning("Failed login attempt for account: " + accNum);
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Login error.", ex.getCause());
                    showErrorDialog("Error", "An unexpected error occurred during login.");
                }
            }
        }.execute();
    }

    private void handleLogout() {
        loggedInAccount = null;
        logger.info("User logged out.");
        cardLayout.show(cardPanel, "MainMenu");
    }

    private void handlePhotoUpload(String accountType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a Profile Picture");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (JPG, PNG)", "jpg", "png");
        fileChooser.addChoosableFileFilter(filter);

        int result = fileChooser.showOpenDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());
                ImageIcon imageIcon = new ImageIcon(imageBytes);

                Image scaledImage = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);

                if ("Savings".equals(accountType)) {
                    savingsProfilePicBytes = imageBytes;
                    savingsPhotoPreview.setIcon(new ImageIcon(scaledImage));
                    savingsPhotoPreview.setText("");
                } else {
                    currentProfilePicBytes = imageBytes;
                    currentPhotoPreview.setIcon(new ImageIcon(scaledImage));
                    currentPhotoPreview.setText("");
                }
                logger.info("Photo selected for upload: " + selectedFile.getAbsolutePath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to read image file.", e);
                showErrorDialog("File Error", "Could not read the selected image file.");
            }
        }
    }

    private void handleAccountCreation(String accountType) {
        JTextField nameField, addressField, contactField, govIdField, ageField, balanceField;
        JPasswordField passField, confirmPassField;

        if ("Savings".equals(accountType)) {
            nameField = savingsCustName; addressField = savingsAddress; contactField = savingsContact;
            govIdField = savingsGovId; ageField = savingsAge; balanceField = savingsBalance;
            passField = savingsPasswordField; confirmPassField = savingsConfirmPasswordField;
        } else {
            nameField = currentCustName; addressField = currentAddress; contactField = currentContact;
            govIdField = currentGovId; ageField = currentAge; balanceField = currentBalance;
            passField = currentPasswordField; confirmPassField = currentConfirmPasswordField;
        }

        if (nameField.getText().trim().isEmpty() || addressField.getText().trim().isEmpty() ||
                contactField.getText().trim().isEmpty() || govIdField.getText().trim().isEmpty() ||
                ageField.getText().trim().isEmpty() || balanceField.getText().trim().isEmpty()) {
            showErrorDialog("Invalid Input", "All fields except photo must be filled out.");
            return;
        }

        String password = new String(passField.getPassword());
        String confirmPassword = new String(confirmPassField.getPassword());

        if (password.isEmpty() || password.length() < 6) {
            showErrorDialog("Invalid Password", "Password must be at least 6 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorDialog("Password Mismatch", "Passwords do not match.");
            return;
        }

        if (!Validator.isPositiveInteger(ageField.getText())) {
            showErrorDialog("Invalid Input", "Age must be a valid positive number.");
            return;
        }
        if (!Validator.isPositiveDouble(balanceField.getText())) {
            showErrorDialog("Invalid Input", "Balance must be a valid positive number.");
            return;
        }

        double balance = Double.parseDouble(balanceField.getText());
        if (balance < 1500) {
            showErrorDialog("Invalid Balance", "Minimum initial balance must be at least 1500.");
            return;
        }

        byte[] profilePicBytes = "Savings".equals(accountType) ? savingsProfilePicBytes : currentProfilePicBytes;

        new SwingWorker<Account, Void>() {
            @Override
            protected Account doInBackground() throws Exception {
                String accNum = generateUniqueAccountNumber();
                int age = Integer.parseInt(ageField.getText());
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                Account acc = "Savings".equals(accountType) ?
                        new SavingsAccount(nameField.getText(), accNum, balance, addressField.getText(), contactField.getText(), govIdField.getText(), age, profilePicBytes, hashedPassword) :
                        new CurrentAccount(nameField.getText(), accNum, balance, addressField.getText(), contactField.getText(), govIdField.getText(), age, profilePicBytes, hashedPassword);

                accountDAO.save(acc);
                accountDAO.recordTransaction(acc.accountNumber, "INITIAL DEPOSIT", balance);
                return acc;
            }

            @Override
            protected void done() {
                try {
                    Account newAccount = get();

                    String successMessage = String.format("Account created successfully!\nYour New Account Number is: %s", newAccount.accountNumber);
                    JOptionPane.showMessageDialog(mainFrame, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
                    logger.info("Created new " + accountType + " account: " + newAccount.accountNumber);

                    nameField.setText(""); addressField.setText(""); contactField.setText(""); govIdField.setText("");
                    ageField.setText(""); balanceField.setText(""); passField.setText(""); confirmPassField.setText("");

                    if ("Savings".equals(accountType)) {
                        savingsPhotoPreview.setIcon(null);
                        savingsPhotoPreview.setText("No Photo Uploaded");
                        savingsProfilePicBytes = null;
                    } else {
                        currentPhotoPreview.setIcon(null);
                        currentPhotoPreview.setText("No Photo Uploaded");
                        currentProfilePicBytes = null;
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Account creation failed.", ex.getCause());
                    showErrorDialog("Database Error", "Could not save the account: " + ex.getCause().getMessage());
                }
            }
        }.execute();
    }

    private void handleDeposit() {
        String amountStr = depositAmountField.getText();
        if (!Validator.isPositiveDouble(amountStr)) {
            showErrorDialog("Invalid Input", "Please enter a valid, positive amount.");
            return;
        }

        double amount = Double.parseDouble(amountStr);

        new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                loggedInAccount.deposit(amount);
                return loggedInAccount.balance;
            }
            @Override
            protected void done() {
                try {
                    double newBalance = get();
                    JOptionPane.showMessageDialog(mainFrame, String.format("Deposit successful. New balance: %.2f", newBalance));
                    logger.info(String.format("Deposited %.2f into account %s", amount, loggedInAccount.accountNumber));
                    depositAmountField.setText("");
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Deposit failed.", ex.getCause());
                    showErrorDialog("Deposit Error", "Could not process deposit: " + ex.getCause().getMessage());
                }
            }
        }.execute();
    }

    private void handleWithdraw() {
        String amountStr = withdrawAmountField.getText();
        if (!Validator.isPositiveDouble(amountStr)) {
            showErrorDialog("Invalid Input", "Please provide a valid, positive amount.");
            return;
        }

        double amount = Double.parseDouble(amountStr);

        new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                loggedInAccount.withdraw(amount);
                return loggedInAccount.balance;
            }

            @Override
            protected void done() {
                try {
                    double newBalance = get();
                    JOptionPane.showMessageDialog(mainFrame, String.format("Withdrawal successful. New balance: %.2f", newBalance));
                    logger.info(String.format("Withdrew %.2f from account %s", amount, loggedInAccount.accountNumber));
                    withdrawAmountField.setText("");
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "Withdrawal failed.", ex.getCause());
                    showErrorDialog("Withdrawal Failed", ex.getCause().getMessage());
                }
            }
        }.execute();
    }

    private void handleCheckBalance() {
        if (loggedInAccount == null) return;
        Account acc = accountDAO.findAccountByNumber(loggedInAccount.accountNumber);
        if (acc == null) {
            showErrorDialog("Error", "Could not refresh account data.");
            return;
        }
        loggedInAccount = acc;

        String details = String.format(
                "Account Type: %s\n" +
                        "Account Holder: %s\n" +
                        "Address: %s\n" +
                        "Contact: %s\n" +
                        "------------------------------------\n" +
                        "CURRENT BALANCE: ₹ %.2f",
                loggedInAccount.accountType, loggedInAccount.customerName, loggedInAccount.address, loggedInAccount.contactNumber, loggedInAccount.balance
        );
        balanceResultArea.setText(details);

        if (loggedInAccount.profilePicture != null) {
            ImageIcon icon = new ImageIcon(loggedInAccount.profilePicture);
            Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            balancePhotoLabel.setIcon(new ImageIcon(scaledImage));
            balancePhotoLabel.setText("");
        } else {
            balancePhotoLabel.setIcon(null);
            balancePhotoLabel.setText("No Photo");
        }
    }

    private void handleFundTransfer() {
        String toAccNum = toAccField.getText();
        String amountStr = transferAmountField.getText();

        if (!Validator.isValidAccountNumber(toAccNum) || !Validator.isPositiveDouble(amountStr)) {
            showErrorDialog("Invalid Input", "Please provide a valid recipient account and a positive amount.");
            return;
        }
        if (loggedInAccount.accountNumber.equals(toAccNum)) {
            showErrorDialog("Invalid Operation", "Cannot transfer funds to the same account.");
            return;
        }

        double amount = Double.parseDouble(amountStr);

        JDialog loadingDialog = new JDialog(mainFrame, "Processing...", true);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        loadingDialog.add(BorderLayout.CENTER, progressBar);
        loadingDialog.add(BorderLayout.NORTH, new JLabel("Processing your fund transfer. Please wait..."));
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        loadingDialog.setSize(300, 75);
        loadingDialog.setLocationRelativeTo(mainFrame);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Account toAccount = accountDAO.findAccountByNumber(toAccNum);

                if (toAccount == null) {
                    throw new SQLException("Recipient account number does not exist.");
                }

                Thread.sleep(2000);

                accountDAO.transferFunds(loggedInAccount, toAccount, amount);
                return null;
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    get();
                    JOptionPane.showMessageDialog(mainFrame, "Fund transfer successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    logger.info(String.format("Transferred %.2f from %s to %s", amount, loggedInAccount.accountNumber, toAccNum));
                    toAccField.setText("");
                    transferAmountField.setText("");
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Fund transfer failed.", ex.getCause());
                    showErrorDialog("Transfer Failed", ex.getCause().getMessage());
                }
            }
        };
        worker.execute();
        loadingDialog.setVisible(true);
    }

    private void handleChangePassword() {
        String oldPass = new String(oldPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirmNewPass = new String(confirmNewPasswordField.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmNewPass.isEmpty()) {
            showErrorDialog("Invalid Input", "All fields are required.");
            return;
        }
        if (newPass.length() < 6) {
            showErrorDialog("Invalid Password", "New password must be at least 6 characters long.");
            return;
        }
        if (!newPass.equals(confirmNewPass)) {
            showErrorDialog("Password Mismatch", "New passwords do not match.");
            return;
        }

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (BCrypt.checkpw(oldPass, loggedInAccount.passwordHash)) {
                    String newHashedPassword = BCrypt.hashpw(newPass, BCrypt.gensalt());
                    accountDAO.updatePassword(loggedInAccount.accountNumber, newHashedPassword);
                    return true;
                }
                return false;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(mainFrame, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        oldPasswordField.setText("");
                        newPasswordField.setText("");
                        confirmNewPasswordField.setText("");
                    } else {
                        showErrorDialog("Authentication Failed", "Incorrect old password.");
                    }
                } catch (Exception ex) {
                    showErrorDialog("Error", "Could not change password.");
                    logger.log(Level.SEVERE, "Password change failed.", ex.getCause());
                }
            }
        }.execute();
    }

    private void handleAccountNumberRecovery() {
        JTextField govtIdField = new JTextField(20);
        JTextField contactField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.add(new JLabel("Enter Government ID:"));
        panel.add(govtIdField);
        panel.add(new JLabel("Enter Contact Number:"));
        panel.add(contactField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Account Number Recovery", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String govtId = govtIdField.getText();
            String contact = contactField.getText();
            if(govtId.trim().isEmpty() || contact.trim().isEmpty()) {
                showErrorDialog("Invalid Input", "Both fields are required for recovery.");
                return;
            }

            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return accountDAO.findAccountByGovtIdAndContact(govtId, contact);
                }

                @Override
                protected void done() {
                    try {
                        String accNum = get();
                        if (accNum != null) {
                            JOptionPane.showMessageDialog(mainFrame, "Your Account Number is: " + accNum, "Account Found", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            showErrorDialog("Not Found", "No account found with the provided details.");
                        }
                    } catch (Exception e) {
                        showErrorDialog("Error", "An error occurred during recovery.");
                        logger.log(Level.SEVERE, "Account recovery failed.", e.getCause());
                    }
                }
            }.execute();
        }
    }

    private void handlePasswordRecovery() {
        JTextField govtIdField = new JTextField(20);
        JTextField contactField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.add(new JLabel("Enter Government ID:"));
        panel.add(govtIdField);
        panel.add(new JLabel("Enter Contact Number:"));
        panel.add(contactField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Password Recovery", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String govtId = govtIdField.getText().trim();
            String contact = contactField.getText().trim();
            if(govtId.isEmpty() || contact.isEmpty()) {
                showErrorDialog("Invalid Input", "Both fields are required for recovery.");
                return;
            }

            String accNum = accountDAO.findAccountByGovtIdAndContact(govtId, contact);
            if (accNum == null) {
                showErrorDialog("Not Found", "No account found with the provided details.");
                return;
            }

            JPasswordField newPassField = new JPasswordField(20);
            JPasswordField confirmPassField = new JPasswordField(20);
            JPanel resetPanel = new JPanel(new GridLayout(0, 1, 10, 10));
            resetPanel.add(new JLabel("Enter New Password:"));
            resetPanel.add(newPassField);
            resetPanel.add(new JLabel("Confirm New Password:"));
            resetPanel.add(confirmPassField);

            int resetResult = JOptionPane.showConfirmDialog(mainFrame, resetPanel, "Set New Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (resetResult == JOptionPane.OK_OPTION) {
                String newPass = new String(newPassField.getPassword());
                String confirmPass = new String(confirmPassField.getPassword());
                if (newPass.length() < 6 || !newPass.equals(confirmPass)) {
                    showErrorDialog("Invalid", "Passwords must match and be at least 6 characters.");
                    return;
                }

                try {
                    String newHashedPassword = BCrypt.hashpw(newPass, BCrypt.gensalt());
                    accountDAO.updatePassword(accNum, newHashedPassword);
                    JOptionPane.showMessageDialog(mainFrame, "Password has been successfully reset.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    showErrorDialog("Error", "Could not reset password.");
                }
            }
        }
    }

    private void handleComplaintRegistration(boolean isScam) {
        if (complaintTypeGroup.getSelection() == null) {
            showErrorDialog("Input Required", "Please select a complaint type.");
            return;
        }
        String details = complaintArea.getText();
        if (details.trim().isEmpty()) {
            showErrorDialog("Input Required", "Please enter the complaint details.");
            return;
        }

        String type = isScam ? "Scam" : "Other";

        try {
            accountDAO.saveComplaint(type, details);
            JOptionPane.showMessageDialog(mainFrame, "Complaint registered successfully!");
            logger.info("New complaint registered. Type: " + type);
            complaintArea.setText("");
            complaintTypeGroup.clearSelection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to save complaint.", e);
            showErrorDialog("Database Error", "Could not save your complaint.");
        }
    }

    private void handleViewComplaints(JTextArea scamArea, JTextArea otherArea) {
        try {
            Map<String, List<String>> allComplaints = accountDAO.getAllComplaints();
            scamArea.setText(String.join("\n\n", allComplaints.getOrDefault("Scam", new ArrayList<>())));
            otherArea.setText(String.join("\n\n", allComplaints.getOrDefault("Other", new ArrayList<>())));
            logger.info("Refreshed complaints view.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to load complaints.", e);
            showErrorDialog("Database Error", "Could not retrieve complaints.");
        }
    }

    private void handleViewHistory() {
        if (loggedInAccount == null) return;
        try {
            List<Transaction> transactions = accountDAO.getTransactionHistory(loggedInAccount.accountNumber);
            if (transactions.isEmpty()) {
                historyArea.setText("No transactions found for this account.");
            } else {
                StringBuilder historyText = new StringBuilder();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (Transaction tx : transactions) {
                    historyText.append(String.format("[%s] %-30s : %.2f\n",
                            sdf.format(tx.getTimestamp()),
                            tx.getType(),
                            tx.getAmount()));
                }
                historyArea.setText(historyText.toString());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to load transaction history.", e);
            showErrorDialog("Database Error", "Could not retrieve transaction history.");
        }
    }

    private void handleExportToPdf() {
        if (loggedInAccount == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Statement as PDF");
        fileChooser.setSelectedFile(new File("Statement_" + loggedInAccount.accountNumber + ".pdf"));
        int userSelection = fileChooser.showSaveDialog(mainFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    List<Transaction> transactions = accountDAO.getTransactionHistory(loggedInAccount.accountNumber);
                    PdfStatementGenerator.generate(loggedInAccount, transactions, fileToSave.getAbsolutePath());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(mainFrame, "Statement saved successfully to:\n" + fileToSave.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                        logger.info("Exported PDF statement for account " + loggedInAccount.accountNumber);
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "Failed to generate PDF statement.", ex.getCause());
                        showErrorDialog("PDF Export Error", "Could not generate or save the PDF file.");
                    }
                }
            }.execute();
        }
    }

    // --- UI Helper/Utility Methods ---

    private JLabel createLabel(String text) {
        return createLabel(text, true);
    }

    private JLabel createLabel(String text, boolean isBold) {
        JLabel label = new JLabel(text);
        label.setFont(isBold ? FONT_BOLD : FONT_PRIMARY);
        label.setForeground(COLOR_TEXT_DARK);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(FONT_PRIMARY);
        textField.setMaximumSize(new Dimension(300, 30));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        return textField;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(FONT_PRIMARY);
        return textArea;
    }


    private JButton createButton(String text, String... cardName) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setBackground(COLOR_BUTTON_BG);
        button.setForeground(COLOR_TEXT_DARK);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(COLOR_BUTTON_HOVER_BG);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(COLOR_BUTTON_BG);
            }
        });

        if (cardName.length > 0) {
            button.addActionListener(e -> cardLayout.show(cardPanel, cardName[0]));
        }

        return button;
    }

    private JButton createDashboardButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBackground(Color.WHITE);
        button.setForeground(COLOR_TEXT_DARK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEtchedBorder());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            if (cardName.equals("MainMenu")) {
                handleLogout();
            } else {
                cardLayout.show(cardPanel, cardName);
            }
        });

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(COLOR_BACKGROUND);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        return button;
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.ERROR_MESSAGE);
    }
}