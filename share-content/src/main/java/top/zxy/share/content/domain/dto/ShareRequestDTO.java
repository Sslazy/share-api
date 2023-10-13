package top.zxy.share.content.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareRequestDTO {
    private Long userId;
    private String author;
    private String title;
    private Boolean isOriginal;
    private Integer price;
    private String downloadUrl;
    private String cover;
    private String summary;

}
