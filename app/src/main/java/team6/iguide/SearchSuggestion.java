package team6.iguide;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestion extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = SearchSuggestion.class.getName();
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestion() {
        super();
        setupSuggestions(AUTHORITY, MODE);
    }
}