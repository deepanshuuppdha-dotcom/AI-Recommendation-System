package com.recsys.strategy;

import com.recsys.model.AlgorithmSettings;
import com.recsys.model.Item;

/** Scores items purely by how well their category matches the user's stated preferences. */
public class ContentBasedStrategy implements RecommendationStrategy {
    @Override
    public double score(Item item, RecommendationContext ctx, AlgorithmSettings settings) {
        return ctx.preferenceMatch(item);
    }
}
