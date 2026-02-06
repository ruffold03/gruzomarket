package gruzomarket.ru.tools.repository;

import gruzomarket.ru.tools.entity.Customer;
import gruzomarket.ru.tools.entity.Favorite;
import gruzomarket.ru.tools.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Найти избранное по пользователю и товару
    Optional<Favorite> findByCustomerAndProduct(Customer customer, Product product);

    // Найти все избранное пользователя
    List<Favorite> findByCustomerOrderByCreatedAtDesc(Customer customer);

    // Проверить, есть ли товар в избранном у пользователя
    boolean existsByCustomerAndProduct(Customer customer, Product product);

    // Количество избранного у пользователя
    long countByCustomer(Customer customer);

    // Удалить все избранное пользователя
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.customer = :customer")
    void deleteAllByCustomer(@Param("customer") Customer customer);

    // Удалить товар из избранного пользователя
    @Modifying
    void deleteByCustomerAndProduct(Customer customer, Product product);

    // Получить ID всех товаров в избранном пользователя
    @Query("SELECT f.product.id FROM Favorite f WHERE f.customer = :customer")
    List<Long> findProductIdsByCustomer(@Param("customer") Customer customer);
}