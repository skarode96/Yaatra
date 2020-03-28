package com.tcd.yaatra.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.FragmentSettingsBinding;
import com.tcd.yaatra.repository.datasource.UserInfoRepository;
import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.ui.activities.MenuContainerActivity;
import com.tcd.yaatra.utils.SharedPreferenceUtils;
import com.tcd.yaatra.repository.models.TransportPreference;

import javax.inject.Inject;

public class SettingsFragment extends BaseFragment<FragmentSettingsBinding> {

    SharedPreferences loginPreferences;

    @Inject
    UserInfoRepository userInfoRepository;

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_settings;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.loginPreferences = SharedPreferenceUtils.createLoginSharedPreference();
        TextView userName = (TextView) (this.layoutDataBinding.userName);
        TextView firstName = (TextView) (this.layoutDataBinding.firstName);
        TextView lastName = (TextView) (this.layoutDataBinding.lastName);
        TextView emailId = (TextView) (this.layoutDataBinding.emailId);
        TextView phoneNum = (TextView) (this.layoutDataBinding.phoneNum);
        TextView country = (TextView) (this.layoutDataBinding.country);
        TextView age = (TextView) (this.layoutDataBinding.age);
        TextView rating = (TextView) (this.layoutDataBinding.rating);

        Gender male = Gender.MALE;
        Gender female = Gender.FEMALE;
        Gender noPref = Gender.NOT_SPECIFIED;
        Gender other = Gender.OTHER;

        TransportPreference noTransportPref = TransportPreference.NO_PREFERENCE;
        TransportPreference walk = TransportPreference.WALK;
        TransportPreference taxi = TransportPreference.TAXI;

        userInfoRepository.getUserProfile(SharedPreferenceUtils.getUserName()).observe(getActivity(), response -> {
            userName.setText(response.getUsername());
            firstName.setText(response.getFirstName());
            lastName.setText(response.getLastName());
            emailId.setText(response.getEmail());
            phoneNum.setText(String.valueOf(9878890098l));
            country.setText("Ireland");
            age.setText(String.valueOf(response.getAge()));
            rating.setText(String.valueOf(response.getRating()));

            if(male.idName.equalsIgnoreCase(response.getGender())){
                layoutDataBinding.genderGroup.check(layoutDataBinding.maleBtn.getId());
            }
            else if(female.idName.equalsIgnoreCase(response.getGender())){
                layoutDataBinding.genderGroup.check(layoutDataBinding.femaleBtn.getId());
            }
            else if(other.idName.equalsIgnoreCase(response.getGender())){
                layoutDataBinding.genderGroup.check(layoutDataBinding.otherBtn.getId());
            }

            if(male.idNumber == response.getPref_gender())
            {
                layoutDataBinding.genderPrefGroup.check(layoutDataBinding.malePrefBtn.getId());
            }
            else if(female.idNumber == response.getPref_gender())
            {
                layoutDataBinding.genderPrefGroup.check(layoutDataBinding.femalePrefBtn.getId());
            }
            else if(noPref.idNumber == response.getPref_gender())
            {
                layoutDataBinding.genderPrefGroup.check(layoutDataBinding.noGenderPrefBtn.getId());
            }
            else if(other.idNumber == response.getPref_gender())
            {
                layoutDataBinding.genderPrefGroup.check(layoutDataBinding.otherPrefBtn.getId());
            }

            if(noTransportPref.intValue == response.getPref_mode_travel())
            {
                layoutDataBinding.transportPrefGroup.check(layoutDataBinding.noTransportPrefBtn.getId());
            }
            else if(walk.intValue == response.getPref_mode_travel())
            {
                layoutDataBinding.transportPrefGroup.check(layoutDataBinding.walkPrefBtn.getId());
            }
            else if(taxi.intValue == response.getPref_mode_travel())
            {
                layoutDataBinding.transportPrefGroup.check(layoutDataBinding.taxiPrefBtn.getId());
            }

        });

        ((MenuContainerActivity)getActivity()).layoutDataBinding.toolbar.setTitle("Yaatra Settings");
        return view;
    }

}

