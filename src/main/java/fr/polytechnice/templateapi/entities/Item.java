package fr.polytechnice.templateapi.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class Item {
    public String name;
    public String description;
    public int price;
    @JsonProperty("creation-date")
    public ZonedDateTime creationDate;

    public Item() {}

    public Item(String name, String description, int price, ZonedDateTime creationDate) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.creationDate = creationDate;
    }
}
