package com.mungwithme.marking.model.dto.request;


import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.enums.Visibility;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarkingTestDto {




    private Long id;
    private String region;
    private Double lat;
    private Double lng;
    private String content;
    private Boolean isTempSaved;
    private Boolean isDeleted;
    private Visibility isVisible;
    private Date regDt;
    private Date modDt;
    private String userName;

    private Long likedCount;

}
