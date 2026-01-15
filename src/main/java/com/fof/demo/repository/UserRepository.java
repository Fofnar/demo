package com.fof.demo.repository;
import com.fof.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Optionnel : Spring le détecte même sans cette annotation
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository fournit déjà :
    // save(user), findAll(), findById(id), deleteById(id), etc.
}
