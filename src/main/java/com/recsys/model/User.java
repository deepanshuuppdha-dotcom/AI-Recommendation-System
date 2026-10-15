package com.recsys.model;

/** Base class for every account. Subclasses decide where they land after login. */
public abstract class User {
    private final int id;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final Role role;

    protected User(int id, String name, String email, String passwordHash, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isAdmin() { return role == Role.ADMIN; }

    /** Polymorphic: each user type has its own home page. */
    public abstract String getHomePath();

    public static User create(int id, String name, String email, String hash, Role role) {
        return role == Role.ADMIN ? new Admin(id, name, email, hash) : new RegularUser(id, name, email, hash);
    }
}
