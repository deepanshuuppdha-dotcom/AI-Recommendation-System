package com.recsys.model;

/** Each interaction type carries a weight used when building a user's behavior profile. */
public enum InteractionType {
    VIEW(1), CLICK(2), LIKE(3), PURCHASE(5);

    private final int weight;
    InteractionType(int weight) { this.weight = weight; }
    public int getWeight() { return weight; }
}
