package box.bookstorebe.api.common;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.common.PersonDocument;
import box.bookstorebe.dto.book.AuthorDto;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.common.PersonDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookrelatedperson.CreateBookRelatedPersonModel;
import box.bookstorebe.model.book.bookrelatedperson.UpdateBookRelatedPersonModel;
import box.bookstorebe.service.common.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/people")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @GetMapping()
    public BasePagingResponse<PersonDto> getBookRelatedPeople(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return new BasePagingResponse<>(personService.getBookRelatedPersons(name, type, page, size));
    }

    @GetMapping("/author")
    public BaseResponse<List<AuthorDto>> getAuthorWithLetter(
            @RequestParam(name = "name", required = false) String name
    ) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, personService.getAuthorWithLetter(name));
    }

    @GetMapping("/books")
    public BaseResponse<List<BookDto>> getBookOfAuthor(
            @RequestParam(name = "authorId", required = true) String authorId
    ) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, personService.getBookOfAuthor(authorId));
    }

    @GetMapping("{id}")
    public BaseResponse<PersonDto> getBookRelatedPersonDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, personService.findById(id));
    }

    @PostMapping
    public BaseResponse<PersonDocument> createNewBookRelatedPerson(@RequestBody @Valid CreateBookRelatedPersonModel bookRelatedPersonModel) throws BizException {
        PersonDocument result = personService.createNewBookRelatedPerson(bookRelatedPersonModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, result, "Create new book related person successfully");
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateBookRelatedPerson(@PathVariable String id, @RequestBody @Valid UpdateBookRelatedPersonModel bookRelatedPersonModel) throws BizException {
        personService.updateBookRelatedPerson(id, bookRelatedPersonModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update book related person successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteBookRelatedPerson(@PathVariable String id) throws BizException {
        personService.deleteBookRelatedPerson(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete book related person successfully");
    }
}
