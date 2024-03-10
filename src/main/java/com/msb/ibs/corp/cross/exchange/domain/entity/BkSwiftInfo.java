package com.msb.ibs.corp.cross.exchange.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BK_SWIFT_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BkSwiftInfo {
    @Id
    @Column(name = "SWIFT_CODE")
    private String swiftCode;

    @Column(name = "SWIFT_NAME")
    private String swiftName;

    @Column(name = "SWIFT_ADD")
    private String swiftAdd;

    @Column(name = "SWIFT_REGION")
    private String swiftRegion;


    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }


    public String getSwiftName() {
        return swiftName;
    }


    public void setSwiftName(String swiftName) {
        this.swiftName = swiftName;
    }

    public String getSwiftAdd() {
        return swiftAdd;
    }

    public void setSwiftAdd(String swiftAdd) {
        this.swiftAdd = swiftAdd;
    }


    public String getSwiftRegion() {
        return swiftRegion;
    }

    public void setSwiftRegion(String swiftRegion) {
        this.swiftRegion = swiftRegion;
    }
}
