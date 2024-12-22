package box.bookstorebe.service.common;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.document.common.WebContentDocument;
import box.bookstorebe.dto.common.WebContentDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.common.WebContentMapper;
import box.bookstorebe.model.common.WebContentModel;
import box.bookstorebe.repository.common.image.ImageRepository;
import box.bookstorebe.repository.common.webcontent.WebContentRepository;
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
public class WebContentService {
    private final WebContentRepository webContentRepository;
    private final ImageRepository imageRepository;

    public Page<WebContentDto> getWebContents(Integer page, Integer size) {
        Page<WebContentDocument> webContentDocuments = webContentRepository.getWebContents(page, size);
        List<WebContentDto> content = new ArrayList<>();
        List<String> imageIds = webContentDocuments.getContent().stream().filter(webContentDocument -> webContentDocument.getProperty().equals(Const.WebContentProperty.IMAGE.toString()) && webContentDocument.getValue() != null).map(WebContentDocument::getValue).toList();
        List<ImageDocument> images = imageRepository.findAllById(imageIds);

        for (WebContentDocument webContentDocument : webContentDocuments) {
            WebContentDto webContentDto = WebContentMapper.INSTANCE.entityToDto(webContentDocument);
            if (webContentDocument.getProperty().equals(Const.WebContentProperty.IMAGE.toString()) && webContentDocument.getValue() != null) {
                images.stream().filter(imageDocument -> imageDocument.getId().equals(webContentDocument.getValue())).findFirst().ifPresent(webContentDto::setImage);
            }
            content.add(webContentDto);
        }

        return new PageImpl<>(content, webContentDocuments.getPageable(), webContentDocuments.getTotalElements());
    }

    public WebContentDto getWebContent(String id) throws BizException {
        WebContentDocument webContentDocument = webContentRepository.findById(id).orElseThrow(() -> new BizException("Web content not found"));
        WebContentDto webContentDto =  WebContentMapper.INSTANCE.entityToDto(webContentDocument);
        if(webContentDocument.getProperty().equals(Const.WebContentProperty.IMAGE.toString()) && webContentDocument.getValue() != null) {
            imageRepository.findById(webContentDocument.getValue()).ifPresent(webContentDto::setImage);
        }
        return webContentDto;
    }

    public void createWebContent(WebContentModel webContentModel) throws BizException {
        WebContentDocument webContentDocument = new WebContentDocument();
        webContentDocument.setKey(webContentModel.getKey());
        webContentDocument.setTitle(webContentModel.getTitle());
        webContentDocument.setProperty(webContentModel.getProperty());
        webContentDocument.setValue(webContentModel.getValue());
        webContentDocument.setCreatedAt(ZonedDateTime.now());
        webContentDocument.setUpdatedAt(ZonedDateTime.now());
        webContentRepository.save(webContentDocument);
    }

    public void updateWebContent(String id, WebContentModel webContentModel) throws BizException {
        WebContentDocument webContentDocument = webContentRepository.findById(id).orElseThrow(() -> new BizException("Web content not found"));
        webContentDocument.setKey(webContentModel.getKey());
        webContentDocument.setTitle(webContentModel.getTitle());
        webContentDocument.setProperty(webContentModel.getProperty());
        webContentDocument.setValue(webContentModel.getValue());
        webContentDocument.setUpdatedAt(ZonedDateTime.now());
        webContentRepository.save(webContentDocument);
    }

    public void deleteWebContent(String id) throws BizException {
        WebContentDocument webContentDocument = webContentRepository.findById(id).orElseThrow(() -> new BizException("Web content not found"));
        webContentRepository.delete(webContentDocument);
    }

}
