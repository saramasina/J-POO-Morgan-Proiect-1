package org.poo.platform.commands.workflow.commands;

import org.poo.platform.accounts.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public final class AddFunds implements Command {
    private Account account;
    private double amount;

    public AddFunds(final String iban, final double ammount,
                    final ArrayList<User> users) {
        this.amount = ammount;
        for (User user : users) {
            for (Account accountUser : user.getAccounts()) {
                if (accountUser.getIban().equals(iban)) {
                    account = accountUser;
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
