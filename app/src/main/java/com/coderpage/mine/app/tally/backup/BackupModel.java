package com.coderpage.mine.app.tally.backup;

import android.support.annotation.Keep;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @author abner-l. 2017-06-01
 * @since 0.4.0
 *
 * 备份文件 JSON 格式。
 */

@Keep
public class BackupModel {

    @JSONField(name = "metadata")
    private BackupModelMetadata metadata;
    @JSONField(name = "category_list")
    private List<BackupModelCategory> categoryList;
    @JSONField(name = "expense_list")
    private List<BackupModelExpense> expenseList;

    public BackupModelMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(BackupModelMetadata metadata) {
        this.metadata = metadata;
    }

    public List<BackupModelCategory> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<BackupModelCategory> categoryList) {
        this.categoryList = categoryList;
    }

    public List<BackupModelExpense> getExpenseList() {
        return expenseList;
    }

    public void setExpenseList(List<BackupModelExpense> expenseList) {
        this.expenseList = expenseList;
    }
}
