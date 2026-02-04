package gruzomarket.ru.tools.service;

import gruzomarket.ru.tools.dto.CategoryDTO;
import gruzomarket.ru.tools.dto.CategoryGroupDTO;
import gruzomarket.ru.tools.entity.Category;
import gruzomarket.ru.tools.exception.BadRequestException;
import gruzomarket.ru.tools.exception.NotFoundException;
import gruzomarket.ru.tools.mapper.CategoryMapper;
import gruzomarket.ru.tools.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        return categoryMapper.toDTO(category);
    }

    public List<CategoryDTO> findByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> findByNameContaining(String namePart) {
        return categoryRepository.findByNameContainingIgnoreCase(namePart).stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO create(CategoryDTO dto) {
        Category category = categoryMapper.toEntity(dto);
        
        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found with id: " + dto.getParentId()));
            category.setParent(parent);
        }
        
        category = categoryRepository.save(category);
        return categoryMapper.toDTO(category);
    }

    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        
        category.setName(dto.getName());
        
        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found with id: " + dto.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
        
        category = categoryRepository.save(category);
        return categoryMapper.toDTO(category);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        
        if (!category.getChildren().isEmpty()) {
            throw new BadRequestException("Cannot delete category with children. Please delete or move children first.");
        }
        
        categoryRepository.deleteById(id);
    }

    public List<CategoryGroupDTO> getCategoryGroups() {
        List<CategoryGroupDTO> groups = new ArrayList<>();

        // Получаем все категории из базы и мапим в DTO
        Map<Long, CategoryDTO> allCategories = categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDTO) // Используем mapper
                .collect(Collectors.toMap(CategoryDTO::getId, Function.identity()));

        // Двигатель и силовая установка
        CategoryGroupDTO engineGroup = new CategoryGroupDTO();
        engineGroup.setGroupName("Двигатель и силовая установка");
        engineGroup.setCategories(getCategoriesByIds(Arrays.asList(
                22L, 8L, 41L, 13L, 23L, 2L, 34L, 32L, 38L // Замените на реальные ID
        ), allCategories));
        groups.add(engineGroup);

        // Трансмиссия и привод
        CategoryGroupDTO transmissionGroup = new CategoryGroupDTO();
        transmissionGroup.setGroupName("Трансмиссия и привод");
        transmissionGroup.setCategories(getCategoriesByIds(Arrays.asList(
                33L, 17L, 7L, 27L, 6L, 16L, 40L, 42L
        ), allCategories));
        groups.add(transmissionGroup);

        // Ходовая часть и подвеска
        CategoryGroupDTO chassisGroup = new CategoryGroupDTO();
        chassisGroup.setGroupName("Ходовая часть и подвеска");
        chassisGroup.setCategories(getCategoriesByIds(Arrays.asList(
                1L, 25L, 30L, 37L, 3L, 21L, 12L
        ), allCategories));
        groups.add(chassisGroup);

        // Рулевое управление
        CategoryGroupDTO steeringGroup = new CategoryGroupDTO();
        steeringGroup.setGroupName("Рулевое управление");
        steeringGroup.setCategories(getCategoriesByIds(Arrays.asList(
                29L, 19L
        ), allCategories));
        groups.add(steeringGroup);

        // Тормозная система
        CategoryGroupDTO brakesGroup = new CategoryGroupDTO();
        brakesGroup.setGroupName("Тормозная система");
        brakesGroup.setCategories(getCategoriesByIds(Arrays.asList(
                35L, 5L
        ), allCategories));
        groups.add(brakesGroup);

        // Кузов и электроника
        CategoryGroupDTO bodyGroup = new CategoryGroupDTO();
        bodyGroup.setGroupName("Кузов и электроника");
        bodyGroup.setCategories(getCategoriesByIds(Arrays.asList(
                14L, 18L, 44L, 4L
        ), allCategories));
        groups.add(bodyGroup);

        // Вспомогательные системы
        CategoryGroupDTO auxiliaryGroup = new CategoryGroupDTO();
        auxiliaryGroup.setGroupName("Вспомогательные системы");
        auxiliaryGroup.setCategories(getCategoriesByIds(Arrays.asList(
                36L, 20L, 43L, 39L, 10L
        ), allCategories));
        groups.add(auxiliaryGroup);

        // Ремонт и прочее
        CategoryGroupDTO repairGroup = new CategoryGroupDTO();
        repairGroup.setGroupName("Ремонт и прочее");
        repairGroup.setCategories(getCategoriesByIds(Arrays.asList(
                28L, 24L, 31L, 15L, 11L, 9L
        ), allCategories));
        groups.add(repairGroup);

        return groups;
    }

    private List<CategoryDTO> getCategoriesByIds(List<Long> ids, Map<Long, CategoryDTO> allCategories) {
        return ids.stream()
                .filter(allCategories::containsKey)
                .map(allCategories::get)
                .collect(Collectors.toList());
    }
}

