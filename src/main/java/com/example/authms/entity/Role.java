package com.example.authms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role")
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private int roleId;

    @Column(name = "name", nullable = false)
    private String name;
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public void removeAllUsersFromRole() {
        if (this.getUsers() != null) {
            List<User> usersInRole = this.getUsers().stream().toList();
            usersInRole.forEach(this::removeUserFromRole);
        }
    }

    public void removeUserFromRole(User user) {
        user.getRoles().remove(this);
        this.getUsers().remove(user);
    }

    public void assignUserToRole(User user) {
        user.getRoles().add(this);
        this.getUsers().add(user);
    }

}
