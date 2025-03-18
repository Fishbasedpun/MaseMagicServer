package com.tus.magic.user_manager.models;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.tus.magic.user_manager.controller.UserController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

import org.springframework.hateoas.CollectionModel;

@Component
public class UserModelAssembler extends RepresentationModelAssemblerSupport<User, UserModel> {

    public UserModelAssembler() {
        super(UserController.class, UserModel.class);
    }

    @Override
    public UserModel toModel(User user) {
        UserModel userModel = new UserModel(user.getId(), user.getUsername(), user.getRole());

        userModel.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        userModel.add(linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete"));
        userModel.add(linkTo(methodOn(UserController.class).getUserFavorites(user.getUsername())).withRel("favorites"));

        return userModel;
    }

    @Override
    public CollectionModel<UserModel> toCollectionModel(Iterable<? extends User> users) {
        List<UserModel> userModels = ((List<User>) users).stream().map(this::toModel).toList();
        return CollectionModel.of(userModels);
    }
}