package org.poo.platform;

import lombok.Getter;
import lombok.Setter;
import org.poo.platform.accounts.Account;

import java.util.ArrayList;

public final class User {
    @Getter @Setter
    private String firstName;
    @Getter @Setter
    private String lastName;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private ArrayList<Account> accounts;

    public User(final String firstName, final String lastName,
                final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        accounts = new ArrayList<>();
    }

    public void addAccount(final Account account) {
        accounts.add(account);
    }

    public User(final User user) {
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.email = user.email;
        this.accounts = new ArrayList<>();
        if (!user.getAccounts().isEmpty()) {
            for (Account accountOut : user.accounts) {
                this.accounts.add(Account.copyAccount(accountOut));
            }
        }
    }
}
