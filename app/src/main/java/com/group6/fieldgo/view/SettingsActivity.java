package com.group6.fieldgo.view;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.group6.fieldgo.R;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {

    private RadioGroup rgTheme, rgLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load preferences
        SharedPreferences prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        String savedTheme = prefs.getString("theme", "system");
        String savedLang = prefs.getString("language", "vi");

        applyTheme(savedTheme);
        applyLanguage(savedLang);

        setContentView(R.layout.activity_settings);

        rgTheme = findViewById(R.id.rgTheme);
        rgLanguage = findViewById(R.id.rgLanguage);

        // Set checked radio button
        switch (savedTheme) {
            case "light":
                rgTheme.check(R.id.rbLight);
                break;
            case "dark":
                rgTheme.check(R.id.rbDark);
                break;
            default:
                rgTheme.check(R.id.rbSystem);
        }

        if ("vi".equals(savedLang)) {
            rgLanguage.check(R.id.rbVietnamese);
        } else {
            rgLanguage.check(R.id.rbEnglish);
        }

        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            String theme;
            if (checkedId == R.id.rbLight) theme = "light";
            else if (checkedId == R.id.rbDark) theme = "dark";
            else theme = "system";

            prefs.edit().putString("theme", theme).apply();
            applyTheme(theme);
        });

        rgLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            String lang = (checkedId == R.id.rbVietnamese) ? "vi" : "en";
            prefs.edit().putString("language", lang).apply();
            applyLanguage(lang);
            recreate();
        });
        Button btnBackProfile = findViewById(R.id.btnBackProfile);
        btnBackProfile.setOnClickListener(v -> {
            // Khi finish
            Intent result = new Intent();
            result.putExtra("languageChanged", true); // hoặc themeChanged
            setResult(RESULT_OK, result);
            finish();
        });


    }

    private void applyTheme(String theme) {
        switch (theme) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void applyLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}

