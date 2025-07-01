package com.LsmFiServices.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ESMFlow {

    private static final Logger logger = LoggerFactory.getLogger(ESMFlow.class);

    @Autowired
    private ESMUtils utils;

    public void executeFiServices(String pinstid) {
	boolean flag = utils.getFlagToSetLimit(pinstid);
	if (flag) {

	}

    }

}
