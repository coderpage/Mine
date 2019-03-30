package com.coderpage.mine.app.tally.module.search;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.common.SimpleCallback;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.sql.TallyDatabase;
import com.coderpage.mine.persistence.database.MineDatabase;
import com.coderpage.mine.persistence.entity.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2019-03-27 23:37
 * @since 0.6.0
 *
 * 搜索页数据
 */

class SearchRepository {
    /**
     * 查询记录
     *
     * @param page     页号
     * @param pageSize 每页数量
     * @param keyWord  关键字
     * @param callback 回调
     */
    void queryRecords(int page, int pageSize, String keyWord, Callback<List<Record>, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            long limit = pageSize;
            long offset = ((page - 1) * pageSize);

            List<Record> recordList = TallyDatabase.getInstance().recordDao()
                    .queryByKeyWord("%" + keyWord + "%", limit, offset);
            callback.success(recordList);
        });
    }

    /**
     * 读取搜索历史
     *
     * @param callback 回调
     */
    void loadSearchHistory(SimpleCallback<List<String>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            String key = "tally_search_history_list";
            KeyValue keyValue = MineDatabase.getInstance().keyValueDao().query(key);

            if (keyValue == null || TextUtils.isEmpty(keyValue.getValue())) {
                MineExecutors.executeOnUiThread(() -> callback.success(new ArrayList<>()));
                return;
            }
            try {
                List<String> list = JSONArray.parseArray(keyValue.getValue(), String.class);
                MineExecutors.executeOnUiThread(() -> callback.success(list));
            } catch (Exception e) {
                MineExecutors.executeOnUiThread(() -> callback.success(new ArrayList<>()));
            }
        });
    }

    /**
     * 保存搜索历史
     *
     * @param callback 回调
     */
    void saveSearchHistory(List<String> list, SimpleCallback<Void> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            String key = "tally_search_history_list";
            String value = JSON.toJSONString(list);

            KeyValue keyValue = new KeyValue(key, value);
            MineDatabase.getInstance().keyValueDao().insert(keyValue);

            MineExecutors.executeOnUiThread(() -> callback.success(null));
        });
    }
}
