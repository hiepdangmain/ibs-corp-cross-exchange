package com.msb.ibs.corp.cross.exchange.domain.dispatcher;

import com.msb.ibs.common.base.Query;
import com.msb.ibs.corp.cross.exchange.application.request.TtrOrderRequest;
import org.springframework.http.ResponseEntity;

public interface GuranteeCreateService extends Query<TtrOrderRequest, ResponseEntity<?>> {


}
