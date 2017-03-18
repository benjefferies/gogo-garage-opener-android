package uk.echosoft.garage.opener;

public class Language {

    static String convertAdjectiveToOppositeVerb(String adjective) {
        if ("Closed".equals(adjective)) {
            return "Opening";
        } else {
            return "Closing";
        }
    }
}
