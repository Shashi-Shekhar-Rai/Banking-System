

import java.sql.Timestamp;

class Transaction {
    private final Timestamp timestamp;
    private final String type;
    private final double amount;

    public Transaction(Timestamp timestamp, String type, double amount) {
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
    }

    public Timestamp getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
}