package com.michaelchaplin.spendometer;

public class CategoryIconDataModel {

    String iconName;
    int iconDrawable;
    private int id_;

    public CategoryIconDataModel(String name, int drawable, int id_){

        this.iconName = name;
        this.iconDrawable = drawable;
        this.id_ = id_;
    }

    String getIconName(){
        return iconName;
    }

    int getIconDrawable(){
        return iconDrawable;
    }

    public int getId(){
        return id_;
    }

}
