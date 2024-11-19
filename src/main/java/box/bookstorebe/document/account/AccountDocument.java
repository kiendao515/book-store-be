package box.bookstorebe.document.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("accounts")
public class AccountDocument implements UserDetails {
    public AccountDocument(String token, String userId) {
        this.token = token;
        this.id = userId;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
    private static final int EXPIRATION = 60 * 24;
    @Id
    private String id;

    @Field(name = "password")
    private String password;

    @Field(name = "email")
    @Indexed(unique = true)
    private String email;

    @Field(name = "enabled")
    private Integer enabled = 0;

    private Role role;

    @Field(name = "salt")
    private String salt;

    @Field(name = "deleted_at")
    private ZonedDateTime deletedAt;

    @Field(name = "created_at")
    private ZonedDateTime createdAt;

    @Field(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Field(name = "token")
    private String token;

    @Field(name = "expiry_date")
    private ZonedDateTime expiryDate;

    private ZonedDateTime calculateExpiryDate(int expiryTimeInMinutes) {
        return ZonedDateTime.now().plusMinutes(expiryTimeInMinutes);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
