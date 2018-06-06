package com.example.navigationdrawertest.customInterface;

import com.example.navigationdrawertest.model.Cell;

import android.view.View;
import android.widget.CompoundButton;

public interface CheckedChangeInterface {

	void onCheckedChanged(CompoundButton buttonView, boolean isChecked,
			Cell cell, int x, int y);
}
