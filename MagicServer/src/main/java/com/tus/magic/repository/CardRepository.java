package com.tus.magic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tus.magic.models.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
	@Query("SELECT c FROM Card c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
	           "OR LOWER(c.typeLine) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
	           "OR LOWER(c.text) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	    List<Card> searchCards(@Param("searchTerm") String searchTerm);
}