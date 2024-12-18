package org.poo.platform.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.platform.Card;
import org.poo.utils.Utils;

import java.util.ArrayList;

public final class SavingsAccount extends Account {
    @Getter @Setter
    private String currency;
    private String iban;
    @Getter @Setter
    private double balance;
    @Getter @Setter
    private String type;
    private ArrayList<Card> cards;
    @Getter @Setter @JsonIgnore
    private double interestRate;
    @Getter @Setter @JsonIgnore
    private String alias;
    @Getter @Setter @JsonIgnore
    private double minBalance;
    @Getter @Setter @JsonIgnore
    private boolean frozen;
    @Getter @Setter @JsonIgnore
    private ArrayNode transactions;

    @Override
    public ArrayList<Card> getCards() {
        return cards;
    }
    @Override
    public void setCards(final ArrayList<Card> cards) {
        this.cards = cards;
    }

    @Override
    public String getIban() {
        return iban;
    }

    @Override
    public void setIban(final String iban) {
        this.iban = iban;
    }

    public SavingsAccount(final String currency,
                          final double interestRate) {
        this.currency = currency;
        this.interestRate = interestRate;
        type = "savings";
        cards = new ArrayList<>();
        balance = 0;
        iban = Utils.generateIBAN();
        minBalance = 0;
        ObjectMapper mapper = new ObjectMapper();
        transactions = mapper.createArrayNode();
    }

    public SavingsAccount(final SavingsAccount account) {
        this.currency = account.currency;
        this.iban = account.iban;
        this.balance = account.balance;
        this.type = account.type;
        this.cards = new ArrayList<>();
        this.interestRate = account.interestRate;
        this.alias = account.alias;
        this.minBalance = account.minBalance;
        this.frozen = account.frozen;
        this.transactions = account.transactions.deepCopy();
        for (Card card : account.cards) {
            this.cards.add(new Card(card));
        }
        this.interestRate = account.interestRate;
        this.alias = account.alias;
    }
}
