package account.user;


import account.security.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Validated
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String lastname;

    @NotEmpty
    @Email(regexp = "\\w+(@acme.com)$")
    private String email;

    @NotEmpty
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String password;

    private boolean enabled;
    private boolean credentialsNonExpired;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private int failedAttempt;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;


    public User() {
        this.enabled = true;
        this.credentialsNonExpired = true;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email.toLowerCase();
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }


    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return getEmail();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public List<Role> getRoles() {
        return roles;
    }

    @JsonIgnore
    public void grantAuthority(Role authority) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(authority);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @JsonIgnore
    public void removeAuthority(Role authority) {
        getAuthorities().remove(authority);
    }

    @JsonIgnore
    public void removeRole(Role role) {
        roles.remove(role);
    }

    @JsonIgnore
    @Override
    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.toString())));
        return authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public void incrementFailedAttempt() {
        this.failedAttempt++;
    }

    public int getFailedAttempts() {
        return failedAttempt;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempt = failedAttempts;
    }
}
