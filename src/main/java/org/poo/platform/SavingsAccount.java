package org.poo.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

import java.util.ArrayList;

public class SavingsAccount extends Account {
    @Getter @Setter
    private String currency;
    @Getter @Setter
    private String IBAN;
    @Getter @Setter
    private double balance;
    @Getter @Setter
    private String type;
    @Getter @Setter
    private ArrayList<Card> cards;
    @Getter @Setter @JsonIgnore
    private double interestRate;
    @Getter @Setter @JsonIgnore
    private String alias;
    @Getter @Setter @JsonIgnore
    private double minBalance;
    @Getter @Setter @JsonIgnore
    private boolean frozen;

    @Override
    public ArrayList<Card> getCards() {
        return cards;
    }

    @Override
    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    @Override
    public String getIBAN() {
        return IBAN;
    }

    @Override
    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public SavingsAccount(String currency, double interestRate) {
        this.currency = currency;
        this.interestRate = interestRate;
        type = "savings";
        cards = new ArrayList<>();
        balance = 0;
        IBAN = Utils.generateIBAN();
        minBalance = 0;
    }

    public SavingsAccount(SavingsAccount account) {
        this.currency = account.currency;
        this.IBAN = account.IBAN;
        this.balance = account.balance;
        this.type = account.type;
        this.cards = new ArrayList<>();
        this.interestRate = account.interestRate;
        this.alias = account.alias;
        this.minBalance = account.minBalance;
        this.frozen = account.frozen;
        for (Card card : account.cards) {
            this.cards.add(new Card(card));
        }
        this.interestRate = account.interestRate;
        this.alias = account.alias;
    }
}
