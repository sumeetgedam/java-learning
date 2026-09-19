package  com.learning.core.classes;

public class BankAccountDemo {

    public static void main(String[] args) {
        BankAccount account = new BankAccount("ACC-01", 100.0);

        
        account.deposit(50.0);
        account.printBalance();

    }
}