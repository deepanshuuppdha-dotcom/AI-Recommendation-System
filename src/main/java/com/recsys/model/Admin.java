package com.recsys.model;

public class Admin extends User {
    public Admin(int id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash, Role.ADMIN);
    }
    @Override public String getHomePath() { return "/admin/dashboard"; }
}
