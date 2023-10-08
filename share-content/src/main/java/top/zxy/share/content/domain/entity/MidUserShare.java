package top.zxy.share.content.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidUserShare {
    private Long id;
    private Long shareId;
    private Long userId;
}
