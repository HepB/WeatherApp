package ru.lyubimov.weather.weatherapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ru.lyubimov.weather.weatherapp.data.city.CityRepository;
import ru.lyubimov.weather.weatherapp.data.city.pref.EncryptCityPrefRepository;

public class ChangeCityDialogFragment extends DialogFragment {

    public static DialogFragment newInstance() {
        return new ChangeCityDialogFragment();
    }

    private ChangeCityDialogListener listener;
    private CityRepository repo;

    public interface ChangeCityDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        repo = new EncryptCityPrefRepository(Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.city_dialog, null))
                .setPositiveButton(R.string.find, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogPositiveClick(ChangeCityDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNegativeClick(ChangeCityDialogFragment.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        initInputView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ChangeCityDialogListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement DialogInterface.OnClickListener");
        }
    }

    private void initInputView() {
        AutoCompleteTextView input = getDialog().findViewById(R.id.edit_city_name);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setThreshold(1);
        final Set<String> cities = new HashSet<>();
        Disposable disposable = repo.getCities().subscribe(new Consumer<Set<String>>() {
            @Override
            public void accept(Set<String> strings) throws Exception {
                cities.addAll(strings);
            }
        });
        String[] arrCities = new String[cities.size()];
        arrCities = cities.toArray(arrCities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_list_item_1,
                arrCities);
        input.setAdapter(adapter);
        disposable.dispose();
    }
}
