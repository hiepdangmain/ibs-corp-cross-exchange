package com.msb.ibs.corp.cross.exchange.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "BB_GUARANTEE_FILE_ATTACHMENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BbGuaranteeFileAttachment {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_Sequence")
    @SequenceGenerator(name = "id_Sequence", sequenceName = "SEQ_GUARANTEE_FILE_ATTACHMENT_ID", allocationSize = 1)
    private Integer id;

    @Column(name = "attachment_type")
    private String attachmentType;

    @Column(name = "document_id")
    private Integer documentId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "status")
    private String status;

    @Column(name = "path_file")
    private String pathFile;

    @Column(name = "trans_refer")
    private String transRefer;

    @Column(name = "debt")
    private String debt;

    @Column(name = "debt_date")
    private String debtDate;

    @Column(name = "CREATE_BY")
    private Integer createBy;

    @CreationTimestamp
    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "corp_id")
    private Integer corpId;

    @Column(name = "choose")
    private String choose;

    @Column(name = "NUM_UPLOAD")
    private Integer numUpload; //so lan day file len MinIO

    @Column(name = "NEXT_TIME")
    private Date nextTime; //thoi gian tiep theo quet day file len MinIO

    @Column(name = "path_file_local")
    private String pathFileLocal;

    @Column(name = "bucket")
    private String bucket;

    public int compareTo(BbGuaranteeFileAttachment detail1, BbGuaranteeFileAttachment detail2) {
        if (detail1.getDocumentId() == detail2.getDocumentId())
            return 0;
        else if (detail1.getDocumentId() > detail2.getDocumentId())
            return 1;
        else
            return -1;
    }
}
