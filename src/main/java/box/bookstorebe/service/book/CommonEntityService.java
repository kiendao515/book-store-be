package box.bookstorebe.service.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.common.CommonEntity;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.dto.common.CommonEntityDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.common.CommonEntityMapper;
import box.bookstorebe.model.book.common.CommonEntityModel;
import box.bookstorebe.model.book.common.UpdateCommonEntity;
import box.bookstorebe.repository.book.CommonEntityRepository;
import box.bookstorebe.repository.common.image.ImageRepository;
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
public class CommonEntityService {
    private final CommonEntityRepository commonEntityRepository;
    private final ImageRepository imageRepository;
    public CommonEntity createEntity(CommonEntityModel commonEntityModel) throws BizException {
        if (!Const.CommonEntityType.isValidType(commonEntityModel.getType())) {
            throw new BizException("Invalid type value: " + commonEntityModel.getType());
        }
        CommonEntity commonEntity= new CommonEntity();
        commonEntity.setName(commonEntityModel.getName());
        commonEntity.setDescription(commonEntityModel.getDescription());
        commonEntity.setType(commonEntityModel.getType());
        commonEntity.setThumbnail(commonEntityModel.getThumbnail());
        commonEntity.setCreatedAt(ZonedDateTime.now());
        commonEntity.setUpdatedAt(ZonedDateTime.now());
        commonEntityRepository.save(commonEntity);
        return commonEntity;
    }
    public Page<CommonEntityDto> getEntity(String type, Integer page, Integer size) {
        Page<CommonEntity> commonEntities = commonEntityRepository.getCommonEntity(type, page, size);
        List<CommonEntityDto> content = new ArrayList<>();
        for (CommonEntity commonEntity : commonEntities.getContent()) {
            CommonEntityDto commonEntityDto = CommonEntityMapper.INSTANCE.entityToDto(commonEntity);
            ImageDocument imageDocument = imageRepository.findById(commonEntity.getThumbnail() != null ? commonEntity.getThumbnail() : "").orElseGet(() -> null);
            if (imageDocument != null) {
                CommonEntityDto.Image image = CommonEntityDto.Image.builder().id(imageDocument.getId()).link(imageDocument.getLink()).build();
                commonEntityDto.setImage(image);
            }
            content.add(commonEntityDto);
        }
        return new PageImpl<>(content, commonEntities.getPageable(), commonEntities.getTotalElements());
    }
    public void updateEntity(String id, UpdateCommonEntity commonEntity) throws BizException {
        CommonEntity entity= commonEntityRepository.findById(id).orElseThrow(()-> new BizException("invalid id"));
        entity.setName(commonEntity.getName());
        entity.setUpdatedAt(ZonedDateTime.now());
        entity.setThumbnail(commonEntity.getThumbnail());
        entity.setDescription(commonEntity.getDescription());
        commonEntityRepository.save(entity);
    }
    public void deleteEntity(String id) throws BizException {
        commonEntityRepository.findById(id).orElseThrow(() -> new BizException("Invalid entity id"));
        commonEntityRepository.deleteById(id);
    }

}
