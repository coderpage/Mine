package com.coderpage.mine.app.tally.provider;

/**
 * @author abner-l. 2017-02-12
 * @since 0.1.0
 */

 enum TallyUriEnum {

    EXPENSE(100, "expense", TallyContract.Expense.CONTENT_TYPE_ID, false, TallyDatabase.Tables.EXPENSE),
    CATEGORY(200, "category", TallyContract.Category.CONTENT_TYPE_ID, false, TallyDatabase.Tables.CATEGORY);

    public int code;

    /**
     * The path to the {@link android.content.UriMatcher} will use to match. * may be used as a
     * wild card for any text, and # may be used as a wild card for numbers.
     */
    public String path;

    public String contentType;

    public String table;

    TallyUriEnum(int code, String path, String contentTypeId, boolean item, String table) {
        this.code = code;
        this.path = path;
        this.contentType = item ? TallyContract.makeContentItemType(contentTypeId)
                : TallyContract.makeContentType(contentTypeId);
        this.table = table;
    }
}
