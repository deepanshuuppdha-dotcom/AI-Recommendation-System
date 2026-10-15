package com.recsys.model;

public enum StrategyType {
    CONTENT_BASED("Content-based (stated preferences only)"),
    BEHAVIOR_BASED("Behavior-based (views, clicks, likes, purchases)"),
    HYBRID("Hybrid (preferences + behavior + popularity)");

    private final String label;
    StrategyType(String label) { this.label = label; }
    public String getLabel() { return label; }
}
