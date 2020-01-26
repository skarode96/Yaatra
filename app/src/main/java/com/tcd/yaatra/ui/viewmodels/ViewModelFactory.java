package com.tcd.yaatra.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {

    private Map<Class, Provider<ViewModel>> viewModelProviderMapping;

    @Inject
    public ViewModelFactory(Map<Class, Provider<ViewModel>> viewModelProviderMapping){
        this.viewModelProviderMapping = viewModelProviderMapping;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Provider provider = this.viewModelProviderMapping.get(modelClass);

        if(provider == null){
            for(Map.Entry<Class, Provider<ViewModel>> entry: this.viewModelProviderMapping.entrySet()){
                if(modelClass.isAssignableFrom(entry.getKey())){
                    provider = entry.getValue();
                }
            }
        }

        if(provider == null){
            throw new RuntimeException("Invalid model class " + modelClass.toString() + ". Did you forgot to add ViewModel binding to ViewModelModule?");
        }

        return ((T) provider.get());
    }
}
