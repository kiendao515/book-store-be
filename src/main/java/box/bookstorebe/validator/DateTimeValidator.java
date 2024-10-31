package box.bookstorebe.validator;

import box.bookstorebe.util.DateTimeUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateTimeValidator implements ConstraintValidator<DateTimeConstraint, String> {
    private String format;

    @Override
    public void initialize(DateTimeConstraint constraintAnnotation) {
        this.format = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            DateTimeUtils.parseDateTime(s, this.format);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
