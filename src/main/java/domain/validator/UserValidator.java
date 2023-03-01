package domain.validator;

import domain.User;
import utils.myFunction;

import java.util.Objects;

public class UserValidator implements Validator<User>{
    @Override
    public void validate(User entity) throws ValidationException {
        if(Objects.equals(entity.getUsername(), "") || Objects.equals(entity.getPassword(),"") || Objects.equals(entity.getName(),"") || Objects.equals(entity.getEmail(), "")){
            throw new ValidationException("Nu pot fi date nule!");
        }
        if(entity.getUsername().contains(" ")){
            throw new ValidationException("Username-ul nu trebuie sa aiba spatii!");
        }
        if(!myFunction.isEmailValid(entity.getEmail())){
            throw new ValidationException("Emailul nu este valid!");
        }
    }
}
