package com.danielevans.email;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by evansdb0 on 7/18/16.
 *
 * @author Daniel Evans
 */
public class ImageBot {

    public static String parseLinks(FullMessage m) {
/**
 *  Document doc = null;
 try {
 doc = generateDocument(m);
 } catch (IOException e) {
 e.printStackTrace();
 }
 // message contained html
 if(doc != null) {

 }
 */
        System.out.println(MessageParser.parseEmailAddress(m.getFrom()));
        System.out.println(m.getMessageBody());
        String google = "http://www.images.google.com/search?q=";
        String search = MessageParser.parseNameFromEmail(m) + " logo";
        String charset = "UTF-8";
//        ImageBot 1.0 https://github.com/Blezzoh/My_Sewanee_Messenger
        String userAgent = "Mozilla/5.0";
        Document doc = null;
        try {
//            links = Jsoup.connect(google + URLEncoder.encode(search, charset))
//                    .userAgent(userAgent).get().select(".g>.r>a");
            doc = Jsoup.connect(google + URLEncoder.encode(search, charset))
                    .userAgent(userAgent).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc != null) {
            Elements elements = doc.body().select("*");
            doc = Jsoup.parse(elements.get(1).attr("href"));
            boolean first = true;
            String link = null;
            // search for elements in the DOM that have as e.ownText() == "Images",
            // the second element will always be our link to the image version of our search
            for (Element e : elements) {
                if (e.ownText().equals("Images")) {
                    if (!first)
                        link = e.attr("href");
                    first = false;
                }
            }
            System.out.println(link);
            try {
                doc = Jsoup.connect(link).userAgent(userAgent).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (doc != null) {
                Elements imgs = doc.getAllElements().select("img");
                System.out.println(imgs.size());
                System.out.println(imgs.get(0).attr("src"));
            }
        }
//        String googleImages = "http://www.google.com/search?q=%22Amazon.com+Reviews%22++logo&ie=UTF-8&prmd=ivns&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjFyZCK8v3NAhVH2SYKHcjjDIkQ_AUIBQ";


        /*for (Element element : elements) {
            System.out.println(element.ownText());
        }*/


        /*if (links != null) {
            for (int i = 0; i < links.size(); i++) {
                System.out.println(links.get(i));
            }
        }*/
        //http://www.google.com/search?q=%22Amazon.com+Reviews%22++logo&ie=UTF-8&prmd=ivns&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjFyZCK8v3NAhVH2SYKHcjjDIkQ_AUIBQ
//        System.out.println("http://images.google.com/search?q=%22Amazon.com+Reviews%22+logo&prmd=ivns&tbm=isch&tbo=u&source=univ&sa=X&ved=0ahUKEwjL46TQ8f3NAhXBdSYKHU2YDv4QsAQICw");
        return null;
    }
}