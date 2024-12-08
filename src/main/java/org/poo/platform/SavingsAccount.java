package org.poo.platform;

import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

import java.util.ArrayList;

public class SavingsAccount extends Account {
    private String currency;
    private String IBAN;
    @Getter @Setter
    private double balance;
    private String type;
    private ArrayList<org.poo.platform.Card> cards;
    private double interestRate;

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
    }

    public SavingsAccount(SavingsAccount account) {
        this.currency = account.currency;
        this.IBAN = account.IBAN;
        this.balance = account.balance;
        this.type = account.type;
        this.cards = new ArrayList<>();
        for (Card card : account.cards) {
            this.cards.add(new Card(card));
        }
        this.interestRate = account.interestRate;
    }
}
