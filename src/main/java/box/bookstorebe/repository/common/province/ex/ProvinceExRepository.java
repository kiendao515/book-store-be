package box.bookstorebe.repository.common.province.ex;

import box.bookstorebe.document.common.ProvinceDocument;

import java.util.List;

public interface ProvinceExRepository {
    List<ProvinceDocument> getAllProvinces(String provinceCode, String districtCode, String wardCode);
}
