package org.poo.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Account {
    @Getter @Setter
    private String currency;
    @Getter @Setter @JsonProperty("IBAN")
    private String IBAN;
    @Getter @Setter
    private double balance;
    @Getter @Setter
    private String type;
    @Getter @Setter
    private ArrayList<Card> cards;
    @Getter @Setter @JsonIgnore
    private String alias;

    public Account() {
        cards = new ArrayList<>();
    }

    public Account(Account account) {
        this.currency = account.currency;
        this.IBAN = account.IBAN;
        this.balance = account.balance;
        this.type = account.type;
        this.cards = new ArrayList<>();
        this.alias = account.alias;
        for (Card card : account.cards) {
            this.cards.add(new Card(card));
        }
    }

    public static Account copyAccount(Account account) {
        if (account.getType() != null) {
            if (account.getType().equals("savings")) {
                return new SavingsAccount((SavingsAccount) account);
            } else if (account.getType().equals("classic")) {
                return new ClassicAccount((ClassicAccount) account);
            } else {
                return new Account(account); // Dacă este doar un `Account`
            }
        }
        return null;
    }

}
