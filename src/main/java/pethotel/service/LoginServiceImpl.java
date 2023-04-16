package pethotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pethotel.dto.LoginDto;
import pethotel.dto.UserDto;
import pethotel.mapper.LoginMapper;

@Service 
public class LoginServiceImpl implements LoginService {

	@Autowired
	private LoginMapper loginMapper;
	
	@Override
	public UserDto login(LoginDto loginDto) throws Exception {
		return loginMapper.login(loginDto);
	}
//로그인 화면 구현
	@Override
	public void regist(UserDto userDto) throws Exception {
		loginMapper.regist(userDto);
	}

	@Override
	public UserDto selectUser(UserDto userDto) throws Exception {
		return loginMapper.selectUser(userDto);
	}

}
