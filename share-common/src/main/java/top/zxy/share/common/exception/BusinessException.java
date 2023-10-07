package top.zxy.share.common.exception;

import aj.org.objectweb.asm.Handle;

public class BusinessException extends RuntimeException{
    private BusinessExceptionEnum e;
    public BusinessException(BusinessExceptionEnum e){
        this.e = e;
    }

    public void setE(BusinessExceptionEnum e){
        this.e = e;
    }

    public BusinessExceptionEnum getE() {
        return e;
    }

}
