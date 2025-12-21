package gruzomarket.ru.tools.service;

import gruzomarket.ru.tools.dto.ProductByBrandDTO;
import gruzomarket.ru.tools.entity.Brand;
import gruzomarket.ru.tools.entity.Product;
import gruzomarket.ru.tools.entity.ProductByBrand;
import gruzomarket.ru.tools.exception.AlreadyExistsException;
import gruzomarket.ru.tools.exception.NotFoundException;
import gruzomarket.ru.tools.mapper.ProductByBrandMapper;
import gruzomarket.ru.tools.repository.BrandRepository;
import gruzomarket.ru.tools.repository.ProductByBrandRepository;
import gruzomarket.ru.tools.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductByBrandService {

    private final ProductByBrandRepository productByBrandRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ProductByBrandMapper productByBrandMapper;

    public List<ProductByBrandDTO> findAll() {
        return productByBrandRepository.findAll().stream()
                .map(productByBrandMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProductByBrandDTO findById(Long productId, Long brandId) {
        ProductByBrand.ProductBrandId id = new ProductByBrand.ProductBrandId();
        id.setProductId(productId);
        id.setBrandId(brandId);
        
        ProductByBrand productByBrand = productByBrandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "ProductByBrand not found with productId: " + productId + " and brandId: " + brandId));
        return productByBrandMapper.toDTO(productByBrand);
    }

    public List<ProductByBrandDTO> findByBrandId(Long brandId) {
        return productByBrandRepository.findByBrandId(brandId).stream()
                .map(productByBrandMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductByBrandDTO> findByProductId(Long productId) {
        return productByBrandRepository.findByProductId(productId).stream()
                .map(productByBrandMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProductByBrandDTO create(ProductByBrandDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + dto.getProductId()));
        
        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new NotFoundException("Brand not found with id: " + dto.getBrandId()));
        
        ProductByBrand.ProductBrandId id = new ProductByBrand.ProductBrandId();
        id.setProductId(dto.getProductId());
        id.setBrandId(dto.getBrandId());
        
        if (productByBrandRepository.existsById(id)) {
            throw new AlreadyExistsException("ProductByBrand already exists with productId: " + dto.getProductId() 
                    + " and brandId: " + dto.getBrandId());
        }
        
        ProductByBrand productByBrand = new ProductByBrand();
        productByBrand.setId(id);
        productByBrand.setProduct(product);
        productByBrand.setBrand(brand);
        
        productByBrand = productByBrandRepository.save(productByBrand);
        return productByBrandMapper.toDTO(productByBrand);
    }

    public void delete(Long productId, Long brandId) {
        ProductByBrand.ProductBrandId id = new ProductByBrand.ProductBrandId();
        id.setProductId(productId);
        id.setBrandId(brandId);
        
        if (!productByBrandRepository.existsById(id)) {
            throw new NotFoundException("ProductByBrand not found with productId: " + productId 
                    + " and brandId: " + brandId);
        }
        
        productByBrandRepository.deleteById(id);
    }
}

