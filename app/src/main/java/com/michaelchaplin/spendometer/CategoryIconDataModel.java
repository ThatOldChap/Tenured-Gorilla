package com.michaelchaplin.spendometer;

public class CategoryIconDataModel {

    String iconName;
    int iconDrawable;
    int id_;

    public CategoryIconDataModel(String name, int drawable, int id_){

        this.iconName = name;
        this.iconDrawable = drawable;
        this.id_ = id_;
    }

    public String getIconName(){
        return iconName;
    }

    public int getIconDrawable(){
        return iconDrawable;
    }

    public int getId(){
        return id_;
    }

}
