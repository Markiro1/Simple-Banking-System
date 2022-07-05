package banking;

import java.sql.*;

public class DataBase {
    private final String url;
    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS card (id INTEGER PRIMARY KEY, number TEXT NOT NULL, pin TEXT NOT NULL, balance INTEGER DEFAULT 0);";
    private final String FIND_BY_NUMBER = "SELECT id, number, pin, balance FROM card WHERE number = ?";
    private final String ADD_CARD = "INSERT INTO card (number, pin, balance) VALUES (?, ?, ?);";
    private final String FIND_BY_ID = "SELECT id, number, pin, balance FROM card WHERE id = ?";
    private final String CLOSE_CARD = "DELETE FROM card WHERE id = ?;";
    private final String SAVE_BALANCE = "UPDATE card SET balance = ? WHERE id = ?";
    private final String TRANSACTION = "UPDATE card SET balance = ? WHERE id = ?";

    public DataBase(String fileName) {
        url = "jdbc:sqlite:" + fileName;
        createTable();
    }

    private void createTable() {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection != null) {
                try (Statement statement = connection.createStatement()){
                    statement.executeUpdate(CREATE_TABLE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void saveBalance(Card card) {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE_BALANCE)){
                    preparedStatement.setInt(1, card.getBalance());
                    preparedStatement.setInt(2, card.getId());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void transaction(Card senderCard, Card resipientCard, int sum) {
        try (Connection connection = DriverManager.getConnection(url)){
            if (connection != null) {
                connection.setAutoCommit(false);
                try (PreparedStatement preparedStatement = connection.prepareStatement(TRANSACTION)){

                    senderCard.withdrawBalance(sum);
                    preparedStatement.setInt(1, senderCard.getBalance());
                    preparedStatement.setInt(2, senderCard.getId());
                    preparedStatement.executeUpdate();

                    resipientCard.toUpBalance(sum);
                    preparedStatement.setInt(1, resipientCard.getBalance());
                    preparedStatement.setInt(2, resipientCard.getId());
                    preparedStatement.executeUpdate();

                    connection.commit();
                } catch (SQLException e) {
                    connection.rollback();
                    e.printStackTrace();
                }
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Card findByNumber(String cardNumber) {
        Card card = null;

        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NUMBER)) {
                    preparedStatement.setString(1, cardNumber);

                    try (ResultSet rs = preparedStatement.executeQuery()) {
                        card = new Card(rs.getInt("id"),
                        rs.getString("number"),
                        rs.getString("pin"),
                        rs.getInt("balance"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return card;
    }

    public Card findById(int id) {
        Card card = null;

        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)){
                    preparedStatement.setInt(1, id);

                    try (ResultSet rs = preparedStatement.executeQuery()){
                        card = new Card(rs.getInt("id"),
                                rs.getString("number"),
                                rs.getString("pin"),
                                rs.getInt("balance"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return card;
    }

    public void add(Card card) {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_CARD, Statement.RETURN_GENERATED_KEYS)){
                    preparedStatement.setString(1, card.getCardNumber());
                    preparedStatement.setString(2, card.getPIN());
                    preparedStatement.setInt(3, card.getBalance());

                    int rows = preparedStatement.executeUpdate();
                    if (rows == 0) {
                        throw new SQLException("Creating user failed!");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void closeCard(int id) {
        try (Connection connection = DriverManager.getConnection(url)){
            if (connection != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(CLOSE_CARD)){
                    preparedStatement.setInt(1, id);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
