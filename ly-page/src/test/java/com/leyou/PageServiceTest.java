package com.leyou;

import com.leyou.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LyPageApplication.class)
public class PageServiceTest {
    @Autowired
    private PageService pageService;

    @Test
    public void testcreateStaticPage(){
        pageService.createStaticPage(113L);
    }
}

