package box.bookstorebe.api.common;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.common.WebContentDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.common.WebContentModel;
import box.bookstorebe.service.common.WebContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/web-contents")
@RequiredArgsConstructor
public class WebContentController {
    private final WebContentService webContentService;

    @GetMapping()
    public BasePagingResponse<WebContentDto> getWebContents(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return new BasePagingResponse<>(webContentService.getWebContents(page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<WebContentDto> getWebContentDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, webContentService.getWebContent(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public BaseResponse<String> createWebContent(@RequestBody @Valid WebContentModel model) throws BizException {
        webContentService.createWebContent(model);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create web content successfully");
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public BaseResponse<String> updateWebContent(@PathVariable String id, @RequestBody @Valid WebContentModel model) throws BizException {
        webContentService.updateWebContent(id, model);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update web content successfully");
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public BaseResponse<String> deleteWebContent(@PathVariable String id) throws BizException {
        webContentService.deleteWebContent(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete web content successfully");
    }
}
