package box.bookstorebe.api.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.book.CollectionDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.collection.CreateCollectionModel;
import box.bookstorebe.model.book.collection.UpdateCollectionModel;
import box.bookstorebe.service.book.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService collectionService;

    @GetMapping()
    public BasePagingResponse<CollectionDto> getCollections(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return new BasePagingResponse<>(collectionService.getCollections(name, page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<CollectionDto> getCollectionDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, collectionService.findById(id));
    }

    @PostMapping
    public BaseResponse<String> createCollection(@RequestBody @Valid CreateCollectionModel collectionModel) throws BizException {
        collectionService.createNewCollection(collectionModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new collection successfully");
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateCollection(@PathVariable String id, @RequestBody @Valid UpdateCollectionModel collectionModel) throws BizException {
        collectionService.updateCollection(id, collectionModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update collection successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteCollection(@PathVariable String id) throws BizException {
        collectionService.deleteCollection(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete collection successfully");
    }
}
