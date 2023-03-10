package domain.validator;

import domain.User;
import utils.myFunction;

import java.util.Objects;

public class UserValidator implements Validator<User>{
    @Override
    public void validate(User entity) throws ValidationException {
        if(Objects.equals(entity.getUsername(), "") || Objects.equals(entity.getPassword(),"") || Objects.equals(entity.getName(),"") || Objects.equals(entity.getEmail(), "")){
            throw new ValidationException("No empty fields please!");
        }
        if(entity.getUsername().contains(" ")){
            throw new ValidationException("Username can not contain spaces!");
        }
        if(!myFunction.isEmailValid(entity.getEmail())){
            throw new ValidationException("Invalid email!");
        }
    }
}
