package com.recsys.strategy;

import com.recsys.model.AlgorithmSettings;
import com.recsys.model.Item;

/** Scores items by the user's past interactions (view=1, click=2, like=3, purchase=5). */
public class BehaviorBasedStrategy implements RecommendationStrategy {
    @Override
    public double score(Item item, RecommendationContext ctx, AlgorithmSettings settings) {
        return ctx.behaviorMatch(item);
    }
}
