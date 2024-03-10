package com.msb.ibs.corp.cross.exchange.application.constants;

import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeUpdateStatusRequest;

public class AppConstant {

	public static final String URI_COMMON = "/api";
	public static final String LANGUAGE_HEADER = "Accept-Language";
	public static final String CHANNEL_HEADER = "Channel";

	public static final String SYSTEM_OK = "200";
	public static final String SYSTEM_PENDING = "202";
	public static final String SYSTEM_TIMEOUT = "408";
	public static final String SYSTEM_ERROR = "500";

	public static final String FORM_GUARANTEE_1 = "1"; // 1. Thư bảo lãnh (Mặc định)
	public static final String FORM_GUARANTEE_2 = "2"; // 2. Hợp đồng bảo lãnh
	public static final String FORM_GUARANTEE_3 = "3"; // 3. Điện swift

	public static final String NOTIFY_MAKER = "maker"; // maker
	public static final String NOTIFY_CHECKER = "checker"; // checker
	public static final String NOTIFY_PARTNER = "3"; // partner
	public static final String TYPE_GUARANTEE_1 = "1"; // "Bảo lãnh dự thầu"
	public static final String TYPE_GUARANTEE_2 = "2"; // "Bảo lãnh thực hiện hợp đồng"
	public static final String TYPE_GUARANTEE_3 = "3"; // "Bảo lãnh thanh toán"
	public static final String TYPE_GUARANTEE_4 = "4"; // "Bảo lãnh tạm ứng"
	public static final String TYPE_GUARANTEE_5 = "5"; // "Bảo lãnh bảo hành"
	public static final String TYPE_GUARANTEE_6 = "6"; // "Bảo lãnh vay vốn"
	public static final String TYPE_GUARANTEE_7 = "7"; // "Bảo lãnh thuế"
	public static final String TYPE_GUARANTEE_8 = "8"; // "Bảo lãnh khác"
	public static final String TYPE_GUARANTEE_9 = "9"; // "Bảo lãnh nhà"

	public static final String GUARANTEE_START_DATE = "1"; // "Ngày hiệu lực "
	public static final String GUARANTEE_START_EVENT = "2"; // Sự kiện bắt đầu hiệu lực
	public static final String GUARANTEE_END_DATE = "1"; // Ngày hết hạn bảo lãnh
	public static final String GUARANTEE_END_EVENT = "2"; // Sự kiện hết hạn bảo lãnh

	public static final String COMMIT_GUARANTEE_1 = "1"; // "Theo mẫu của MSB"
	public static final String COMMIT_GUARANTEE_2 = "2"; // Theo mẫu của các cơ quan nhà nước có thẩm quyền
	public static final String COMMIT_GUARANTEE_3 = "3"; // "Mẫu khác theo yêu cầu"

	public static final String GUARANTEE_FILE_PROPOSAL_FORM = "ReportGuarantee.pdf";

	public static final String REASON_CANCEL_CHECKER_REJECT = "Hủy do khách hàng từ chối phê duyệt yêu cầu chỉnh sửa";
	public static final String REASON_CANCEL_CHECKER_EXPIRED = "Hủy do quá hạn phê duyệt yêu cầu chỉnh sửa";

	public static final class STATUS {
		public static final String DRAF = "DRAF";
		public static final String CKNG = "CKNG";
		public static final String CKRJ = "CKRJ";
		public static final String EXPR = "EXPR";
		public static final String REJE = "REJE"; //MSB tu choi
		public static final String FAIL = "FAIL";
		public static final String PEND = "PEND"; //cho tiep nhan
		public static final String DPEN = "DPEN"; //MSB dang xu ly
		public static final String DSUC = "DSUC"; //thanh cong
		public static final String UPDA = "UPDA"; //yeu cau chinh sua
		public static final String DUPD = "DUPD"; //cho duyet chinh sua
		public static final String NEWR = "NEWR";
		public static final String SUCC = "SUCC";
		public static final String PROC = "PROC"; //dang xu ly
		public static final String CKTM = "CKTM"; //het han cho duyet
		public static final String WCAN = "WCAN"; //cho huy
		public static final String CANC = "CANC"; //da huy
		public static final String DEL = "CANC"; //da huy
	}

