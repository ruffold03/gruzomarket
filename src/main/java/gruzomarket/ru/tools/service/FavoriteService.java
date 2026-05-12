package gruzomarket.ru.tools.service;

import gruzomarket.ru.tools.entity.Customer;
import gruzomarket.ru.tools.entity.Favorite;
import gruzomarket.ru.tools.entity.Product;
import gruzomarket.ru.tools.repository.FavoriteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductService productService;
    private final CustomerService customerService;

    @Transactional
    public void addToFavorites(Long productId, String customerEmail) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        Product product = productService.getProductById(productId);

        // Проверяем, не добавлен ли уже товар
        if (!favoriteRepository.existsByCustomerAndProduct(customer, product)) {
            Favorite favorite = new Favorite();
            favorite.setCustomer(customer);
            favorite.setProduct(product);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void removeFromFavorites(Long productId, String customerEmail) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        Product product = productService.getProductById(productId);
        favoriteRepository.deleteByCustomerAndProduct(customer, product);
    }

    public List<Product> getFavoriteProducts(String customerEmail) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        return favoriteRepository.findByCustomerOrderByCreatedAtDesc(customer)
                .stream()
                .map(Favorite::getProduct)
                .filter(product -> product.getIsVisible()) // Только видимые товары
                .collect(Collectors.toList());
    }

    public boolean isFavorite(Long productId, String customerEmail) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        Product product = productService.getProductById(productId);
        return favoriteRepository.existsByCustomerAndProduct(customer, product);
    }

    public long getFavoriteCount(String customerEmail) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        return favoriteRepository.countByCustomer(customer);
    }

    public List<Long> getFavoriteProductIds(String customerEmail) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        return favoriteRepository.findProductIdsByCustomer(customer);
    }

    @Transactional
    public void clearFavorites(String customerEmail) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        favoriteRepository.deleteAllByCustomer(customer);
    }

    @Transactional
    public boolean toggleFavorite(Long productId, String customerEmail) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        Product product = productService.getProductById(productId);

        Optional<Favorite> existing = favoriteRepository.findByCustomerAndProduct(customer, product);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false; // Удалили из избранного
        } else {
            Favorite favorite = new Favorite();
            favorite.setCustomer(customer);
            favorite.setProduct(product);
            favoriteRepository.save(favorite);
            return true; // Добавили в избранное
        }
    }
}