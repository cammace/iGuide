package team6.iguide;

import android.content.SearchRecentSuggestionsProvider;

//TODO fix search suggestion layout
public class SearchSuggestion extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "team6.iguide.SearchSuggestion";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestion() {
        super();
        setupSuggestions(AUTHORITY, MODE);
    }
}