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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


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

    // this token is to handle reset password
    private String resetToken;

    // monitor token creation time and expiration time
    private LocalDateTime resetTokenCreationTime;

    private boolean enabled = false;

    @Enumerated(EnumType.STRING)
    private Position position;

    @ManyToOne
    @JoinColumn(name = "created_by_admin_id") // Foreign key to link Employee to Admin
    private PersonEntity createdByAdmin;

    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JToken> jtokens;

    @OneToMany(mappedBy = "persons",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfirmationTokenModel> confirmationTokens;

    @ManyToOne
    @JoinColumn(name = "location_id")  // Foreign key column in Employee table
    private Location location;

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
