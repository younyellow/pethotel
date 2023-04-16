package pethotel.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import pethotel.dto.ApplyDto;
import pethotel.dto.CompanyDto;
import pethotel.dto.ConsultingDto;
import pethotel.dto.ReviewDto;
import pethotel.dto.StarDto;
import pethotel.dto.UserDto;
import pethotel.service.PetHotelService;

@Slf4j
@Controller
public class PetHotelController {
	@Autowired
	private PetHotelService petHotelService;

	// --------------------------qna등록페이지-----------------------
	@GetMapping("/can.register")
	public ModelAndView consulting() throws Exception {
		ModelAndView mv = new ModelAndView("Business_QnA_regist.html");
		return mv;
	}

	@PostMapping("/register/12")
	public String insertConsulting(ConsultingDto consultingDto, HttpSession session) throws Exception {
		UserDto userDto = (UserDto) session.getAttribute("user");
		consultingDto.setConsultingId(userDto.getUserId());
		petHotelService.insertconsulting(consultingDto);
		return ("redirect:/list");
	}

	// ----------------------qna상세 페이지---------------------------------
	@GetMapping("/can.openconsultDetail.do")
	public ModelAndView detail(@RequestParam int consultingIdx, HttpSession session) throws Exception {
		UserDto userDto = (UserDto) session.getAttribute("user"); // 세션에 있는 로그인 정보
		ConsultingDto consultingDto = petHotelService.detail(consultingIdx);
		// consultingIdx값으로 consultingDto채우기
		if (userDto.getUserId().equals(consultingDto.getConsultingId()) || userDto.isUserCompany()) {
			ModelAndView mv = new ModelAndView("Business_answer_content.html");

			mv.addObject("detail", consultingDto);
			return mv;
		} else {
			session.setAttribute("message", "잘못된 접근 입니다.");
			ModelAndView mv = new ModelAndView("redirect:/list");
			return mv;
		}

	}

	@PostMapping("/reply/1234")
	public String insertreply(HttpSession session, ConsultingDto consultingDto) throws Exception {
		UserDto userDto = (UserDto) session.getAttribute("user");
		consultingDto.setConsultingId(userDto.getUserId());
		petHotelService.insertreply(consultingDto);
		return ("redirect:/list");
	}

