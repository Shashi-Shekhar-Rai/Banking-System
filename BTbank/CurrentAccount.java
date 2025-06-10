

import java.sql.SQLException;

class CurrentAccount extends Account {
    private static final double OVERDRAFT_LIMIT = 5000;

    CurrentAccount(String customerName, String accountNumber, double balance, String address, String contactNumber, String govtID, int age, byte[] profilePicture, String passwordHash) {
        super(customerName, accountNumber, balance, address, contactNumber, govtID, age, "Current", profilePicture, passwordHash);
    }

    @Override
    void withdraw(double amount) throws InsufficientFundsException, SQLException {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (balance - amount < -OVERDRAFT_LIMIT) {
            throw new InsufficientFundsException("Withdrawal failed. Exceeded overdraft limit of " + OVERDRAFT_LIMIT);
        }
        balance -= amount;
        accountDAO.updateBalance(this);
        accountDAO.recordTransaction(this.accountNumber, "WITHDRAWAL", amount);
    }
}