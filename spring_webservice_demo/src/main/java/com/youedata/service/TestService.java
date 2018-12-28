package com.youedata.service;

import com.youedata.model.User;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author: lyl
 * @date: 2018/5/2 10:19.
 */
@WebService
public interface TestService {
    @WebMethod
    String getName(@WebParam(name = "userId") String userId);
    @WebMethod
    User getUser(String userId);
}
