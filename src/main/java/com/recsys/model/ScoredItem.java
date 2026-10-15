package com.recsys.model;

/** An item together with its recommendation score (0..1) and a human-readable reason. */
public class ScoredItem {
    private final Item item;
    private final double score;
    private final String reason;

    public ScoredItem(Item item, double score, String reason) {
        this.item = item;
        this.score = score;
        this.reason = reason;
    }

    public Item getItem() { return item; }
    public double getScore() { return score; }
    public String getReason() { return reason; }
    public int getPopularity() { return item.getPopularity(); }
}
