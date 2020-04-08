package com.xyc.jdesapi.util;

import com.xyc.jdesapi.pojo.JDinfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
@Component
public class HtmlParseUitl {

    public List<JDinfo> parseJD(String keywords) throws Exception {
        ArrayList<JDinfo> list=new ArrayList<>();
        String url = "https://search.jd.com/Search?keyword="+keywords;
        //解析网页
        //Document 浏览器对象
        Document document = Jsoup.parse(new URL(url), 3000);
        //获取具体的标签
        Element element = document.getElementById("J_goodsList");
        //获取li
        Elements elements = element.getElementsByTag("li");
        //遍历信息
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("source-data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String name = el.getElementsByClass("p-name").eq(0).text();
            JDinfo dinfo = new JDinfo();
            dinfo.setImg(img);
            dinfo.setPrice(price);
            dinfo.setTitle(name);
            list.add(dinfo);
        }
        return list;
    }
}
