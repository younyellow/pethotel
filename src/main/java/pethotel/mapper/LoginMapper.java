package pethotel.mapper;

import org.apache.ibatis.annotations.Mapper;

import pethotel.dto.LoginDto;
import pethotel.dto.UserDto;

@Mapper
public interface LoginMapper {
	public UserDto login(LoginDto loginDto) throws Exception;

	public void regist(UserDto userDto)throws Exception;

	public UserDto selectUser(UserDto userDto)throws Exception;
	
	
}
