package de.email.aux;

import de.email.core.FullMessage;
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

    public static String parseSenderImage(FullMessage m) {
        String google = "http://www.images.google.com/search?q=";
        String search = m.getFromName() + " logo";
        String charset = "UTF-8";
        String userAgent = "Mozilla/5.0";
        Document doc = null;
        try {
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
            try {
                doc = Jsoup.connect(link).userAgent(userAgent).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (doc != null) {
                Elements imgs = doc.getAllElements().select("img");
                return imgs.get(0).attr("src");
            }
        }
        return null;
    }
}