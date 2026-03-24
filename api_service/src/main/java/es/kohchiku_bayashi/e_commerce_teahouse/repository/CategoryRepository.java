package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.active = true")
    List<Category> findByActiveTrue();

    Optional<Category> findByName(String name);

    List<Category> findByNameContainingIgnoreCase(String name);

    List<Category> findAllByOrderByNameAsc();
}
