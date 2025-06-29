package com.luna.togetherchat.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.luna.togetherchat.common.enums.BusinessErrorEnum;
import com.luna.togetherchat.common.enums.CommonErrorEnum;
import com.luna.togetherchat.common.enums.ErrorEnum;
import com.luna.togetherchat.common.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.HibernateValidator;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 校验工具类
 */
public class AssertUtil {
    /**
     * 抛出业务异常
     */
    private static void throwException(String msg) {
        throwException(null, msg);
    }

    private static void throwException(ErrorEnum errorEnum, Object... arg) {
        if (Objects.isNull(errorEnum)) {
            errorEnum = BusinessErrorEnum.BUSINESS_ERROR;
        }
        throw new BusinessException(errorEnum.getErrorCode(), MessageFormat.format(errorEnum.getErrorMsg(), arg));
    }

    /**
     * 校验到失败就结束
     */
    private static final Validator failFastValidator =
            Validation.byProvider(HibernateValidator.class)
                    .configure()
                    .failFast(true)
                    .buildValidatorFactory()
                    .getValidator();

    /**
     * 全部校验
     */
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 注解验证参数(校验到失败就结束)
     *
     * @param obj
     */
    public static <T> void fastFailValidate(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = failFastValidator.validate(obj);
        if (!constraintViolations.isEmpty()) {
            throwException(CommonErrorEnum.PARAM_VALID, constraintViolations.iterator().next().getMessage());
        }
    }

    /**
     * 注解验证参数(全部校验,抛出异常)
     *
     * @param obj
     */
    public static <T> void allCheckValidateThrow(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);

        if (!constraintViolations.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder();

            for (ConstraintViolation<T> violation : constraintViolations) {
                //拼接异常信息
                errorMsg.append(violation.getPropertyPath().toString()).append(":").append(violation.getMessage()).append(",");
            }

            //去掉最后一个逗号
            throwException(CommonErrorEnum.PARAM_VALID, errorMsg.toString().substring(0, errorMsg.length() - 1));
        }
    }


    /**
     * 注解验证参数(全部校验,返回异常信息集合)
     *
     * @param obj
     */
    public static <T> Map<String, String> allCheckValidate(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        if (!constraintViolations.isEmpty()) {
            Map<String, String> errorMessages = new HashMap<>();

            for (ConstraintViolation<T> violation : constraintViolations) {
                errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return errorMessages;
        }
        return new HashMap<>();
    }

    private static boolean isEmpty(Object obj) {
        return ObjectUtil.isEmpty(obj);
    }

    //****************************校验方法******************************//

    /**
     * 如果不是true，则抛异常
     * @param expression
     * @param msg
     */
    public static void isTrue(boolean expression, String msg) {
        if (!expression) {
            throwException(msg);
        }
    }

    public static void isTrue(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (!expression) {
            throwException(errorEnum, args);
        }
    }

    /**
     * 如果是true，则抛异常
     * @param expression
     * @param msg
     */
    public static void isFalse(boolean expression, String msg) {
        if (expression) {
            throwException(msg);
        }
    }

    public static void isFalse(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (expression) {
            throwException(errorEnum, args);
        }
    }

    /**
     * 如果不是非空对象，则抛异常
     * @param obj
     * @param msg
     */
    public static void isNotEmpty(Object obj, String msg) {
        if (isEmpty(obj)) {
            throwException(msg);
        }
    }

    public static void isNotEmpty(Object obj, ErrorEnum errorEnum, Object... args) {
        if (isEmpty(obj)) {
            throwException(errorEnum, args);
        }
    }

    /**
     * 如果是非空对象，则抛异常
     * @param obj
     * @param msg
     */
    public static void isEmpty(Object obj, String msg) {
        if (!isEmpty(obj)) {
            throwException(msg);
        }
    }

    public static void isEmpty(Object obj, ErrorEnum errorEnum, Object... args) {
        if (!isEmpty(obj)) {
            throwException(errorEnum, args);
        }
    }

    /**
     * 如果o1和o2不相等，则抛异常
     * @param o1
     * @param o2
     * @param msg
     */
    public static void equal(Object o1, Object o2, String msg) {
        if (!ObjectUtil.equal(o1, o2)) {
            throwException(msg);
        }
    }

    public static void equal(Object o1, Object o2, ErrorEnum errorEnum, Object... args) {
        if (!ObjectUtil.equal(o1, o2)) {
            throwException(errorEnum, args);
        }
    }

    /**
     * 如果o1和o2相等，则抛异常
     * @param o1
     * @param o2
     * @param msg
     */
    public static void notEqual(Object o1, Object o2, String msg) {
        if (ObjectUtil.equal(o1, o2)) {
            throwException(msg);
        }
    }

    public static void notEqual(Object o1, Object o2, ErrorEnum errorEnum, Object... args) {
        if (ObjectUtil.equal(o1, o2)) {
            throwException(errorEnum, args);
        }
    }

    /**
     * 如果object为null，则抛异常
     * @param object
     * @param errorMessage
     */
    public static void isNotNull(Object object, String errorMessage) {
        if (ObjectUtil.isNull(object)) {
            throwException(errorMessage);
        }
    }

    public static void isNotNull(Object object, ErrorEnum errorEnum, Object... args) {
        if (ObjectUtil.isNull(object)) {
            throwException(errorEnum);
        }
    }

    /**
     * 如果object不为null，则抛异常
     * @param object
     * @param errorMessage
     */
    public static void isNull(Object object, String errorMessage) {
        if (ObjectUtil.isNotNull(object)) {
            throwException(errorMessage);
        }
    }

    public static void isNull(Object object, ErrorEnum errorEnum, Object... args) {
        if (ObjectUtil.isNotNull(object)) {
            throwException(errorEnum, args);
        }
    }
}