	public static final class GUARANTEE_HISTORY {
		public static final String REQUEST_TYPE_NEW = "1"; // mở mới
		public static final String REQUEST_TYPE_UPDATE = "2"; // tu chỉnh
		public static final String ACTIVE = "1"; // hoạt động
		public static final String IN_ACTIVE = "0"; // không hoạt động
	}

	public static final class STATUS_RECORD {
		public static final String NEWR = "NEWR";
		public static final String DPEN = "DPEN";
		public static final String SUCC = "SUCC";
		public static final String FAIL = "FAIL";
		public static final String DLTD = "DLTD"; //da xoa
	}

	public static final class IBS_ADMIN_ACTION {
		public static final String RECEIVE = "RECEIVE"; //tiep nhan
		public static final String REJECT = "REJECT"; //tu choi
		public static final String COMPLETE = "COMPLETE"; //hoan thanh
		public static final String REQ_UPDATE = "REQ_UPDATE"; //yeu cau chinh sua
		public static final String UPDATE = "UPDATE"; //update ma BPM, so bao lanh
		public static final String SEND_TO_MSB = "SEND"; // gui sang MSB
	}

	public static final class MAIL_TEMPLATE {
		public static final String GUARANTEE_PROCESS = "GUARANTEE_PROCESS"; //dang xu ly ho so
		public static final String GUARANTEE_REJECT = "GUARANTEE_REJECT"; //tu choi ho so bao lanh
		public static final String GUARANTEE_UPDATE = "GUARANTEE_UPDATE"; //yeu cau cap nhat ho so bao lanh
		public static final String GUARANTEE_COMPLETE = "GUARANTEE_COMPLETE"; //xu ly thanh cong ho so bao lanh
		public static final String GUARANTEE_TO_MSB_EDIT = "GUARANTEE_TO_MSB_EDIT"; //chuyen ho so chinh sua sang MSB
		public static final String GUARANTEE_TO_MSB = "GUARANTEE_TO_MSB"; //chuyen ho so sang MSB
		public static final String GUARANTEE_SYNC_FAIL = "GUARANTEE_SYNC_FAIL"; //dong bo ho so sang bpm that bai
		public static final String GUARANTEE_CANCEL = "GUARANTEE_CANCEL"; //gui mail khi checker duyet yc chinh sua that bai
		public static final String GUARANTEE_CANCEL_FAIL = "GUARANTEE_CANCEL_FAIL"; //gui mail khi huy sang BPM that bai
	}

	public static final class BB_GUARANTEE_FIELD {
		public static final String applicant = "applicant";
		public static final String certCode = "certCode";
		public static final String certCodeDate = "certCodeDate";
		public static final String certCodePlace = "certCodePlace";
		public static final String formGuarantee = "formGuarantee";
		public static final String swiftCode = "swiftCode";
		public static final String notificationTo = "notificationTo";
		public static final String typeGuarantee = "typeGuarantee";
		public static final String otherGuarantee = "otherGuarantee";
		public static final String beneficiary = "beneficiary";
		public static final String beneficiaryAddress = "beneficiaryAddress";
		public static final String amount = "amount";
		public static final String currency = "currency";
		public static final String guaranteeStart = "guaranteeStart";
		public static final String startDate = "startDate";
		public static final String startEventDate = "startEventDate";
		public static final String guaranteeEnd = "guaranteeEnd";
		public static final String expiredDate = "expiredDate";
		public static final String endEventDate = "endEventDate";
		public static final String expectEndDate = "expectEndDate";
		public static final String formatTextGuarantee = "formatTextGuarantee";
		public static final String commit4 = "commit4";
		public static final String commit2 = "commit2";
		public static final String commit3 = "commit3";
		public static final String commitGuarantee = "commitGuarantee";
		public static final String contractNo = "contractNo";
		public static final String accountPayment = "accountPayment";
		public static final String commitOtherContent = "commitOtherContent";
		public static final String commit2Date = "commit2Date";
	}

	public static final class BPM_ACTION {
		public static final String RECEIVE = "RECEIVE"; //tiep nhan
		public static final String REJECT = "REJECT"; //tu choi
		public static final String COMPLETE = "COMPLETE"; //hoan thanh
		public static final String REQ_UPDATE = "REQ_UPDATE"; //yeu cau chinh sua
		public static final String TRANSFER = "TRANSFER"; //dieu chuyen yeu cau
		public static final String AUTO_CLOSE = "AUTO_CLOSE"; //dong do qua han
	}

}
