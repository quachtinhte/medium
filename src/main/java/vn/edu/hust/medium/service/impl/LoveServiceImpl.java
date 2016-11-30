package vn.edu.hust.medium.service.impl;

import vn.edu.hust.medium.service.LoveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoveServiceImpl implements LoveService {

    private final Logger log = LoggerFactory.getLogger(LoveServiceImpl.class);

}
