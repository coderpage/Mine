package com.coderpage.mine.app.tally.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

/**
 * @author abner-l. 2017-02-12
 * @since 0.1.0
 */

public class TallyUriMatcher {
    private UriMatcher mUriMatcher;
    private SparseArray<TallyUriEnum> mEnumsMap = new SparseArray<>();

    public TallyUriMatcher() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        buildUriMatcher();
    }

    private void buildUriMatcher() {
        final String authority = TallyContract.CONTENT_AUTHORITY;

        TallyUriEnum[] uris = TallyUriEnum.values();
        for (int i = 0; i < uris.length; i++) {
            mUriMatcher.addURI(authority, uris[i].path, uris[i].code);
        }
        buildEnumsMap();
    }

    private void buildEnumsMap() {
        TallyUriEnum[] uris = TallyUriEnum.values();
        for (int i = 0; i < uris.length; i++) {
            mEnumsMap.put(uris[i].code, uris[i]);
        }
    }

    public TallyUriEnum matchUri(Uri uri) {
        final int code = mUriMatcher.match(uri);
        try {
            return matchCode(code);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Unknown uri " + code);
        }
    }

    public TallyUriEnum matchCode(int code) {
        TallyUriEnum actionUriEnum = mEnumsMap.get(code);
        if (actionUriEnum != null) {
            return actionUriEnum;
        } else {
            throw new UnsupportedOperationException("Unknown uri with code " + code);
        }
    }
}
