package com.tus.magic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tus.magic.models.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
}