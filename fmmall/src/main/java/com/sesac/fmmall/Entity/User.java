package com.sesac.fmmall.Entity;

import com.sesac.fmmall.Constant.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`user`")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "login_id", nullable = false, unique = true, length = 30)
    private String loginId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "user_name", nullable = false, length = 30)
    private String userName;

    @Column(name = "user_phone", nullable = false, length = 20)
    private String userPhone;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PaymentMethod> paymentMethods;

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

}