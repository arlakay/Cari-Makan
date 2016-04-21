package com.carimakan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.carimakan.app.MyApplication;
import com.carimakan.helper.SQLiteHandler;
import com.carimakan.util.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
	private static final String TAG = RegisterActivity.class.getSimpleName();
	private Button btnRegister, btnLinkToLogin;
	private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword, inputLayoutPassword2;
	private EditText inputName,	inputEmail, inputPassword, inputPassword2;
	private ProgressDialog pDialog;
	private SQLiteHandler db;
	private CoordinatorLayout coordinatorLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		coordinatorLayout = (CoordinatorLayout) findViewById(R.id
				.coordinatorLayout);

		inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
		inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
		inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
		inputLayoutPassword2 = (TextInputLayout) findViewById(R.id.input_layout_password2);

        inputName = (EditText) findViewById(R.id.name);
		inputEmail = (EditText) findViewById(R.id.email);
		inputPassword = (EditText) findViewById(R.id.password);
        inputPassword2 = (EditText) findViewById(R.id.password2);

		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		// SQLite database handler
		db = new SQLiteHandler(getApplicationContext());

		inputName.addTextChangedListener(new MyTextWatcher(inputName));
		inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
		inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
		inputPassword2.addTextChangedListener(new MyTextWatcher(inputPassword2));

		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String name = inputName.getText().toString();
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
                String password2 = inputPassword2.getText().toString();

				if (!password2.equals(password)) {
					inputLayoutPassword2.setError(getString(R.string.err_msg_password2));
					requestFocus(inputPassword2);
				} else {
					registerUser(name, email, password);
				}
			}
		});

		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(i);
				finish();
			}
		});

	}

	/**
	 * Function to store user in MySQL database will post params(tag, name,
	 * email, password) to register url
	 * */
	private void registerUser(final String name, final String email,
			final String password) {

		if (!validateName()) {
			return;
		}

		if (!validateEmail()) {
			return;
		}

		if (!validatePassword()) {
			return;
		}

		if (!validatePassword2()) {
			return;
		}

		// Tag used to cancel the request
		String tag_string_req = "req_register";
		pDialog.setMessage("Registering ...");
		showDialog();

		StringRequest strReq = new StringRequest(Method.POST,
				Config.URL_REGISTER, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Register Response: " + response.toString());
						hideDialog();

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");
							if (!error) {
								// User successfully stored in MySQL
								// Now store the user in sqlite
								String uid = jObj.getString("uid");

								JSONObject user = jObj.getJSONObject("user");
								String name = user.getString("name");
								String email = user.getString("email");
								String created_at = user
										.getString("created_at");

								// Inserting row in users table
								db.addUser(name, email, uid, created_at);

								Snackbar snackbar = Snackbar
										.make(coordinatorLayout, "Register Success", Snackbar.LENGTH_LONG)
										.setAction("LOGIN", new View.OnClickListener() {
											@Override
											public void onClick(View view) {
												// Launch login activity
												Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
												startActivity(intent);
												finish();
											}
										});

								snackbar.show();


							} else {

								// Error occurred in registration. Get the error
								// message
								String errorMsg = jObj.getString("error_msg");
								Toast.makeText(getApplicationContext(),
										errorMsg, Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Registration Error: " + error.getMessage());
						Toast.makeText(getApplicationContext(),
								error.getMessage(), Toast.LENGTH_LONG).show();
						hideDialog();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();
				params.put("tag", "register");
				params.put("name", name);
				params.put("email", email);
				params.put("password", password);
				return params;
			}
		};
		// Adding request to request queue
		MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

	private boolean validateName() {
		if (inputName.getText().toString().trim().isEmpty()) {
			inputLayoutName.setError(getString(R.string.err_msg_name));
			requestFocus(inputName);
			return false;
		} else {
			inputLayoutName.setErrorEnabled(false);
		}
		return true;
	}

	private boolean validateEmail() {
		String email = inputEmail.getText().toString().trim();

		if (email.isEmpty() || !isValidEmail(email)) {
			inputLayoutEmail.setError(getString(R.string.err_msg_email));
			requestFocus(inputEmail);
			return false;
		} else {
			inputLayoutEmail.setErrorEnabled(false);
		}
		return true;
	}

	private boolean validatePassword() {
		if (inputPassword.getText().toString().trim().isEmpty()) {
			inputLayoutPassword.setError(getString(R.string.err_msg_password));
			requestFocus(inputPassword);
			return false;
		} else {
			inputLayoutPassword.setErrorEnabled(false);
		}
		return true;
	}

	private boolean validatePassword2() {
		if (inputPassword2.getText().toString().trim().isEmpty()) {
			inputLayoutPassword2.setError(getString(R.string.err_msg_password2));
			requestFocus(inputPassword2);
			return false;
		} else {
			inputLayoutPassword2.setErrorEnabled(false);
		}
		return true;
	}

	private static boolean isValidEmail(String email) {
		return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	private void requestFocus(View view) {
		if (view.requestFocus()) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}

	private class MyTextWatcher implements TextWatcher {

		private View view;

		private MyTextWatcher(View view) {
			this.view = view;
		}

		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		public void afterTextChanged(Editable editable) {
			switch (view.getId()) {
				case R.id.name:
					validateName();
					break;
				case R.id.email:
					validateEmail();
					break;
				case R.id.password:
					validatePassword();
					break;
				case R.id.password2:
					validatePassword2();
					break;
			}
		}
	}

}
