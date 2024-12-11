package org.poo.platform.commands.workflow_commands;

import org.poo.platform.Account;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;

public class SetAlias extends Command {
    private Account account;
    private String alias;
    private int timestamp;

    public SetAlias(String email, String account, String alias, int timestamp, ArrayList<User> users) {
        this.timestamp = timestamp;
        this.alias = alias;
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                for (Account account_user : user.getAccounts()) {
                    if (account_user.equals(account)) {
                        this.account = account_user;
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
