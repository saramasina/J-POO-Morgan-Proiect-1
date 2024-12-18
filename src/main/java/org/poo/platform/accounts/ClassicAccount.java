package org.poo.platform.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.platform.Card;
import org.poo.utils.Utils;

import java.util.ArrayList;

public final class ClassicAccount extends Account {
    @Getter @Setter
    private String currency;
    @JsonProperty("IBAN")
    private String iban;
    @Getter @Setter
    private double balance;
    @Getter @Setter
    private String type;
    private ArrayList<Card> cards;
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

    public ClassicAccount(final String currency) {
        this.currency = currency;
        type = "classic";
        iban = Utils.generateIBAN();
        balance = 0;
        cards = new ArrayList<>();
        minBalance = 0;
        ObjectMapper mapper = new ObjectMapper();
        transactions = mapper.createArrayNode();
    }

    public ClassicAccount(final ClassicAccount account) {
        this.currency = account.currency;
        this.iban = account.iban;
        this.balance = account.balance;
        this.type = account.type;
        this.cards = new ArrayList<>();
        this.alias = account.alias;
        this.minBalance = account.minBalance;
        this.frozen = account.isFrozen();
        this.transactions = account.transactions.deepCopy();
        for (Card card : account.cards) {
            this.cards.add(new Card(card));
        }
    }
}
