package box.bookstorebe.document.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("provinces")
public class ProvinceDocument {
    @Id
    private String id;

    @Field(name = "Code")
    private String code;

    @Field(name = "Name")
    private String name;

    @Field(name = "NameEn")
    private String nameEn;

    @Field(name = "FullName")
    private String fullName;

    @Field(name = "FullNameEn")
    private String fullNameEn;

    @Field(name = "District")
    private List<District> districts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class District {
        @Field(name = "Code")
        private String code;

        @Field(name = "Name")
        private String name;

        @Field(name = "NameEn")
        private String nameEn;

        @Field(name = "FullName")
        private String fullName;

        @Field(name = "FullNameEn")
        private String fullNameEn;

        @Field(name = "Ward")
        private List<Ward> wards;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Ward {
        @Field(name = "Code")
        private String code;

        @Field(name = "Name")
        private String name;

        @Field(name = "NameEn")
        private String nameEn;

        @Field(name = "FullName")
        private String fullName;

        @Field(name = "FullNameEn")
        private String fullNameEn;
    }
}
