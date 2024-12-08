package org.poo.platform.commands.workflow_commands;

import org.poo.platform.User;
import org.poo.platform.commands.Command;
import org.poo.platform.Account;

import java.util.ArrayList;

public class AddFunds extends Command {
    private Account account;
    private double amount;
    private int timestamp;

    public AddFunds(String IBAN, double ammount, int timestamp, ArrayList<User> users) {
        this.amount = ammount;
        this.timestamp = timestamp;
        for (User user : users) {
            for (Account account_user : user.getAccounts()) {
                if (account_user.getIBAN().equals(IBAN)) {
                    account = account_user;
                }
            }
        }
    }

    @Override
    public void operation() {
        if (account != null) {
            account.setBalance(account.getBalance() + amount);
        }
    }
}
