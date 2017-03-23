package com.coderpage.mine.app.tally.data;

/**
 * @author abner-l. 2017-03-05
 */

public class CategoryItem {

    /** 分类 ID，{@link com.coderpage.mine.app.tally.provider.TallyContract.Category#_ID} */
    private long id;
    /** 分类名称，{@link com.coderpage.mine.app.tally.provider.TallyContract.Category#NAME} */
    private String name;
    /** 分类 */
    private int icon;
    private int order;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
