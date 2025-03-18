package com.tus.magic.user_manager.models;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;


@Relation(collectionRelation = "users")
public class UserModel extends RepresentationModel<UserModel> {
    private Long id;
    private String username;
    private Role role;

    public UserModel(Long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
}