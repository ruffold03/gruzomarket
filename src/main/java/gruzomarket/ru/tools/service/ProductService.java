package gruzomarket.ru.tools.service;

import gruzomarket.ru.tools.dto.ProductDTO;
import gruzomarket.ru.tools.dto.ProductSearchResponse;
import gruzomarket.ru.tools.entity.Category;
import gruzomarket.ru.tools.entity.Product;
import gruzomarket.ru.tools.exception.AlreadyExistsException;
import gruzomarket.ru.tools.exception.NotFoundException;
import gruzomarket.ru.tools.mapper.ProductMapper;
import gruzomarket.ru.tools.repository.CategoryRepository;
import gruzomarket.ru.tools.repository.ProductRepository;
import gruzomarket.ru.tools.repository.spec.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        return productMapper.toDTO(product);
    }

    public ProductDTO findByArticle(String article) {
        Product product = productRepository.findByArticle(article)
                .orElseThrow(() -> new NotFoundException("Product not found with article: " + article));
        return productMapper.toDTO(product);
    }

    public List<ProductDTO> findByNameContaining(String namePart) {
        return productRepository.findByNameContainingIgnoreCase(namePart).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductSearchResponse<ProductDTO> search(
            String q,
            List<Long> categoryIds,
            List<Long> brandIds,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            int page,
            int size,
            String sort
    ) {
        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                parseSort(sort)
        );

        Specification<Product> spec = Specification.where(ProductSpecifications.textQuery(q))
                .and(ProductSpecifications.categoryIds(categoryIds))
                .and(ProductSpecifications.brandIds(brandIds))
                .and(ProductSpecifications.priceBetween(minPrice, maxPrice))
                .and(ProductSpecifications.inStock(inStock));

        Page<ProductDTO> dtoPage = productRepository.findAll(spec, pageable).map(productMapper::toDTO);
        return new ProductSearchResponse<>(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getSize(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages(),
                dtoPage.isFirst(),
                dtoPage.isLast()
        );
    }

    private Sort parseSort(String sort) {
        String s = Optional.ofNullable(sort).orElse("name_asc").trim().toLowerCase();
        return switch (s) {
            case "name_desc" -> Sort.by(Sort.Direction.DESC, "name");
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "id_desc" -> Sort.by(Sort.Direction.DESC, "id");
            case "id_asc" -> Sort.by(Sort.Direction.ASC, "id");
            default -> Sort.by(Sort.Direction.ASC, "name");
        };
    }

    public ProductDTO create(ProductDTO dto) {
        if (productRepository.findByArticle(dto.getArticle()).isPresent()) {
            throw new AlreadyExistsException("Product with article " + dto.getArticle() + " already exists");
        }
        
        Product product = productMapper.toEntity(dto);
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + dto.getCategoryId()));
            product.setCategory(category);
        }
        
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    public ProductDTO update(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        
        if (!product.getArticle().equals(dto.getArticle()) 
                && productRepository.findByArticle(dto.getArticle()).isPresent()) {
            throw new AlreadyExistsException("Product with article " + dto.getArticle() + " already exists");
        }
        
        product.setName(dto.getName());
        product.setArticle(dto.getArticle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setOriginalAuto(dto.getOriginalAuto());
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + dto.getCategoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
        
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}

