package org.poo.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

import java.util.ArrayList;

public class ClassicAccount extends Account{
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

    @Override
    public ArrayList<Card> getCards() {
        return cards;
    }

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

    public ClassicAccount(String currency) {
        this.currency = currency;
        type = "classic";
        IBAN = Utils.generateIBAN();
        balance = 0;
        cards = new ArrayList<>();
    }

    public ClassicAccount(ClassicAccount account) {
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
}
