package team6.iguide;

/***
 iGuide
 Copyright (C) 2015 Cameron Mace

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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