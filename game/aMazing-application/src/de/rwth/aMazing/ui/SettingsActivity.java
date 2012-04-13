/**
 * aMazing! Geolocalized multiplayer game for Android devices.
 * Conceived and realized within the course "Mixed Reality Games for 
 * Mobile Devices" at Fraunhofer FIT.
 * 
 * http://www.fit.fraunhofer.de/de/fb/cscw/mixed-reality.html
 * http://www.totem-games.org/?q=aMazing
 * 
 * Copyright (C) 2012  Alexander Hermans, Tianjiao Wang
 * 
 * Contact: 
 * alexander.hermans0@gmail.com, tianjiao.wang@rwth-aachen.de,
 * richard.wetzel@fit.fraunhofer.de, lisa.blum@fit.fraunhofer.de, 
 * denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Developer(s): Alexander Hermans, Tianjiao Wang
 * ZebroGaMQ:  Denis Conan, Gabriel Adgeg 
 */

package de.rwth.aMazing.ui;

import de.rwth.aMazing.GameSession;
import de.rwth.aMazing.R;

import android.app.Activity;
//import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private TextView[] tv = new TextView[6];

	// private TextView tv, tv1, tv2, tv3, tv4, tv5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		tv[0] = (TextView) findViewById(R.id.music);
		tv[1] = (TextView) findViewById(R.id.time);
		tv[2] = (TextView) findViewById(R.id.district);
		tv[3] = (TextView) findViewById(R.id.crown);
		tv[4] = (TextView) findViewById(R.id.item);
		tv[5] = (TextView) findViewById(R.id.back);

		tv[0].setTypeface(MenuActivity.tf);
		tv[1].setTypeface(MenuActivity.tf);
		tv[2].setTypeface(MenuActivity.tf);
		tv[3].setTypeface(MenuActivity.tf);
		tv[4].setTypeface(MenuActivity.tf);
		tv[5].setTypeface(MenuActivity.tf);

		tv[0].setOnTouchListener(new CustomTouchListener());
		tv[1].setOnTouchListener(new CustomTouchListener());
		tv[2].setOnTouchListener(new CustomTouchListener());
		tv[3].setOnTouchListener(new CustomTouchListener());
		tv[4].setOnTouchListener(new CustomTouchListener());
		tv[5].setOnTouchListener(new CustomTouchListener());

		tv[0].setOnClickListener(this);
		tv[1].setOnClickListener(this);
		tv[2].setOnClickListener(this);
		tv[3].setOnClickListener(this);
		tv[4].setOnClickListener(this);
		tv[5].setOnClickListener(this);

		setText();
	}
	private void setText() {
		if (settingsHolder[0] % 2 == 0) {
			tv[0].setText("Sound on");
		}
		if (settingsHolder[0] % 2 == 1) {
			tv[0].setText("Sound off");
		}
		tv[1].setText("Time: " + GameSession.timeInMiliSetting / 60000 + " min");
		tv[2].setText("District: " + GameSession.districtSizeInMetersSetting
				+ "m");
		tv[3].setText("Crowns: " + GameSession.crownNumberSetting);
		tv[4].setText("Items: " + GameSession.itemNumberSetting);
	}

	private boolean[] soundSetting = { true, false };
	private int[] timeSetting = { 900000, 1800000, 2700000, 300000};
	private int[] districtSetting = { 500, 1000, 1500, 2000, 250 };
	private int[] crownNumberSetting = { 5, 7, 3 };
	private int[] itemNumberSetting = { 25, 30, 40, 50, 5, 15, 20 };

	private int[] settingsHolder = { 0, 0, 0, 0, 0 };

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.music:
			settingsHolder[0]++;
			GameSession.soundSetting = soundSetting[settingsHolder[0]
					% soundSetting.length];
			break;
		case R.id.time:
			settingsHolder[1]++;
			GameSession.timeInMiliSetting = timeSetting[settingsHolder[1]
					% timeSetting.length];
			break;
		case R.id.district:
			settingsHolder[2]++;
			GameSession.districtSizeInMetersSetting = districtSetting[settingsHolder[2]
					% districtSetting.length];
			break;
		case R.id.crown:
			settingsHolder[3]++;
			GameSession.crownNumberSetting = crownNumberSetting[settingsHolder[3]
					% crownNumberSetting.length];
			break;
		case R.id.item:
			settingsHolder[4]++;
			GameSession.itemNumberSetting = itemNumberSetting[settingsHolder[4]
					% itemNumberSetting.length];
			break;
		case R.id.back:
			finish();
			break;
		}
		setText();
	}
}