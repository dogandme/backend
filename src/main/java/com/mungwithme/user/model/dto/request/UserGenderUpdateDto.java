package com.mungwithme.user.model.dto.request;


import com.mungwithme.user.model.enums.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserGenderUpdateDto {

    @NotNull(message = "{error.arg}")
    private Gender gender;
}
