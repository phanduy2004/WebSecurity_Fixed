package com.group8.alomilktea.repository;

import com.group8.alomilktea.common.enums.Status;
import com.group8.alomilktea.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByEmail(String email);

	Optional<User> findByUsername(String username);

	@Query("""
		select u from User u where u.username = :name or u.email = :name
	""")
	Optional<User> findByUsernameOrEmail(@Param("name") String name);

	@Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = 4")
	List<User> findAllShippers();

	List<User> findAllByStatus(Status status);


	@Query("""
		select s.shipCid from User u JOIN ShipmentCompany s on u.userId = s.user.userId  where u.email = :email
	""")
	Long findShipCIdByUser(@Param("email") String email);
}