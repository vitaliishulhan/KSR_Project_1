package extraction;

import java.io.*;
import java.util.ArrayList;


public class FileParser {

    public static ArrayList<String[]> parse(String pathname) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(pathname)), 1024);

        StringBuilder docText = new StringBuilder();

        int i;

        while( (i = bis.read()) != -1) {
            docText.append((char) i);
        }

        ArrayList<String[]> articlesData = new ArrayList<>();

        while (docText.length() != 0) {
            String docTextStr = docText.toString();

            int reutersStartIndex = docTextStr.indexOf("<REUTERS");
            int reutersEndIndex = docTextStr.indexOf("</REUTERS>") + 10;

            String article = docTextStr.substring(reutersStartIndex, reutersEndIndex);

            String[] articleData = getDataFromArticle(article);

            if (articleData != null) {
                articlesData.add(articleData);

                if (articleData[0].equals("STOP")) {
                    break;
                }
            }

            docText.delete(0, reutersEndIndex);
        }

        return articlesData;
    }

    private static String[] getDataFromArticle(String article) {

        String placesTagStr = article.substring(article.indexOf("<PLACES>") + 8, article.indexOf("</PLACES>"));

        int DAmount = getDAmount(placesTagStr);

        if (DAmount != 0 && DAmount != 1)
            return null;

        String mutableArticleStr = new String(article);

        mutableArticleStr = mutableArticleStr.substring(mutableArticleStr.indexOf("NEWID=\"") + 7);

        String id = mutableArticleStr.substring(0, mutableArticleStr.indexOf('"'));

        String place = DAmount == 0 ? placesTagStr : placesTagStr.substring(3, placesTagStr.length() - 4);



        if (!"west-germany,usa,france,uk,canada,japan".contains(place) || place.length() == 0)
            return null;

        int bodyStartIndex = getIndexAfter(mutableArticleStr, "<BODY>");
        int bodyEndIndex = mutableArticleStr.indexOf("</BODY>");

        if (bodyStartIndex == -1) {

            bodyEndIndex = getIndexAfter(mutableArticleStr, "&#3;");

            bodyStartIndex = getIndexAfter(mutableArticleStr, "</DATELINE>");

            if (bodyStartIndex == -1) {

                bodyStartIndex = getIndexAfter(mutableArticleStr, "</TITLE>");

                if(bodyStartIndex == -1) {
                    bodyStartIndex = getIndexAfter(mutableArticleStr, "&#2;");
                }
            }

        }

        String body = mutableArticleStr.substring(bodyStartIndex, bodyEndIndex);

        return new String[] {id, place, body};
    }

    private static int getDAmount(String places) {
        StringBuilder mutablePlaces = new StringBuilder(places);
        int DAmount = 0;

        int dIndex;

        while (( dIndex = mutablePlaces.indexOf("<D>")) != -1) {
            DAmount++;

            mutablePlaces.delete(dIndex, dIndex + 3);
        }

        return DAmount;
    }

    private static int getIndexAfter(String from, String target) {
        int index = from.indexOf(target);
        return index != -1 ? index + target.length() : -1;
    }

    public static void main(String[] args) throws IOException {
        ArrayList<String[]> articlesData = parse("articles/reut2-000.sgm");

        for (String[] articleData: articlesData) {
                System.out.println("ID: " + articleData[0]);
                System.out.println("Place: " + articleData[1]);
                System.out.println("Body:\n" + articleData[2]);
        }
    }
}
