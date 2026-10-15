# Shelfwise: AI Recommendation System (Java only)

Java 17 + Servlets + JDBC + MySQL. The UI is plain HTML and CSS rendered by servlets. No JSP, no JavaScript, no frameworks.
The "AI" is a weighted scoring algorithm written in plain Java.

## Run it
1. Install JDK 17+, Maven, MySQL 8 and **Tomcat 9** (this project uses `javax.servlet`; for Tomcat 10+ change the dependency to `jakarta.servlet-api` and replace `javax.servlet` with `jakarta.servlet` in the imports).
2. Load the schema and sample data: `mysql -u root -p < database/schema.sql`
3. Edit `src/main/resources/db.properties` (MySQL user and password).
4. Build: `mvn clean package`, then copy `target/recsys.war` into Tomcat's `webapps/`.
5. Open `http://localhost:8080/recsys/`

Demo accounts (created automatically on first start):

| Role  | Email             | Password  |
|-------|-------------------|-----------|
| Admin | admin@recsys.com  | Admin@123 |
| User  | user@recsys.com   | User@1234 |

## How the recommendation works
Each item gets a score between 0 and 1:

    score = (w1 * preferenceMatch + w2 * behaviorMatch + w3 * popularity) / (w1 + w2 + w3)

* **preferenceMatch**: the user's 0-5 rating for the item's category, divided by their highest rating.
* **behaviorMatch**: accumulated interaction weights per category (view 1, click 2, like 3, purchase 5), divided by the highest category.
* **popularity**: item popularity divided by the highest popularity.
* `w1, w2, w3`, the strategy (content-based, behavior-based, hybrid) and the list size are set by the admin under **Settings** and read from the `system_settings` table on every request.
* Purchased items are not recommended again. Every list shown is logged, and clicks and purchases update that log, which feeds the admin analytics (CTR, conversion rate, top items, improvement tips).

## Where each marking area lives
| Area | Where to look |
|---|---|
| Problem understanding and design | This README, the layered layout below, `strategy/` and `AnalyticsService.suggest()` |
| Core Java | Inheritance `User` -> `Admin`/`RegularUser`; interface + polymorphism `RecommendationStrategy`; enums; Collections, streams, Comparator in `RecommendationService`; custom exceptions; Singleton `DBConnectionManager`; `Optional`; `switch` expressions |
| JDBC | `dao/`: `PreparedStatement` everywhere, try-with-resources, transactions with commit/rollback (`PreferenceDAO.save`, `SettingsDAO.saveAll`, `InteractionDAO.log`, `RecommendationLogDAO.logShown`), batch inserts, salted password hashes |
| Servlets and web | `servlet/` (`@WebServlet`), `AuthFilter` (role-based access), `HttpSession`, session-id reset on login, Post/Redirect/Get with flash messages, HTML escaping in `Html.esc` |

## Layout
    com.recsys
     ├── model       User, Admin, RegularUser, Item, ScoredItem, AlgorithmSettings, AnalyticsReport, enums
     ├── dao         UserDAO, ItemDAO, PreferenceDAO, InteractionDAO, RecommendationLogDAO, SettingsDAO
     ├── strategy    RecommendationStrategy + Content/Behavior/Hybrid + StrategyFactory
     ├── service     RecommendationService, AuthService, SettingsService, AnalyticsService
     ├── servlet     Login, Register, Logout, Recommendation, Preference, Interaction, AdminDashboard, Settings, Analytics, Users
     ├── filter      AuthFilter
     ├── exception   ValidationException, InvalidSettingException, UserNotFoundException, AuthenticationException
     └── util        DBConnectionManager, PasswordUtil, Html, AppContextListener

## Pages
* **User**: For you (recommendation list with Open / Like / Buy), Preferences.
* **Admin**: Overview, Settings (strategy, weights, list size), Analytics (CSS bar graphs and tips), Users.
