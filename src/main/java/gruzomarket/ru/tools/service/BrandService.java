package gruzomarket.ru.tools.service;

import gruzomarket.ru.tools.dto.BrandDTO;
import gruzomarket.ru.tools.entity.Brand;
import gruzomarket.ru.tools.exception.AlreadyExistsException;
import gruzomarket.ru.tools.exception.NotFoundException;
import gruzomarket.ru.tools.mapper.BrandMapper;
import gruzomarket.ru.tools.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public List<BrandDTO> findAll() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toDTO)
                .collect(Collectors.toList());
    }

    public BrandDTO findById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found with id: " + id));
        return brandMapper.toDTO(brand);
    }

    public BrandDTO findByName(String name) {
        Brand brand = brandRepository.findByNameIgnoreCase(name);
        if (brand == null) {
            throw new NotFoundException("Brand not found with name: " + name);
        }
        return brandMapper.toDTO(brand);
    }

    public BrandDTO create(BrandDTO dto) {
        if (brandRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new AlreadyExistsException("Brand with name " + dto.getName() + " already exists");
        }
        Brand brand = brandMapper.toEntity(dto);
        // Явно обнуляем ID и productCount для нового бренда
        brand.setId(null);
        brand.setProductCount(null);
        brand = brandRepository.save(brand);
        return brandMapper.toDTO(brand);
    }

    public BrandDTO update(Long id, BrandDTO dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found with id: " + id));

        if (!brand.getName().equalsIgnoreCase(dto.getName())
                && brandRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new AlreadyExistsException("Brand with name " + dto.getName() + " already exists");
        }

        brand.setName(dto.getName());
        brand = brandRepository.save(brand);
        return brandMapper.toDTO(brand);
    }

    public void delete(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new NotFoundException("Brand not found with id: " + id);
        }
        brandRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return brandRepository.existsByNameIgnoreCase(name);
    }
}
