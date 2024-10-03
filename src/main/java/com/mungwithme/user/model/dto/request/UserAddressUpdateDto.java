package com.mungwithme.user.model.dto.request;


import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
//@AllArgsConstructor
public class UserAddressUpdateDto {

    // 삭제 주소 ids
    @NotNull(message = "{error.arg}")
    private Set<Long> removeIds;

    // 추가 ids
    @NotNull(message = "{error.arg}")
    private Set<Long> addIds;
}
