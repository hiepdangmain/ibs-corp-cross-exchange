package com.msb.ibs.corp.cross.exchange.domain.dto;

import com.msb.ibs.common.dispatcher.Trimmable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * TODO: Class description here.
 *
 * @author QuangBD3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Request to create new user info")
public class UserInfo implements Trimmable {
	private Integer userId;

	private String userName;

	private String mobile;

	private String email;

	private String dcType;

	private String loginFlag;
}
