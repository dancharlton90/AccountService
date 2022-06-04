package account.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    public UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getRoles()
        );
    }
}
