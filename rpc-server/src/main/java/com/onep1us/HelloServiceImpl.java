package com.onep1us;

import annotation.RpcService;
import api.HelloPara;
import api.HelloService;

/**
 * @author wanjiahao
 */
@RpcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello" + name;
    }

    @Override
    public String hello() {
        return "hello";
    }

    @Override
    public String hello(int a) {
        return "hello1";
    }

    @Override
    public String hello(HelloPara helloPara) {
        return "null";
    }
}
