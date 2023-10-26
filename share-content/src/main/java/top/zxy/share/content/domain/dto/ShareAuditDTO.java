package top.zxy.share.content.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.zxy.share.content.domain.enums.AuditStatusEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareAuditDTO {
    private AuditStatusEnum auditStatusEnum;

    private String reasons;

    private Boolean showFlag;
}

