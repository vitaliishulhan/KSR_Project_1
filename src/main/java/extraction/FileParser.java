package extraction;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class of the articles parser. Offers methods for extracting articles from particular sgml files
 */
public class FileParser {

    /**
     * Parses single file
     *
     * @param pathname path to the file to be parsed
     * @return ArrayList with String[] 3-element array with id, place (i.e. country), body (i.e. article text) correspondingly
     * @throws IOException throws this expection if the file does not exist
     */
    public static ArrayList<String[]> parse(String pathname) throws IOException {

        // Create buffered input stream for the given file
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(pathname), 1024);

        // String builder for easy manipulation on document text
        StringBuilder docText = new StringBuilder();

        // Help variable for getting text from file
        int i;

        // Get text from file and save in string builder
        while( (i = bis.read()) != -1) {
            docText.append((char) i);
        }

        // Collection for saving article data after parsing
        ArrayList<String[]> articlesData = new ArrayList<>();

        ArrayList<Integer> franceids = new ArrayList<>();
        // Loop for extracting data from articles
        // Since document text is cutted after each iteration, its length is condition to stop
        while (docText.length() != 0) {

            // Get string representation  of document text from string builder
            String docTextStr = docText.toString();

            // Get the first and the last indexes of the article
            int reutersStartIndex = docTextStr.indexOf("<REUTERS");
            int reutersEndIndex = docTextStr.indexOf("</REUTERS>") + 10;

            if (reutersStartIndex == -1)
                break;

            // Get this article from document text
            String article = docTextStr.substring(reutersStartIndex, reutersEndIndex);

            // Get data from article
            String[] articleData = getDataFromArticle(article);

            // if article is not out of all conditions, add it to the collection
            if (articleData != null) {
                articlesData.add(articleData);
                if (articleData[1].equals("france")) {
                   franceids.add(Integer.valueOf(articleData[0]));
                }
            }

            // Delete this article from the document text
            docText.delete(0, reutersEndIndex);
        }

        return articlesData;
    }

    /**
     * The main parser function according to functionality. Parse single article.
     *
     * @param article article in the string representation
     * @return String[] 3-element array with id, place (i.e. country), body (i.e. article text) correspondingly or null,
     *if e.g. there are no place
     */

    private static String[] getDataFromArticle(String article) {

        // Get content of the PLACES element
        String placesTagStr = article.substring(article.indexOf("<PLACES>") + 8, article.indexOf("</PLACES>"));

        int DAmount = getDAmount(placesTagStr);

        // If there are more then 1 place, article is out of conditions
        if (DAmount > 1)
            return null;

        // Get a copy of the article text
        String mutableArticleStr = new String(article);

        //Delete all chars to NEWID attribute value
        mutableArticleStr = mutableArticleStr.substring(mutableArticleStr.indexOf("NEWID=\"") + 7);

        // Get this value as a id of the article
        String id = mutableArticleStr.substring(0, mutableArticleStr.indexOf('"'));

        // If there are no D element, it means that there are only one country, so take it
        // If no, t
        String place = DAmount == 0 ? placesTagStr : placesTagStr.substring(3, placesTagStr.length() - 4);

        // If given country is not one of the written below or PLACES element is empty, article is out of condition
        if (!"west-germany,usa,france,uk,canada,japan".contains(place) || place.length() == 0) //TODO usa is missing!
            return null;

        // Take  BODY element start and end index in the article string representation
        int bodyStartIndex = getIndexAfter(mutableArticleStr, "<BODY>");
        int bodyEndIndex = mutableArticleStr.indexOf("</BODY>");

        // If BODY element does not exist...
        if (bodyStartIndex == -1) {

            //... take end index as a specific symbol of "&#3;"...
            bodyEndIndex = getIndexAfter(mutableArticleStr, "&#3;");

            //... and try to get index of the end of the <DATELINE> element
            bodyStartIndex = getIndexAfter(mutableArticleStr, "</DATELINE>");

            // if <DATELINE> also does not exist...
            if (bodyStartIndex == -1) {

                //... try to get index of the end of the <TITLE> element
                bodyStartIndex = getIndexAfter(mutableArticleStr, "</TITLE>");

                // If <TITLE> element does not exist too...
                if(bodyStartIndex == -1) {
                    // Take specific symbol of "&#2;" as a start index
                    bodyStartIndex = getIndexAfter(mutableArticleStr, "&#2;");
                }
            }

        }

        // Take article body
        String body = mutableArticleStr.substring(bodyStartIndex, bodyEndIndex-4);

        String title = "";

        try {
            title = article.substring(getIndexAfter(article, "<TITLE>"), article.indexOf("</TITLE>"));
        } catch (StringIndexOutOfBoundsException ignored) {}

        return new String[] {id, place, title + '\n' + body};
    }

    /**
     * It's used for getting information about how many D elements are in the PLACES element
     * @param places content of the PLACES element in the string representation
     * @return amount of the D elements
     */

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

    /**
     *
     * Return index of from string after the first target occurence
     *
     * @param from string where get the index from
     * @param target string for searching in the from string
     * @return index after the first target occurence
     */

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
