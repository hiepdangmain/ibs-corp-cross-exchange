package com.msb.ibs.corp.cross.exchange.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "")
public class TtrOrderRequest {
	@Schema(name = "appId", format = "String", description = "Ma ung dung")
	private String appId;
	@Schema(name = "status", format = "String", description = "Trang thai")
	private String status;
	@Schema(name = "channelCode", format = "String", description = "Kenh thuc hien")
	private String channelCode;
}
