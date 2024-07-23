package box.bookstorebe.service.book;

import box.bookstorebe.document.book.CollectionDocument;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.dto.book.CollectionDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.collection.CreateCollectionModel;
import box.bookstorebe.model.book.collection.UpdateCollectionModel;
import box.bookstorebe.repository.book.CollectionRepository;
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
public class CollectionService {
    private final ImageRepository imageRepository;
    private final CollectionRepository collectionRepository;

    public Page<CollectionDto> getCollections(String name, Integer page, Integer size) {
        Page<CollectionDocument> collectionDocuments = collectionRepository.getCollections(name, page, size);

        List<CollectionDto> content = new ArrayList<>();
        for (CollectionDocument collectionDocument : collectionDocuments.getContent()) {
            CollectionDto collectionDto = new CollectionDto();
            collectionDto.setId(collectionDocument.getId());
            collectionDto.setName(collectionDocument.getName());
            collectionDto.setDescription(collectionDocument.getDescription());
            collectionDto.setCreatedAt(collectionDocument.getCreatedAt());
            collectionDto.setUpdatedAt(collectionDocument.getUpdatedAt());

            ImageDocument imageDocument = imageRepository.findById(collectionDocument.getImageId() != null ? collectionDocument.getImageId() : "").orElseGet(() -> null);
            if (imageDocument != null) {
                CollectionDto.Image image = CollectionDto.Image.builder().id(imageDocument.getId()).link(imageDocument.getLink()).build();
                collectionDto.setImage(image);
            }

            content.add(collectionDto);
        }
        return new PageImpl<>(content, collectionDocuments.getPageable(), collectionDocuments.getTotalElements());
    }

    public CollectionDto findById(String id) throws BizException {
        CollectionDocument collectionDocument = collectionRepository.findById(id).orElseThrow(() -> new BizException("Invalid collection id"));
        ImageDocument imageDocument = imageRepository.findById(collectionDocument.getImageId() != null ? collectionDocument.getImageId() : "").orElseGet(() -> null);
        CollectionDto result = new CollectionDto();
        result.setId(collectionDocument.getId());
        result.setName(collectionDocument.getName());
        result.setDescription(collectionDocument.getDescription());
        result.setCreatedAt(collectionDocument.getCreatedAt());
        result.setUpdatedAt(collectionDocument.getUpdatedAt());
        if (imageDocument != null) {
            CollectionDto.Image image = CollectionDto.Image.builder().id(imageDocument.getId()).link(imageDocument.getLink()).build();
            result.setImage(image);
        }
        return result;
    }

    public void createNewCollection(CreateCollectionModel collectionModel) throws BizException {
        CollectionDocument collectionDocument = new CollectionDocument();
        ImageDocument imageDocument = imageRepository.findById(collectionModel.getImageId() != null ? collectionModel.getImageId() : "").orElseThrow(() -> new BizException("Invalid image id"));
        collectionDocument.setImageId(imageDocument.getId());
        collectionDocument.setName(collectionModel.getName());
        collectionDocument.setDescription(collectionModel.getDescription());
        collectionDocument.setCreatedAt(ZonedDateTime.now());
        collectionDocument.setUpdatedAt(ZonedDateTime.now());
        collectionRepository.save(collectionDocument);
    }

    public void updateCollection(String id, UpdateCollectionModel collectionModel) throws BizException {
        CollectionDocument collectionDocument = collectionRepository.findById(id).orElseThrow(() -> new BizException("Invalid collection id"));
        ImageDocument imageDocument = imageRepository.findById(collectionModel.getImageId() != null ? collectionModel.getImageId() : "").orElseThrow(() -> new BizException("Invalid image id"));
        collectionDocument.setImageId(imageDocument.getId());
        collectionDocument.setName(collectionModel.getName());
        collectionDocument.setDescription(collectionModel.getDescription());
        collectionDocument.setUpdatedAt(ZonedDateTime.now());
        collectionRepository.save(collectionDocument);
    }

    public void deleteCollection(String id) throws BizException {
        collectionRepository.findById(id).orElseThrow(() -> new BizException("Invalid collection id"));
        collectionRepository.deleteById(id);
    }
}
