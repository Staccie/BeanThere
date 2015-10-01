package com.beanthere.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

	public static final String BEANC = "aomc";
	public static final String BEANU = "aomu";
	public static final String BEANP = "aomp";

	public static final String LOGIN_MILLIS = "login_millis";
	public static final String FAIL_COUNT = "fail_limit";
	public static final String LAST_UPDATED = "lastUpdated";

	public static void putInt(Context context, String key, int value) {
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getInt(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		return sp.getInt(key, -1);
	}

	public static int getInt(Context context, String key, int defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		return sp.getInt(key, defaultValue);
	}

	public static void putString(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		return sp.getString(key, "");

	}

	public static String getString(Context context, String key, String defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		return sp.getString(key, defaultValue);

	}

	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static Boolean getBoolean(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}

	public static void putLong(Context context, String key, long value) {
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static Long getLong(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		return sp.getLong(key, -1);
	}

//	public static String getAOMU(Context context) {
//		return getString(context, AOM_U);
//	}
//
//	public static String getAOMC(Context context) {
//		return getString(context, AOM_C);
//	}
//
//	public static String getAOMP(Context context) {
//		return getString(context, AOM_P);
//	}

}
