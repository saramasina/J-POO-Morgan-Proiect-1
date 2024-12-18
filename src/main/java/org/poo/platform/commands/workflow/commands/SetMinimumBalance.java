package org.poo.platform.commands.workflow.commands;

import org.poo.platform.accounts.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public final class SetMinimumBalance implements Command {
    private Account account;
    private final double amount;

    public SetMinimumBalance(final String account, final double amount,
                             final ArrayList<User> users) {
        this.amount = amount;
        for (User user : users) {
            for (Account acc : user.getAccounts()) {
                if (acc.getIban().equals(account)) {
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
