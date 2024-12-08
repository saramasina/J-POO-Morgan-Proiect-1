package org.poo.platform;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class User {
    @Getter @Setter
    private String firstName;
    @Getter @Setter
    private String lastName;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private ArrayList<Account> accounts;

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public User(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.accounts = new ArrayList<>();
        if (user.getAccounts().size() != 0) {
            for (Account account_out : user.accounts) {
                this.accounts.add(Account.copyAccount(account_out));
            }
        }
    }
}
