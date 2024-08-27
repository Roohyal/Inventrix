package com.mathias.inventrix.domain.entity;

import com.mathias.inventrix.domain.enums.Position;
import com.mathias.inventrix.domain.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonEntity extends BaseClass implements UserDetails {
    @NotBlank(message = "Your Fullname is required")
    private String fullName;

    @NotBlank(message = "Your email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "PhoneNumber is required")
    private String phoneNumber;

    private String  companyId;

    @NotBlank(message = "Your Company name is required")
    private String companyName;

    @NotBlank(message = "Your Password is needed")
    private String password;

    @Transient
    private String confirmPassword;

    private boolean enabled = false;

    @Enumerated(EnumType.STRING)
    private Position position;

    @Enumerated(EnumType.STRING)
    private Role role;



    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JToken> jtokens;

    @OneToMany(mappedBy = "persons",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfirmationTokenModel> confirmationTokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