	// ---------------------qnalist--------------------------------------------
	@GetMapping("/list")
	public ModelAndView consultinglist(
			@RequestParam(value = "currentPage", required = false, defaultValue = "1") int currentPage)
			throws Exception {
		ModelAndView mv = new ModelAndView("Business_QA.html");
		List<ConsultingDto> list = petHotelService.selectConsultingList((currentPage - 1) * 10);
		mv.addObject("list", list);
		mv.addObject("pageCount", Math.ceil(petHotelService.selectConsultingListCount() / 10.0));
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	// --------------------------------업체 등록-----------------------------------------
	@GetMapping("/listcompany")
	public ModelAndView companylist(
			@RequestParam(value = "currentPage", required = false, defaultValue = "1") int currentPage)
			throws Exception {
		ModelAndView mv = new ModelAndView("Company_list.html");
		List<CompanyDto> list = petHotelService.companylist((currentPage - 1) * 4);
		mv.addObject("list", list);
		mv.addObject("pageCount", Math.ceil(petHotelService.selectBoardListCount() / 4.0));
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@GetMapping("/can.company")
	public ModelAndView companyregist(HttpSession session) throws Exception {
		UserDto userDto = (UserDto) session.getAttribute("user");
		if (userDto.isUserCompany()) {
			ModelAndView mv = new ModelAndView("Business_registration.html");
			return mv;
		} else {
			session.setAttribute("message", "사업자 계정이 아닙니다.");
			ModelAndView mv = new ModelAndView("redirect:/listcompany");
			return mv;
		}

	}

	@PostMapping("/company/regist")
	public String insertcompany(@RequestParam("file") MultipartFile file, CompanyDto companydto, HttpSession session)
			throws Exception {
		UserDto userDto = (UserDto) session.getAttribute("user");
		companydto.setCompanyId(userDto.getUserId());
		petHotelService.insertcompany(file, companydto);
		return ("redirect:/listcompany");
	}

	// ---------------------------메인페이지--------------------------
	@GetMapping("/mainpage")
	public ModelAndView mainpage() throws Exception {
		ModelAndView mv = new ModelAndView("mainpage.html");
		return mv;
	}

	// ----------------------회사 리스트---------------------------------

	@GetMapping("/download.do")
	public void downloadFile(@RequestParam int companyIdx, HttpServletResponse response) throws Exception {
		CompanyDto companyDto = petHotelService.onecompany(companyIdx);
		String companyPhoto = companyDto.getCompanyPhoto();

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			response.setHeader("Content-Disposition", "inline;");

			byte[] buf = new byte[1024];
			fis = new FileInputStream(companyPhoto);
			bis = new BufferedInputStream(fis);
			bos = new BufferedOutputStream(response.getOutputStream());
			int read;
			while ((read = bis.read(buf, 0, 1024)) != -1) {
				bos.write(buf, 0, read);
			}
		} finally {
			bos.close();
			bis.close();
			fis.close();
		}
	}

	// -------------------------------------------------------------업체 상세페이지
	@GetMapping("/can.companyDetail.do")
	public ModelAndView companydetail(HttpSession session,@RequestParam int companyIdx) throws Exception {
		CompanyDto companydetail = petHotelService.companydetail(companyIdx);
		ModelAndView mv = new ModelAndView("company_info_detail.html");

		mv.addObject("companydetail", companydetail);

		CompanyDto reviewlist1 = petHotelService.reviewlist1(companyIdx);
		mv.addObject("reviewlist1", reviewlist1);

		List<ReviewDto> reviewlist2 = petHotelService.reviewlist2(companyIdx);
		mv.addObject("reviewlist2", reviewlist2);
		// 별점 불러오는거
		List<StarDto> starDto = petHotelService.star();
		mv.addObject("star", starDto);

		ReviewDto reviewDto = petHotelService.averageStar(companyIdx);
		mv.addObject("averagestar", reviewDto);
		
		UserDto userDto = (UserDto) session.getAttribute("user");
	
		List<ApplyDto> applyDto = petHotelService.applyId(userDto.getUserId());
		mv.addObject("applyDto",applyDto);
	
		return mv;
	}


	@PostMapping("/insertCompanyReview")
	public String insertreview(HttpSession session,ReviewDto reviewDto,@RequestParam int applyIdx) throws Exception {
		UserDto userDto = (UserDto) session.getAttribute("user");

		ReviewDto reviewlist2 = petHotelService.reviewIdx(applyIdx);	
		if(reviewlist2 == null) {
			petHotelService.insertreview(reviewDto);
			return ("redirect:/can.companyDetail.do?companyIdx=" + reviewDto.getCompanyIdx());
		}else {
		
			session.setAttribute("message", "이미 리뷰가 등록된 예약입니다.");
			return ("redirect:/can.companyDetail.do?companyIdx=" + reviewDto.getCompanyIdx());
		}	
		
	
	}

	// --------------------예약 등록----------------------------
	@PostMapping("/apply")
	public String insertapply(ApplyDto applyDto, HttpSession session) throws Exception {

		UserDto userDto = (UserDto) session.getAttribute("user");
		applyDto.setApplyId(userDto.getUserId());
		petHotelService.insertapply(applyDto);
		return ("redirect:/can.applylist?companyIdx=" + applyDto.getCompanyIdx());
	}

	@GetMapping("/can.apply123")
	public ModelAndView displayinsert(@RequestParam int companyIdx, HttpSession session, HttpServletRequest request)
			throws Exception {
		UserDto userDto = (UserDto) session.getAttribute("user");
		if (userDto.isUserCompany()) {
			session.setAttribute("message", "사업자 계정입니다.");
			String referer = request.getHeader("Referer");
			ModelAndView mv = new ModelAndView("redirect:" + referer);
			return mv;
		} else {
			ModelAndView mv = new ModelAndView("apply.html");
			CompanyDto companyDto = petHotelService.displayinsert(companyIdx);
			mv.addObject("displayinsert", companyDto);
			return mv;
		}

	}

	@GetMapping("/can.applylist")
	public ModelAndView applylist(
			@RequestParam(value = "currentPage", required = false, defaultValue = "1") int currentPage,
			@RequestParam int companyIdx, HttpSession session) throws Exception {
		UserDto userDto = (UserDto) session.getAttribute("user");

		if (userDto.isUserCompany()) {
			ModelAndView mv = new ModelAndView("applylist.html");
			List<ApplyDto> list = petHotelService.applylist((currentPage - 1) * 10, companyIdx);
			mv.addObject("companyIdx", companyIdx);
			mv.addObject("applylist", list);
			mv.addObject("pageCount", Math.ceil(petHotelService.selectApplyListCount() / 10.0));
			mv.addObject("currentPage", currentPage);
			return mv;
		} else {
			ModelAndView mv = new ModelAndView("redirect:/userreservation");
			mv.addObject("userId", userDto.getUserId());
			return mv;
		}

	}

	// --------------------예약 확인------------------------
	@GetMapping("/reservation")
	public ModelAndView reservation(@RequestParam int applyIdx) throws Exception {
		ModelAndView mv = new ModelAndView("reservation.html");
		ApplyDto applyDto = petHotelService.reservation(applyIdx);
		mv.addObject("reservation", applyDto);
		return mv;
	}

	@GetMapping("/userreservation")
	public ModelAndView userreservation(HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView("reservation.html");
		UserDto userDto = (UserDto) session.getAttribute("user");
		List<ApplyDto> apply = petHotelService.userreservation(userDto.getUserId());
		mv.addObject("reservation", apply);
		return mv;
	}

}