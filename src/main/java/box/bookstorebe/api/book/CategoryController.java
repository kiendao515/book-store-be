package box.bookstorebe.api.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.book.CategoryDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.category.CreateCategoryModel;
import box.bookstorebe.model.book.category.UpdateCategoryModel;
import box.bookstorebe.service.book.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping()
    public BasePagingResponse<CategoryDto> getCategories(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "sort_by", defaultValue = "_id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "DESC") Const.SortDirection orderBy,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return new BasePagingResponse<>(categoryService.getCategories(name, sortBy, orderBy, page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<CategoryDto> getCategoryDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, categoryService.findById(id));
    }

    @PostMapping
    public BaseResponse<String> createCategory(@RequestBody @Valid CreateCategoryModel categoryModel) throws BizException {
        categoryService.createNewCategory(categoryModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new category successfully");
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateCategory(@PathVariable String id, @RequestBody @Valid UpdateCategoryModel categoryModel) throws BizException {
        categoryService.updateCategory(id, categoryModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update category successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteCategory(@PathVariable String id) throws BizException {
        categoryService.deleteCategory(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete category successfully");
    }
}
