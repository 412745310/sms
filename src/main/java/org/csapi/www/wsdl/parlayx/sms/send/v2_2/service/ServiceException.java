
/**
 * ServiceException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package org.csapi.www.wsdl.parlayx.sms.send.v2_2.service;

public class ServiceException extends java.lang.Exception{

    private static final long serialVersionUID = 1455864733379L;
    
    private org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.ServiceExceptionE faultMessage;

    
        public ServiceException() {
            super("ServiceException");
        }

        public ServiceException(java.lang.String s) {
           super(s);
        }

        public ServiceException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public ServiceException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.ServiceExceptionE msg){
       faultMessage = msg;
    }
    
    public org.csapi.www.wsdl.parlayx.sms.send.v2_2.service.SendSmsServiceStub.ServiceExceptionE getFaultMessage(){
       return faultMessage;
    }
}
    