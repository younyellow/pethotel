package pethotel.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import pethotel.dto.LoginDto;
import pethotel.dto.UserDto;
import pethotel.service.LoginService;

@Controller
public class LoginController {
	@Autowired
	private LoginService loginService;
	//-----------------------로그인 화면창-------------------------
	@GetMapping("/login.do")
	public String login(HttpSession session) throws Exception {
		if (session.getAttribute("user") == null) {
			return "login.html";
		} else {
			return "redirect:/mainpage";
		}
	}
	

	@PostMapping("/login.do")
	public String login(LoginDto loginDto, HttpSession session,HttpServletRequest request) throws Exception {
		UserDto userDto = loginService.login(loginDto);
		if (userDto == null) {
			session.setAttribute("message", "일치하는 정보가 존재하지 않습니다.");
			return "redirect:login.do";
		} else {
			session.setAttribute("user", userDto);
			String referer = request.getHeader("Referer");
			return "redirect:"+referer;
		}
	}
	//-----------------로그아웃 버튼---------------------------------
	@GetMapping("/logout.do")
	public String logout(HttpSession session) throws Exception {
		session.removeAttribute("user");
		session.invalidate();
		return "redirect:login.do";
	}
	//------------------회원 가입화면-----------------------------------
	@GetMapping("/userregist")
	public ModelAndView regist() throws Exception {
		ModelAndView mv = new ModelAndView("User_regist.html");
		return mv;
	}
	@PostMapping("/userregist.do")
	public String regist(UserDto userDto,HttpSession session) throws Exception {
		UserDto user = loginService.selectUser(userDto);
		if (user == null) {
			loginService.regist(userDto);
			session.setAttribute("message", "정상적으로 처리 되었습니다.");
			return "redirect:/login.do";
		} else {
			session.setAttribute("message", "이미 존재하는 아이디 입니다.");
			return "redirect:userregist";
		}
	}
}