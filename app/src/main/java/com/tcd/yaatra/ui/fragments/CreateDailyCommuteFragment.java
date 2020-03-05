package com.tcd.yaatra.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentCreateDailyCommuteBinding;
import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.JourneyFrequency;
import com.tcd.yaatra.repository.models.TransportPreference;
import com.tcd.yaatra.services.api.yaatra.models.CreateDailyCommuteRequestBody;
import com.tcd.yaatra.ui.activities.LoginActivity;
import com.tcd.yaatra.ui.activities.RegisterActivity;
import com.tcd.yaatra.ui.viewmodels.CreateDailyCommuteFragmentViewModel;
import com.tcd.yaatra.ui.viewmodels.RegisterActivityViewModel;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

import java.util.Calendar;

import javax.inject.Inject;

public class CreateDailyCommuteFragment extends BaseFragment<FragmentCreateDailyCommuteBinding> {

    View view;
    SharedPreferences loginPreferences;
    DatePickerDialog picker;
    EditText date;

    @Inject
    CreateDailyCommuteFragmentViewModel createDailyCommuteFragmentViewModel;

    @Override
    public int getFragmentResourceId() {
        return R.layout.fragment_create_daily_commute;
    }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.buttonCreate.setOnClickListener(view -> handleOnCreateDailyButtonClick());
        layoutDataBinding.journeyStartDate.setOnFocusChangeListener((v, hasFocus) -> handleOnSelectDateClick());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        this.loginPreferences = SharedPreferenceUtils.createLoginSharedPreference();
        date = (EditText) (this.layoutDataBinding.journeyStartDate);
        date.setInputType(InputType.TYPE_NULL);
        return view;
    }

    private void handleOnCreateDailyButtonClick() {

        JourneyFrequency daily = JourneyFrequency.DAILY;
        JourneyFrequency weekly = JourneyFrequency.WEEKLY;
        JourneyFrequency weekend = JourneyFrequency.WEEKEND;

        Gender male = Gender.MALE;
        Gender female = Gender.FEMALE;
        Gender noPref = Gender.NOT_SPECIFIED;
        Gender other = Gender.OTHER;

        TransportPreference noTransportPref = TransportPreference.NO_PREFERENCE;
        TransportPreference walk = TransportPreference.WALK;
        TransportPreference taxi = TransportPreference.TAXI;

        if (TextUtils.isEmpty(layoutDataBinding.title.getText().toString())) {
            layoutDataBinding.title.setError("Title is required");
            layoutDataBinding.title.setHint("Please enter title");
            layoutDataBinding.title.requestFocus();
        } else if (TextUtils.isEmpty(layoutDataBinding.journeyStartDate.getText().toString())) {
            layoutDataBinding.journeyStartDate.setError("Journey Start Date is required");
            layoutDataBinding.journeyStartDate.setHint("Please enter journey start date");
            layoutDataBinding.journeyStartDate.requestFocus();
        } else if (TextUtils.isEmpty(layoutDataBinding.journeyStartTime.getText().toString())) {
            layoutDataBinding.journeyStartTime.setError("Journey Start Time is required");
            layoutDataBinding.journeyStartTime.setHint("Please enter journey start time");
            layoutDataBinding.journeyStartTime.requestFocus();
        } else {

            CreateDailyCommuteRequestBody createDailyRequestBody = new CreateDailyCommuteRequestBody();
            createDailyRequestBody.setJourneyTitle(layoutDataBinding.title.getText().toString());
            createDailyRequestBody.setSourceLong(23.66);
            createDailyRequestBody.setSourceLat(23.89);
            createDailyRequestBody.setDestinationLong(57.33);
            createDailyRequestBody.setDestinationLat(56.11);
            createDailyRequestBody.setStartTime(layoutDataBinding.journeyStartDate.getText().toString() + " " + layoutDataBinding.journeyStartTime.getText().toString());
            createDailyRequestBody.setUserId(SharedPreferenceUtils.getUserId());

            RadioButton genderPrefBtn = (RadioButton) view.findViewById(layoutDataBinding.genderPrefGroup.getCheckedRadioButtonId());
            if (genderPrefBtn.getText().toString().equalsIgnoreCase(male.stringLabel))
                createDailyRequestBody.setPrefGender(male.idNumber);
            else if (genderPrefBtn.getText().toString().equalsIgnoreCase(female.stringLabel))
                createDailyRequestBody.setPrefGender(female.idNumber);
            else if (genderPrefBtn.getText().toString().equalsIgnoreCase(other.stringLabel))
                createDailyRequestBody.setPrefGender(other.idNumber);
            else if (genderPrefBtn.getText().toString().equalsIgnoreCase(noPref.stringLabel))
                createDailyRequestBody.setPrefGender(noPref.idNumber);

            RadioButton transportPrefBtn = (RadioButton) view.findViewById(layoutDataBinding.transportPrefGroup.getCheckedRadioButtonId());
            if (transportPrefBtn.getText().toString().equalsIgnoreCase(noTransportPref.stringLabel))
                createDailyRequestBody.setPrefGender(noTransportPref.intValue);
            else if (transportPrefBtn.getText().toString().equalsIgnoreCase(walk.stringLabel))
                createDailyRequestBody.setPrefGender(walk.intValue);
            else if (transportPrefBtn.getText().toString().equalsIgnoreCase(taxi.stringLabel))
                createDailyRequestBody.setPrefGender(taxi.intValue);

            RadioButton journeyFreqBtn = (RadioButton) view.findViewById(layoutDataBinding.journeyFreqGroup.getCheckedRadioButtonId());
            if (journeyFreqBtn.getText().toString().equalsIgnoreCase(daily.stringLabel))
                createDailyRequestBody.setJourneyFrequency(daily.intValue);
            else if (journeyFreqBtn.getText().toString().equalsIgnoreCase(weekly.stringLabel))
                createDailyRequestBody.setJourneyFrequency(weekly.intValue);
            else if (journeyFreqBtn.getText().toString().equalsIgnoreCase(weekend.stringLabel))
                createDailyRequestBody.setJourneyFrequency(weekend.intValue);

            createDailyCommuteFragmentViewModel.createDaily(createDailyRequestBody).observe(this, createDailyCommuteResponse -> {
                switch (createDailyCommuteResponse.getState()) {
                    case LOADING:
                        layoutDataBinding.progressBarOverlay.setVisibility(View.VISIBLE);
                        break;

                    case SUCCESS:
                        layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
//                        Toast.makeText(getActivity(), "daily commute creation successful", Toast.LENGTH_SHORT).show();
//                        Intent myIntent = new Intent(getActivity(), DailyCommuteListActivity.class);
//                        startActivity(myIntent);
                        break;

                    case FAILURE:
                        layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), createDailyCommuteResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
                        break;
                }
            });

        }
    }
    private void handleOnSelectDateClick() {

        if(date.isFocused()) {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }, year, month, day);
            picker.show();
        }
        date.setFocusableInTouchMode(true);
        date.setClickable(true);
    }

}
