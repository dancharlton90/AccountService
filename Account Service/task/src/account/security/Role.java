package account.security;

public enum Role {
    ROLE_USER("ROLE_USER"),
    ROLE_ACCOUNTANT("ROLE_ACCOUNTANT"),
    ROLE_ADMINISTRATOR("ROLE_ADMINISTRATOR");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }
}
