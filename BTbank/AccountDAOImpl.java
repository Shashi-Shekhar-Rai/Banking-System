import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

class AccountDAOImpl implements AccountDAO {

    @Override
    public List<Account> findAllAsList() throws SQLException {
        List<Account> allAccounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY customer_name";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                allAccounts.add(mapResultSetToAccount(rs));
            }
        }
        return allAccounts;
    }

    @Override
    public boolean accountExists(String accNum) {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Account findAccountByNumber(String accNum) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    @Override
    public void save(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, customer_name, password_hash, balance, address, contact_number, govt_id, age, account_type, profile_picture) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.accountNumber);
            pstmt.setString(2, account.customerName);
            pstmt.setString(3, account.passwordHash);
            pstmt.setDouble(4, account.balance);
            pstmt.setString(5, account.address);
            pstmt.setString(6, account.contactNumber);
            pstmt.setString(7, account.govtID);
            pstmt.setInt(8, account.age);
            pstmt.setString(9, account.accountType);

            if (account.profilePicture != null) {
                pstmt.setBytes(10, account.profilePicture);
            } else {
                pstmt.setNull(10, java.sql.Types.BLOB);
            }
            pstmt.executeUpdate();
        }
    }

    @Override
    public Account findAndAuthenticate(String accNum, String password) throws SQLException {
        Account account = findAccountByNumber(accNum);
        if (account != null && BCrypt.checkpw(password, account.passwordHash)) {
            return account;
        }
        return null;
    }

    @Override
    public void updateBalance(Account account) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, account.balance);
            pstmt.setString(2, account.accountNumber);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updatePassword(String accNum, String newHashedPassword) throws SQLException {
        String sql = "UPDATE accounts SET password_hash = ? WHERE account_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newHashedPassword);
            pstmt.setString(2, accNum);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void recordTransaction(String accountNumber, String type, double amount) throws SQLException {
        String sql = "INSERT INTO transactions (account_number, transaction_type, amount) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void transferFunds(Account from, Account to, double amount) throws SQLException, InsufficientFundsException {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            from.withdraw(amount);
            to.deposit(amount);

            recordTransaction(from.accountNumber, "TRANSFER_OUT to " + to.accountNumber, amount);
            recordTransaction(to.accountNumber, "TRANSFER_IN from " + from.accountNumber, amount);

            conn.commit();
        } catch (SQLException | InsufficientFundsException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public List<Transaction> getTransactionHistory(String accountNumber) throws SQLException {
        List<Transaction> history = new ArrayList<>();
        String sql = "SELECT timestamp, transaction_type, amount FROM transactions WHERE account_number = ? ORDER BY timestamp DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                history.add(new Transaction(
                        rs.getTimestamp("timestamp"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount")
                ));
            }
        }
        return history;
    }

    @Override
    public void saveComplaint(String type, String details) throws SQLException {
        String sql = "INSERT INTO complaints (type, details) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            pstmt.setString(2, details);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Map<String, List<String>> getAllComplaints() throws SQLException {
        Map<String, List<String>> allComplaints = new HashMap<>();
        allComplaints.put("Scam", new ArrayList<>());
        allComplaints.put("Other", new ArrayList<>());

        String sql = "SELECT * FROM complaints ORDER BY timestamp DESC";
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String type = rs.getString("type");
                String complaint = String.format("[%s] %s", rs.getTimestamp("timestamp"), rs.getString("details"));
                if (allComplaints.containsKey(type)) {
                    allComplaints.get(type).add(complaint);
                }
            }
        }
        return allComplaints;
    }

    @Override
    public String findAccountByGovtIdAndContact(String govtId, String contact) {
        String sql = "SELECT account_number FROM accounts WHERE govt_id = ? AND contact_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, govtId);
            pstmt.setString(2, contact);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("account_number");
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        String accType = rs.getString("account_type");
        byte[] picBytes = rs.getBytes("profile_picture");
        String passwordHash = rs.getString("password_hash");

        return "Savings".equals(accType) ?
                new SavingsAccount(rs.getString("customer_name"), rs.getString("account_number"), rs.getDouble("balance"), rs.getString("address"), rs.getString("contact_number"), rs.getString("govt_id"), rs.getInt("age"), picBytes, passwordHash) :
                new CurrentAccount(rs.getString("customer_name"), rs.getString("account_number"), rs.getDouble("balance"), rs.getString("address"), rs.getString("contact_number"), rs.getString("govt_id"), rs.getInt("age"), picBytes, passwordHash);
    }
}