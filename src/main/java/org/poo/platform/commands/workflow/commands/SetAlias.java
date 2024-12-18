package org.poo.platform.commands.workflow.commands;

import org.poo.platform.accounts.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public final class SetAlias implements Command {
    private Account account;
    private String alias;

    public SetAlias(final String email, final String account,
                    final String alias, final ArrayList<User> users) {
        this.alias = alias;
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                for (Account accountUser : user.getAccounts()) {
                    if (accountUser.equals(account)) {
                        this.account = accountUser;
                    }
                }
            }
        }
    }
    @Override
    public void operation() {
        if (account != null) {
            account.setAlias(alias);
        }
    }
}
