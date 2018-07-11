package helpers;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import ba.com.apdesign.aptours.R;
import models.GenderListView;

public class SpinnerHelper {
    public static ArrayList<GenderListView> populateGender(Context context, Spinner genderSpinner) {
        ArrayList<GenderListView> items = new ArrayList<>();
        items.add(new GenderListView("", context.getString(R.string.ChooseGender)));
        items.add(new GenderListView(context.getString(R.string.MaleShort), context.getString(R.string.Male)));
        items.add(new GenderListView(context.getString(R.string.FemaleShort), context.getString(R.string.Female)));

        ArrayAdapter<GenderListView> arrayAdapter = new ArrayAdapter<GenderListView>(context, android.R.layout.simple_spinner_dropdown_item, items);
        genderSpinner.setAdapter(arrayAdapter);

        return items;
    }
}
