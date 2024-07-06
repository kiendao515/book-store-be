package box.bookstorebe.service.book;

import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.dto.book.CategoryDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.book.CategoryMapper;
import box.bookstorebe.model.book.category.CreateCategoryModel;
import box.bookstorebe.model.book.category.UpdateCategoryModel;
import box.bookstorebe.repository.book.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Page<CategoryDto> getCategories(String name, Integer page, Integer size) {
        Page<CategoryDocument> categoryDocuments = categoryRepository.getCategories(name, page, size);

        List<CategoryDto> content = new ArrayList<>();
        for (CategoryDocument categoryDocument : categoryDocuments.getContent()) {
            content.add(CategoryMapper.INSTANCE.entityToDto(categoryDocument));
        }
        return new PageImpl<>(content, categoryDocuments.getPageable(), categoryDocuments.getTotalElements());
    }

    public CategoryDto findById(String id) throws BizException {
        CategoryDocument categoryDocument = categoryRepository.findById(id).orElseThrow(() -> new BizException("Invalid category id"));
        return CategoryMapper.INSTANCE.entityToDto(categoryDocument);
    }

    public void createNewCategory(CreateCategoryModel categoryModel) {
        CategoryDocument categoryDocument = new CategoryDocument();
        categoryDocument.setName(categoryModel.getName());
        categoryDocument.setDescription(categoryModel.getDescription());
        categoryDocument.setCreatedAt(ZonedDateTime.now());
        categoryDocument.setUpdatedAt(ZonedDateTime.now());
        categoryRepository.save(categoryDocument);
    }

    public void updateCategory(String id, UpdateCategoryModel updateCategoryModel) throws BizException {
        CategoryDocument categoryDocument = categoryRepository.findById(id).orElseThrow(() -> new BizException("Invalid category id"));
        categoryDocument.setName(updateCategoryModel.getName());
        categoryDocument.setDescription(updateCategoryModel.getDescription());
        categoryDocument.setUpdatedAt(ZonedDateTime.now());
        categoryRepository.save(categoryDocument);
    }

    public void deleteCategory(String id) throws BizException {
        categoryRepository.findById(id).orElseThrow(() -> new BizException("Invalid category id"));
        categoryRepository.deleteById(id);
    }
}
