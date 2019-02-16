package com.qan.fiction.util.download;

import android.util.Log;
import com.qan.fiction.ui.abs_web_activity.info.CategoryInfo;
import com.qan.fiction.ui.abs_web_activity.info.CommunityInfo;
import com.qan.fiction.ui.abs_web_activity.info.SearchInfo;
import com.qan.fiction.util.web.FormattedNumber;
import com.qan.fiction.util.storage.SerPair;
import com.qan.fiction.util.storage.entries.Entry;
import com.qan.fiction.util.storage.entries.FF_Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.qan.fiction.util.constants.Constants.SELECTED_STRING;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class FF_Extract {

    private static final List<String> genres;

    private static final int RANDOM = 0;

    private static final int STAFF = 1;

    private static final int STORIES = 2;

    private static final int FOLLOWS = 3;

    private static final int CREATE = 4;


    static {
        genres = Arrays.asList("Adventure", "Angst", "Crime", "Drama", "Family",
                "Fantasy", "Friendship", "General", "Horror", "Humor", "Hurt/Comfort",
                "Mystery", "Parody", "Poetry", "Romance", "Sci-Fi", "Spiritual",
                "Supernatural", "Suspense", "Tragedy", "Western");
    }

    public static final String FORMAT_1 = "MM/dd/yy";
    public static final String FORMAT_2 = "MM/dd";

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static Entry extract(Document doc, String site, int id) {
        Element base = doc.getElementById("content_wrapper_inner");
        Entry e = new FF_Entry();
        e.genre = "None";
        e.time = System.currentTimeMillis();
        e.file = site + "_" + id;
        Elements a = base.select("a.xcontrast_txt[href^=/u/]");
        String[] repr = a.get(0).attr("href").replaceAll("/u/(\\d*)/(.*)", "$1 $2").split("\\s");
        e.authorId = Integer.parseInt(repr[0]);
        e.author = repr[1] + '@' + site;
        a = base.select("b.xcontrast_txt");
        e.title = a.get(0).text();
        a = base.select("div.xcontrast_txt");
        e.description = a.get(0).text();
        a = doc.select("#pre_story_links a");
        e.category = a.get(a.size() - 1).text();
        if (e.category.matches(".*Crossover"))
            e.category = e.category.substring(0, e.category.lastIndexOf(" Crossover"));
        String data;
        data = base.select("span.xgray").text();
        e.site = site;
        getTextInfo(e, data);
        return e;
    }

    private static void getTextInfo(Entry e, String data) {
        {
            int start = 2;
            int max = 4;
            for (; start <= max && e.genre.equals("None"); start++) {
                String s = data.split(" - ")[start].replace("/", "");
                for (String name : genres)
                    s = s.replace(name, "");
                if (s.equals(""))
                    e.genre = data.split(" - ")[start];
            }
        }
        e.favorites = extract_int("Favs: ", data, 0);
        e.follows = extract_int("Follows: ", data, 0);
        e.reviews = extract_int("Reviews: ", data, 0);
        e.words = extract_int("Words: ", data, 0);
        e.chapters = extract_int("Chapters: ", data, 1);
        e.publish = extract_date("Published: ", data, -1);
        e.update = extract_date("Updated: ", data, -1);
        e.complete = data.contains("Complete");
    }

    public static int extract_int(String prefix, String data, int defaultValue) {
        if (data.contains(prefix)) {
            String res = text_extract(data, prefix, "([\\d,]*)[\\w\\W]*", "$1").replace(",", "");
            return Integer.parseInt(res);
        }
        return defaultValue;
    }

    public static long extract_date(String prefix, String data, long defaultValue) {

        if (data.contains(prefix)) {
            String res = text_extract(data, prefix, "(.*?) -.*", "$1");
            if (res.contains("ago")) {
                Log.d("DATE_DEBUG", res);
                return System.currentTimeMillis(); //Why bother parsing this ...
            }
            SimpleDateFormat a = new SimpleDateFormat(FORMAT_1);
            SimpleDateFormat b = new SimpleDateFormat(FORMAT_2);
            a.setTimeZone(TimeZone.getDefault());
            b.setTimeZone(TimeZone.getDefault());
            Date dateA = parse(res, a);
            Date dateB = parse(res, b);
            if (dateA != null) {
                return dateA.getTime();
            } else if (dateB != null) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateB);
                calendar.set(Calendar.YEAR, year);
                return calendar.getTime().getTime();
            } else {
            }
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

    private static Date parse(String res, SimpleDateFormat a) {
        try {
            return a.parse(res);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String text_extract(String data, String tag, String regex, String replace) {
        return data.substring(data.indexOf(tag) + tag.length())
                .replaceFirst(regex, replace);
    }

    public static String getImageUrl(Document doc) {
        Elements a = doc.select("#content_wrapper_inner .cimage[data-original]");
        if (a.size() == 0)
            return null;
        return a.get(0).attr("data-original");

    }

    public static ArrayList<CategoryInfo> getCategories(Document doc) {
        Elements e = doc.select("#list_output div");
        ArrayList<CategoryInfo> result = new ArrayList<CategoryInfo>();
        for (int i = 0; i < e.size(); i++) {
            Elements list = e.get(i).children();
            String cat = list.get(0).text();
            String value = list.get(1).text().replaceAll("[\\(\\)]", "");
            String ref = list.get(0).absUrl("href");
            try {
                result.add(new CategoryInfo(cat, value, ref));
            } catch (NumberFormatException ignore) {

            }
        }
        return result;
    }

    public static HashMap<String, ArrayList<SerPair<String, String>>> getFields(Document doc) {
        HashMap<String, ArrayList<SerPair<String, String>>> map = new HashMap<String,
                ArrayList<SerPair<String, String>>>();
        Elements s = doc.select("#content_wrapper_inner select");
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

        return map;
    }

    public static ArrayList<String> getOrder(Document doc) {
        ArrayList<String> order = new ArrayList<String>();
        Elements s = doc.select("#content_wrapper_inner select");
        for (int i = 0; i < s.size(); i++) {
            order.add(s.get(i).attr("name"));
        }

        return order;
    }

    public static ArrayList<Entry> getEntries(Document doc, String category, String site) {
        ArrayList<Entry> s = new ArrayList<Entry>();
        Element a = doc.select("#content_wrapper_inner").get(0);
        Elements b = a.select("div.xgray");
        Elements c = a.select("a.stitle");
        Elements d = a.select("div.z-list a[href^=/u/]");
        Elements f = a.select("a.stitle[href^=/s/]");
        Elements g = a.select("div.z-indent");
        for (int i = 0; i < b.size(); i++) {
            Entry e = new FF_Entry();
            e.category = category;
            e.genre = "None";
            e.site = site;
            e.time = System.currentTimeMillis();
            e.file = site + "_" + f.get(i).attr("href").replaceAll("/s/(\\d*)/.*", "$1");
            Elements elems = g.get(i).children();
            for (int j = 0; j < elems.size(); j++)
                if (elems.get(j).tagName().equalsIgnoreCase("div"))
                    elems.get(j).remove();
            e.description = Jsoup.clean(g.get(i).html(), Whitelist.simpleText());

            getTextInfo(e, b.get(i).text());
            e.title = c.get(i).text();
            String[] repr = d.get(i).attr("href").replaceAll("/u/(\\d*)/(.*)", "$1 $2").split("\\s");
            e.authorId = Integer.parseInt(repr[0]);
            e.author = repr[1] + '@' + site;
            s.add(e);
        }
        return s;
    }

    public static int getTotalEntries(Document doc) {
        Elements a = doc.select("#content_wrapper_inner center");
        if (a.size() > 1) {
            try {
                return FormattedNumber.parseInt(a.get(1).text().trim().split("\\s")[0].replace(",", ""));
            } catch (NumberFormatException ex) {
                return 0;
            }
        } else
            return doc.select("#content_wrapper_inner div.xgray").size();
    }

    public static int getTotalSearchEntries(Document doc) {
        Elements a = doc.select("#content_wrapper_inner center");
        if (a.size() >= 1) {
            try {
                return Integer.parseInt(a.get(0).text().trim().split("\\s")[0].replace(",", ""));
            } catch (NumberFormatException ex) {
                return 0;
            }
        } else
            return 0;
    }

    public static ArrayList<CommunityInfo> getCommunityInfo(Document doc) {
        ArrayList<CommunityInfo> communities = new ArrayList<CommunityInfo>();
        Elements a = doc.select("#content_wrapper_inner");
        Elements b = a.select("div a");
        Elements c = a.select("div.z-indent");
        Elements d = a.select("div.xgray");
        String prefix = a.select("select[name=s] option[selected]").get(0).text();

        int state = 0;
        String[] contains = {"Random", "Staff", "Stories", "Follow", "Create"};
        for (int i = 0; i < contains.length; i++)
            if (prefix.contains(contains[i]))
                state = i;

        for (int i = 0; i < c.size(); i++) {
            CommunityInfo info = new CommunityInfo();
            info.name = b.get(i).text();
            info.url = b.get(i).absUrl("href");
            info.summary = c.get(i).ownText();
            String data = d.get(i).text();
            info.staff = extract_int("Staff: ", data, 0);
            info.archive = extract_int("Archive: ", data, 0);
            info.followers = extract_int("Followers: ", data, 0);
            info.since = extract_date("Since: ", data, "MM-dd-yy", -1);
            info.founder = data.substring(data.lastIndexOf(":") + 2);
            switch (state) {
                case RANDOM:
                    info.result = "by " + info.founder;
                    break;
                case STAFF:
                    info.result = "Staff: " + info.staff;
                    break;
                case STORIES:
                    info.result = "Stories: " + info.archive;
                    break;
                case FOLLOWS:
                    info.result = "Followers: " + info.followers;
                    break;
                default:
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(info.since);
                    calendar.setTimeZone(TimeZone.getDefault());
                    info.result = "Created: " + sdf.format(calendar.getTime());
            }
            communities.add(info);
        }

        return communities;
    }

    /**
     * Gets the communities categories for the given type (Anime, TV, etc.)
     * This will work for Regular/Crossover, but there are speed improvements in the other one
     * since we have only one select statement.
     */
    public static ArrayList<CategoryInfo> getCommunities(Document doc) {
        Elements e = doc.select("#list_output a");
        Elements f = doc.select("#list_output span");
        ArrayList<CategoryInfo> result = new ArrayList<CategoryInfo>();
        for (int i = 0; i < e.size(); i++) {
            String cat = e.get(i).text();
            String value = f.get(i).text().replaceAll("[\\(\\)]", "");
            String ref = e.get(i).absUrl("href");
            result.add(new CategoryInfo(cat, value, ref));
        }
        return result;
    }

    public static ArrayList<String> getExclude(Document doc) {
        Elements e = doc.select("select.filter_select_negative");
        ArrayList<String> elements = new ArrayList<String>();
        for (int i = 0; i < e.size(); i++) {
            elements.add(e.get(i).attr("name"));
        }
        return elements;
    }

    public static ArrayList<SearchInfo> getSearchedInfo(Document doc) {
        ArrayList<SearchInfo> info = new ArrayList<SearchInfo>();
        Elements e = doc.select("#content_wrapper_inner tbody");
        if (e.size() > 2) {
            e = e.get(2).select("tr");
            Elements links = e.select("a");
            int i = 0;
            if (e.size() > 1)
                i = 1;
            for (; i < e.size(); i++) {
                Elements a = e.get(i).children();
                String name = a.get(0).text() + ":";
                String value = a.get(2).text();
                value = value.substring(0, value.length() - 1);
                String ref = links.get(i).attr("href");

                info.add(new SearchInfo(name, value, ref));
            }
        }
        return info;
    }

    public static ArrayList<Entry> getEntries(Document doc, String site) {
        ArrayList<String> s = new ArrayList<String>();
        Elements e = doc.select("#content_wrapper_inner div.xgray");
        for (int i = 0; i < e.size(); i++) {
            String[] split = e.get(i).text().split(" - ");
            if (split[0].equals("Crossover"))
                s.add(split[1].replace(" & ", " + "));
            else
                s.add(split[0]);
        }
        ArrayList<Entry> entries = getEntries(doc, "", site);
        for (int i = 0; i < entries.size(); i++)
            entries.get(i).category = s.get(i);
        return entries;
    }
}
