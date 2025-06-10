

import java.sql.SQLException;

abstract class Account {
    String accountNumber;
    String customerName;
    double balance;
    String address;
    String contactNumber;
    String govtID;
    int age;
    String accountType;
    byte[] profilePicture;
    String passwordHash;
    AccountDAO accountDAO = new AccountDAOImpl();

    Account(String customerName, String accountNumber, double balance, String address, String contactNumber, String govtID, int age, String accountType, byte[] profilePicture, String passwordHash) {
        this.customerName = customerName;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.address = address;
        this.contactNumber = contactNumber;
        this.govtID = govtID;
        this.age = age;
        this.accountType = accountType;
        this.profilePicture = profilePicture;
        this.passwordHash = passwordHash;
    }

    abstract void withdraw(double amount) throws InsufficientFundsException, SQLException;

    void deposit(double amount) throws SQLException {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        this.balance += amount;
        accountDAO.updateBalance(this);
        accountDAO.recordTransaction(this.accountNumber, "DEPOSIT", amount);
    }
}