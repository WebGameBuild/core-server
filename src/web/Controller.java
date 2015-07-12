package web;

import web.annotations.Validator;
import web.exceptions.InvalidRequestParamException;

import java.lang.reflect.Method;

public abstract class Controller {

    public void validate(Method action, JsonData params) throws InvalidRequestParamException {
        for (Validator validator : action.getAnnotationsByType(Validator.class)) {
            if (validator.required() && params.get(validator.param()) == null) {
                throw new InvalidRequestParamException("Param " + validator.param() + " can not be empty");
            }
            if (params.get(validator.param()) != null) {
                if(!validator.type().isInstance(params.get(validator.param()))) {
                    throw  new InvalidRequestParamException("Param " + validator.param() + " must be "
                            + validator.type().toString());
                }
            }
        }
    }
}
