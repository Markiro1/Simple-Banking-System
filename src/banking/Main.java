package banking;

public class Main {

    public static void main(String[] args) {
        // write your code here
        String fileName = args.length == 0 ? "database.db" : args[1];
        BankSystem bankSystem = new BankSystem(fileName);
        bankSystem.welcomeScreen();
    }
}


