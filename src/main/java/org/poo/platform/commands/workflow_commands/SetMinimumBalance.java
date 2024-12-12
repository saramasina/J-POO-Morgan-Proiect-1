package org.poo.platform.commands.workflow_commands;

import org.poo.platform.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class SetMinimumBalance extends Command {
    private int timestamp;
    private Account account;
    private double amount;

    public SetMinimumBalance(String account, double amount, int timestamp, ArrayList<User> users) {
        this.timestamp = timestamp;
        this.amount = amount;
        for (User user : users) {
            for (Account acc : user.getAccounts()) {
                if (acc.getIBAN().equals(account)) {
                    this.account = acc;
                }
            }
        }
    }

    public void operation() {
        if (account != null) {
            account.setMinBalance(amount);
        }
    }
}
