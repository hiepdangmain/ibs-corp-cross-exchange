package com.msb.ibs.corp.cross.exchange.application.rest;


import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.utils.ResponseUtils;
import com.msb.ibs.corp.cross.exchange.application.request.SwiftInfoRequest;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService.BkSwiftInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "SwiftInfoController Controller")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1")
public class SwiftInfoController {

    private BkSwiftInfoService swiftInfoService;

    public SwiftInfoController(BkSwiftInfoService swiftInfoService) {
        this.swiftInfoService = swiftInfoService;
    }

    @PostMapping(path = "/search-swift")
    @Operation(summary = "Tim kiem swift code")
    public Object searchSwift(@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal, SwiftInfoRequest input) throws LogicException {
        return ResponseUtils.success(swiftInfoService.getSwiftInfo(principal, input));
    }
}
