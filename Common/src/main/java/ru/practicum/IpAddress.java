package ru.practicum;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.lang.annotation.*;


@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = IpAddress.IPV4_REGEX)
@NotNull(message = "Поле IP отсутствует")
public @interface IpAddress
{
    String IPV4_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";

    String message() default "IP адрес не соответствует формату IPv4";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}