package com.example.barak.fourinarow;

import android.content.Context;
import android.widget.Button;

/**
 * Created by Barak on 13/07/2016.
 */
public class MyButton extends Button {

    private int m_ColIndex;

    public MyButton(Context activity) {
        super(activity);
    }

    public void SetColIndex(int i_ColIndex) {
        m_ColIndex = i_ColIndex;
    }

    public int GetColIndex() {
        return m_ColIndex;
    }
}
