package org.poo.platform.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.platform.Card;

import java.util.ArrayList;

public class Account {
    @Getter
    @Setter
    private String currency;
    @Getter
    @Setter
    @JsonProperty("IBAN")
    private String iban;
    @Getter
    @Setter
    private double balance;
    @Getter
    @Setter
    private String type;
    @Getter
    @Setter
    private ArrayList<Card> cards;
    @Getter
    @Setter
    @JsonIgnore
    private String alias;
    @Getter
    @Setter
    @JsonIgnore
    private double minBalance;
    @Getter
    @Setter
    @JsonIgnore
    private boolean frozen;
    @Getter @Setter @JsonIgnore
    private ArrayNode transactions;

    public Account() {
        cards = new ArrayList<>();
        minBalance = 0;
    }

    public Account(final Account account) {
        this.currency = account.currency;
        this.iban = account.iban;
        this.balance = account.balance;
        this.type = account.type;
        this.cards = new ArrayList<>();
        this.alias = account.alias;
        this.minBalance = account.minBalance;
        this.frozen = account.isFrozen();
        ObjectMapper mapper = new ObjectMapper();
        transactions = mapper.createArrayNode();
        for (Card card : account.cards) {
            this.cards.add(new Card(card));
        }
    }

    /**
     * Creates a deep-copy of the given account,
     * using the copy constructor of each class
     * @param account the object to be deep-copied
     */
    public static Account copyAccount(final Account account) {
            if (account.getType().equals("savings")) {
                return new SavingsAccount((SavingsAccount) account);
            } else if (account.getType().equals("classic")) {
                return new ClassicAccount((ClassicAccount) account);
            }
        return null;
    }
}
