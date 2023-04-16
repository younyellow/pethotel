package pethotel.service;

import pethotel.dto.LoginDto;
import pethotel.dto.UserDto;

public interface LoginService {
	public UserDto login(LoginDto loginDto) throws Exception;

	 void regist(UserDto userDto)throws Exception ;

	public UserDto selectUser(UserDto userDto)throws Exception;
	
}
