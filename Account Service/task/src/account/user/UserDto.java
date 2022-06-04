package account.user;

import account.security.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class UserDto implements Serializable {
    private final long id;
    @NotEmpty
    private final String name;
    @NotEmpty
    private final String lastname;
    @NotEmpty
    @Email(regexp = "\\w+(@acme.com)$")
    private final String email;
    private final List<Role> roles;

    public UserDto(long id, String name, String lastname, String email, List<Role> roles) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        List<String> rolesToString = new ArrayList<>();
        for (Role role : roles) {
            rolesToString.add(role.toString());
        }
        return rolesToString.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto entity = (UserDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.name, entity.name) &&
                Objects.equals(this.lastname, entity.lastname) &&
                Objects.equals(this.email, entity.email) &&
                Objects.equals(this.roles, entity.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastname, email, roles);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "lastname = " + lastname + ", " +
                "email = " + email + ", " +
                "roles = " + roles + ")";
    }
}
