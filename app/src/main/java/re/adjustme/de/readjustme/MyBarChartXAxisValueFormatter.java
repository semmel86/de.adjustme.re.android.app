package re.adjustme.de.readjustme;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import re.adjustme.de.readjustme.Bean.LabelData;

/**
 * Created by Stefan on 21.01.2018.
 */


public class MyBarChartXAxisValueFormatter implements IValueFormatter {

    private String[] mValues;

    public MyBarChartXAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // "value" represents the position of the label on the axis (x or y)
        if (entry.getData() instanceof LabelData) {
            LabelData l = (LabelData) entry.getData();
            String description = l.getLabel().getDescription();
            String descriptionSubString = "";
            if (description.contains("&")) {
                descriptionSubString += description.substring(0, description.indexOf("&"));
                descriptionSubString += "\n";
                descriptionSubString += description.substring(description.indexOf("&"), description.length());
                description = descriptionSubString;
            }
            return description;
        }else{
            return mValues[dataSetIndex];
        }
    }
}

