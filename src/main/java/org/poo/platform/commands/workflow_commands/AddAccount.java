package org.poo.platform.commands.workflow_commands;

import org.poo.platform.Account;
import org.poo.platform.ClassicAccount;
import org.poo.platform.SavingsAccount;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class AddAccount extends Command {
    private String email;
    private String currency;
    private String accountType;
    private int timestamp;
    private double interestRate;
    private User user;

    public AddAccount(String email, String currency, String accountType, int timestamp, double interestRate, ArrayList<User> users) {
        this.email = email;
        this.currency = currency;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.interestRate = interestRate;
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                this.user = user;
            }
        }
    }

    @Override
    public void operation() {
        Account account = null;
        if (accountType.equals("classic")) {
            account = new ClassicAccount(currency);
        } else {
            account = new SavingsAccount(currency, interestRate);
        }
        user.addAccount(account);
    }
}
