package banking;

public class Card {
    private int id;
    private String cardNumber;
    private String PIN;
    private int balance;

    public Card(int id, String cardNumber, String PIN, int balance) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.PIN = PIN;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void toUpBalance(int sum) {
        this.balance += sum;
    }

    public void withdrawBalance(int sum) {
        this.balance -= sum;
    }
}
