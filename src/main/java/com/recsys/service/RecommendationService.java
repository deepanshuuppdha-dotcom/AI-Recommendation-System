package com.recsys.service;

import com.recsys.dao.*;
import com.recsys.model.AlgorithmSettings;
import com.recsys.model.Item;
import com.recsys.model.ScoredItem;
import com.recsys.strategy.RecommendationContext;
import com.recsys.strategy.RecommendationStrategy;
import com.recsys.strategy.StrategyFactory;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecommendationService {
    private final SettingsDAO settingsDAO = new SettingsDAO();
    private final PreferenceDAO preferenceDAO = new PreferenceDAO();
    private final InteractionDAO interactionDAO = new InteractionDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final RecommendationLogDAO logDAO = new RecommendationLogDAO();

    /** Builds, ranks and logs the top-N recommendations for a user. */
    public List<ScoredItem> recommend(int userId) throws SQLException {
        AlgorithmSettings settings = AlgorithmSettings.fromMap(settingsDAO.getAll());
        Map<String, Double> prefs = preferenceDAO.getPreferences(userId);
        Map<String, Double> behavior = interactionDAO.getBehaviorScores(userId);
        Set<Integer> purchased = interactionDAO.getPurchasedItemIds(userId);
        List<Item> items = itemDAO.findAll();

        RecommendationContext ctx = new RecommendationContext(prefs, behavior, items);
        RecommendationStrategy strategy = StrategyFactory.create(settings.getStrategy());

        List<ScoredItem> ranked = items.stream()
                .filter(i -> !purchased.contains(i.getId()))
                .map(i -> new ScoredItem(i, strategy.score(i, ctx, settings), strategy.explain(i, ctx)))
                .sorted(Comparator.comparingDouble(ScoredItem::getScore).reversed()
                        .thenComparing(Comparator.comparingInt(ScoredItem::getPopularity).reversed()))
                .limit(settings.getTopN())
                .toList();

        logDAO.logShown(userId, ranked);
        return ranked;
    }
}
