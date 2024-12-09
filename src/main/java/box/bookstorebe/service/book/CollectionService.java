package box.bookstorebe.service.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.CollectionDocument;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.dto.book.CollectionDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.collection.CreateCollectionModel;
import box.bookstorebe.model.book.collection.UpdateCollectionModel;
import box.bookstorebe.repository.book.BookRepository;
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
import java.util.Objects;

@AllArgsConstructor
@Service
@Slf4j
public class CollectionService {
    private final ImageRepository imageRepository;
    private final CollectionRepository collectionRepository;
    private final BookRepository bookRepository;

    public Page<CollectionDto> getCollections(String name, Integer showQuantity, String sortBy, Const.SortDirection orderBy, Integer page, Integer size) {
        Page<CollectionDocument> collectionDocuments = collectionRepository.getCollections(name, showQuantity, sortBy, orderBy,page, size);
        List<String> collectionIds = collectionDocuments.getContent().stream().map(CollectionDocument::getId).filter(Objects::nonNull).toList();
        List<BookDocument> bookDocuments = new ArrayList<>();
        if (showQuantity.equals(1)) {
            bookDocuments = bookRepository.findAllByCollectionIdIn(collectionIds);
        }
        List<CollectionDto> content = new ArrayList<>();
        for (CollectionDocument collectionDocument : collectionDocuments.getContent()) {
            CollectionDto collectionDto = new CollectionDto();
            collectionDto.setId(collectionDocument.getId());
            collectionDto.setName(collectionDocument.getName());
            collectionDto.setDescription(collectionDocument.getDescription());
            collectionDto.setCreatedAt(collectionDocument.getCreatedAt());
            collectionDto.setUpdatedAt(collectionDocument.getUpdatedAt());
            collectionDto.setImage(collectionDocument.getImageUrl());
            collectionDto.setQuantity(bookDocuments.size());
            content.add(collectionDto);
        }
        if (sortBy.equals("name")) {
            content.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
            return new PageImpl<>(content, collectionDocuments.getPageable(), collectionDocuments.getTotalElements());
        }
        if (sortBy.equals("numOfBooks")) {
            content.sort((o1, o2) -> o2.getQuantity().compareTo(o1.getQuantity()));
        }
        return new PageImpl<>(content, collectionDocuments.getPageable(), collectionDocuments.getTotalElements());
    }

    public CollectionDto findById(String id) throws BizException {
        CollectionDocument collectionDocument = collectionRepository.findById(id).orElseThrow(() -> new BizException("Invalid collection id"));
        CollectionDto result = new CollectionDto();
        result.setId(collectionDocument.getId());
        result.setName(collectionDocument.getName());
        result.setDescription(collectionDocument.getDescription());
        result.setCreatedAt(collectionDocument.getCreatedAt());
        result.setUpdatedAt(collectionDocument.getUpdatedAt());
        result.setImage(collectionDocument.getImageUrl());
        return result;
    }

    public void createNewCollection(CreateCollectionModel collectionModel) throws BizException {
        CollectionDocument collectionDocument = new CollectionDocument();
        collectionDocument.setImageUrl(collectionModel.getImage());
        collectionDocument.setName(collectionModel.getName());
        collectionDocument.setDescription(collectionModel.getDescription());
        collectionDocument.setCreatedAt(ZonedDateTime.now());
        collectionDocument.setUpdatedAt(ZonedDateTime.now());
        collectionRepository.save(collectionDocument);
    }

    public void updateCollection(String id, UpdateCollectionModel collectionModel) throws BizException {
        CollectionDocument collectionDocument = collectionRepository.findById(id).orElseThrow(() -> new BizException("Invalid collection id"));
        collectionDocument.setImageUrl(collectionModel.getImage());
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
