package com.recsys.util;

import com.recsys.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/** Tiny server-side HTML helper: escaping, flash messages and the shared page layout. */
public final class Html {
    private Html() {}

    public static String esc(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (char c : s.toCharArray()) {
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    public static User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (User) s.getAttribute("user");
    }

    /** Stores a one-time message that is shown on the next page rendered. */
    public static void setFlash(HttpServletRequest req, String type, String message) {
        req.getSession(true).setAttribute("flash", new String[]{type, message});
    }

    public static String alert(String type, String message) {
        return "<div class=\"alert alert-" + type + "\" role=\"alert\">" + esc(message) + "</div>";
    }

    /** CSS class that gives each category its own accent colour. */
    public static String catClass(String category) {
        return "cat-" + category.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    public static String page(HttpServletRequest req, String title, String active, String body) {
        String ctx = req.getContextPath();
        User user = currentUser(req);

        String flash = "";
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("flash") instanceof String[] f) {
            flash = alert(f[0], f[1]);
            s.removeAttribute("flash");
        }

        StringBuilder nav = new StringBuilder();
        if (user == null) {
            nav.append(link(ctx, "/login", "Sign in", "login", active));
            nav.append(link(ctx, "/register", "Create account", "register", active));
        } else if (user.isAdmin()) {
            nav.append(link(ctx, "/admin/dashboard", "Overview", "dashboard", active));
            nav.append(link(ctx, "/admin/settings", "Settings", "settings", active));
            nav.append(link(ctx, "/admin/analytics", "Analytics", "analytics", active));
            nav.append(link(ctx, "/admin/users", "Users", "users", active));
        } else {
            nav.append(link(ctx, "/user/dashboard", "For you", "dashboard", active));
            nav.append(link(ctx, "/user/preferences", "Preferences", "preferences", active));
        }

        String account = user == null ? "" :
                "<div class=\"account\"><span>" + esc(user.getName()) + "</span>"
              + "<a href=\"" + ctx + "/logout\">Sign out</a></div>";

        return "<!DOCTYPE html>\n<html lang=\"en\"><head><meta charset=\"UTF-8\">"
             + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
             + "<title>" + esc(title) + " | Shelfwise</title>"
             + "<link rel=\"stylesheet\" href=\"" + ctx + "/css/style.css\"></head><body>"
             + "<header class=\"topbar\"><a class=\"brand\" href=\"" + ctx + "/\">Shelfwise</a>"
             + "<nav>" + nav + "</nav>" + account + "</header>"
             + "<main class=\"container\">" + flash + body + "</main>"
             + "</body></html>";
    }

    private static String link(String ctx, String path, String label, String key, String active) {
        return "<a href=\"" + ctx + path + "\"" + (key.equals(active) ? " class=\"active\" aria-current=\"page\"" : "")
             + ">" + label + "</a>";
    }
}
