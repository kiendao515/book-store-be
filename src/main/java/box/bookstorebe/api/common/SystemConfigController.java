package box.bookstorebe.api.common;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.model.common.SystemConfigModel;
import box.bookstorebe.service.common.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/system-config")
@AllArgsConstructor
public class SystemConfigController {
    private final SystemConfigService systemConfigService;

    @Operation(summary = "Update system config")
    @PostMapping("")
    public BaseResponse<String> updateSystemConfig(@RequestBody @Valid SystemConfigModel systemConfigModel) {
        systemConfigService.updateSystemConfig(systemConfigModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update config successfully");
    }

    @Operation(summary = "Delete system config")
    @DeleteMapping("{id}")
    public BaseResponse<String> deleteSystemConfig(@PathVariable String id) {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete config successfully");
    }


}
