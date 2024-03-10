package com.msb.ibs.corp.cross.exchange.domain.integration.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ConvertTokenInfo {
    private String username;
    private List<String> roles;

    @JsonProperty("token_type")
    @SerializedName("token_type")
    private String tokenType;

    @JsonProperty("access_token")
    @SerializedName("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    @SerializedName("expires_in")
    private Integer expiresIn;

    @JsonProperty("refresh_token")
    @SerializedName("refresh_token")
    private String refreshToken;
}
