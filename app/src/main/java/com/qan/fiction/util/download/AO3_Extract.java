package com.qan.fiction.util.download;

import com.qan.fiction.ui.abs_web_activity.info.CategoryInfo;
import com.qan.fiction.util.constants.Constants;
import com.qan.fiction.util.storage.SerPair;
import com.qan.fiction.util.storage.entries.AO3_Entry;
import com.qan.fiction.util.storage.entries.Entry;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

import static com.qan.fiction.util.constants.Constants.SELECTED_STRING;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class AO3_Extract {

    private static final ArrayList<SerPair<String, String>> list;

    static {
        list = new ArrayList<SerPair<String, String>>() {{
            add(p("Ratings: All", ""));
            add(p("Teen And Up", "11"));
            add(p("General", "10"));
            add(p("Explicit", "13"));
            add(p("Mature", "12"));
            add(p("Not Rated", "9"));
        }};
    }

    private static SerPair<String, String> p(String a, String b) {
        return new SerPair<String, String>(a, b);
    }

    public static Entry extract(Document doc, String site, int id) {
        Element meta = doc.select(".work.meta.group").get(0);
        Entry e = new AO3_Entry();
        e.site = site;
        e.file = site + "_" + id;
        e.time = System.currentTimeMillis();
        e.genre = "None";
        Element details = doc.select("#workskin").get(0);
        Elements author = details.select("a[rel=author]");
        if (author.size() > 0) {
            e.author = details.select("a[rel=author]").get(0).html() + "@" + site;
            e.authorId = 0;
        } else {
            e.author = "Anonymous" + "@" + site;
            e.authorId = -1;
        }
        e.title = details.select("h2.title").get(0).html();
        Elements elem = details.select("div.summary blockquote");
        e.description = "";
        if (elem.size() > 0)
            e.description = elem.get(0).text();

        Elements b = meta.select("dd.fandom.tags a");
        e.category = "";
        for (int i = 0; i < b.size(); i++) {
            if (i != 0)
                e.category += " + ";
            e.category += b.get(i).text();
        }
        b = meta.select("dd.freeform.tags a");
        if (b.size() != 0)
            e.genre = "";
        for (int i = 0; i < b.size(); i++) {
            if (i != 0)
                e.genre += "/";
            e.genre += b.get(i).text();
        }

        String data = meta.select("dl.stats").text();
        getTextInfo(e, data);

        return e;
    }

    private static void getTextInfo(Entry e, String data) {
        e.favorites = extract_int("Kudos: ", data, 0);
        e.follows = extract_int("Bookmarks: ", data, 0);
        e.reviews = extract_int("Comments: ", data, 0);
        e.words = Integer.parseInt(text_extract(data, "Words: ", "([\\d,]*)[\\w\\W]*", "$1").replace(",", ""));
        e.chapters = Integer.parseInt(text_extract(data, "Chapters: ", "([\\d,]*)[\\w\\W]*", "$1").replace(",", ""));
        e.publish = extract_date("Published: ", data, "yyyy-MM-dd", -1);
        e.update = extract_date("Updated: ", data, "yyyy-MM-dd", -1);
        String res = text_extract(data, "Chapters: ", "([\\d,]*)/([\\d,\\?]*)([\\w\\W])*", "$2").replace(",", "");
        e.complete = !res.equals("?") && Integer.parseInt(res) == e.chapters;
    }

    public static int extract_int(String prefix, String data, int defaultValue) {
        if (data.contains(prefix)) {
            String res = text_extract(data, prefix, "([\\d,]*)[\\w\\W]*", "$1").replace(",", "");
            return Integer.parseInt(res);
        }
        return defaultValue;
    }

    public static long extract_date(String prefix, String data, String format, long defaultValue) {
        if (data.contains(prefix)) {
            String res = text_extract(data, prefix, "([\\d-]*)[\\w\\W]*", "$1");
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(TimeZone.getDefault());
            try {
                return sdf.parse(res).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public static String text_extract(String data, String tag, String regex, String replace) {
        return data.substring(data.indexOf(tag) + tag.length())
                .replaceFirst(regex, replace);
    }

    public static ArrayList<CategoryInfo> getCategories(Document doc) {
        Elements e = doc.select("#main ul.tags > li");
        ArrayList<CategoryInfo> items = new ArrayList<CategoryInfo>();
        for (int i = 0; i < e.size(); i++) {
            Element a = e.get(i).select("a").get(0);
            String name = a.text();
            String value = e.get(i).ownText().replaceAll("[\\(\\)]", "");
            String ref = a.absUrl("href");
            CategoryInfo info = new CategoryInfo(name, value, ref);
            items.add(info);
        }
        return items;
    }

    public static HashMap<String, ArrayList<SerPair<String, String>>> getFields(Document doc) {
        HashMap<String, ArrayList<SerPair<String, String>>> map = new HashMap<String, ArrayList<SerPair<String, String>>>();
        Elements s = doc.select("#main select");
        for (int i = 0; i < s.size(); i++) {
            ArrayList<SerPair<String, String>> keyvals = new ArrayList<SerPair<String, String>>();
            Elements t = s.get(i).select("option");
            for (int j = 0; j < t.size(); j++) {
                String value = t.get(j).attr("value");
                String name = t.get(j).text();
                if (t.get(j).hasAttr("selected"))
                    keyvals.add(new SerPair<String, String>(SELECTED_STRING, t.get(j).text()));
                if (!value.equals("-1") && !name.equals("-"))
                    keyvals.add(new SerPair<String, String>(name, value));
            }
            map.put(s.get(i).attr("name"), keyvals);
        }
        ArrayList<SerPair<String, String>> temp = new ArrayList<SerPair<String, String>>(list);
        map.put("work_search[rating_ids]", temp);
        s = doc.select("dd[id=tag_category_rating] li");
        if (s.select("input[checked]").size() == 1)
            temp.add(p(SELECTED_STRING, s.select("label").text().replaceAll("(.*?)\\s*(?:Audiences)?\\s*\\(\\d+\\)", "$1")));

        return map;
    }

    public static ArrayList<String> getOrder(Document doc) {
        ArrayList<String> order = new ArrayList<String>();
        Elements s = doc.select("#main select");
        for (int i = 0; i < s.size(); i++) {
            order.add(s.get(i).attr("name"));
        }
        order.add("work_search[rating_ids]");

        return order;
    }


    public static ArrayList<Entry> getEntries(Document doc, String category, String site) {

        ArrayList<Entry> list = new ArrayList<Entry>();
        Elements articles = doc.select("li.work");
        Elements date = articles.select("p.datetime");
        for (int i = 0; i < articles.size(); i++) {
            Entry e = new AO3_Entry();
            Element base = articles.get(i);
            Elements a = base.select("h5.fandoms a");
            Elements b = base.select("blockquote.summary");
            Elements c = base.select("h4.heading");
            Elements d = base.select("li.freeforms");
            String data = base.select("dl.stats").text();

            e.site = site;
            e.genre = "None";
            e.time = System.currentTimeMillis();
            e.description = "";
            if (b.size() > 0)
                e.description = b.get(0).text();

            e.title = c.get(0).child(0).text();
            e.file = site + "_" + c.get(0).child(0).attr("href").replaceAll("/works/(\\d*)", "$1");
            if (c.get(0).select("a[rel=author]").size() == 0) {
                e.author = "Anonymous" + "@" + site;
                e.authorId = -1;//Anonymous
            } else {
                e.author = c.get(0).child(1).text() + '@' + site;
                e.authorId = 0;
            }

            e.category = category;
            for (int j = 0; j < a.size(); j++) {
                e.category += " + ";
                e.category += a.get(j).text();
            }

            if (d.size() > 0)
                e.genre = "";
            for (int j = 0; j < d.size(); j++) {
                if (j != 0)
                    e.genre += "/";
                e.genre += d.get(j).text();
            }

            getTextInfo(e, data);
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
            format.setTimeZone(TimeZone.getDefault());
            try {
                e.update = format.parse(date.get(i).text()).getTime();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            list.add(e);
        }
        return list;
    }

    public static int getTotal(Document doc) {
        String data = doc.select("h2.heading").text();
        try {
            return Integer.parseInt(data.replaceAll(".*?(\\d*) Work.*", "$1"));
        } catch (NumberFormatException ex) {
            return doc.select("li[role=article]").size(); //Probably 20
        }
    }

    public static String getPageUrl(Document d, int id, int page) {
        String site = Constants.AO3_S;
        String n = Constants.download.get(site);
        Elements chapters = d.select("#selected_id option");
        if (chapters.size() > 0) {
            String value = chapters.get(page - 1).attr("value");
            return n.replaceFirst("\\?", String.valueOf(id)).replaceFirst("\\?", value);
        } else {
            n = Constants.siteInfo.get(site);
            return n.replaceFirst("\\?", String.valueOf(id));
        }
    }

    public static int getSearchTotal(Document doc) {
        try {
            String data = doc.select("#inner h3.heading").get(0).ownText();
            return Integer.parseInt(data.replaceAll("(\\d*) Found.*", "$1"));
        } catch (Exception ex) {
            return 0;
        }
    }
}
