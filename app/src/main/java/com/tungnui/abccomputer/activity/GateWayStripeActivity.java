package com.tungnui.abccomputer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tungnui.abccomputer.R;
import com.tungnui.abccomputer.utils.DialogUtils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

/**
 * Created by Nasir on 7/18/17.
 */

public class GateWayStripeActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;

    private CardInputWidget cardInputWidget;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariables();
        initView();

        initListener();
    }
    private void initVariables(){
        mContext = getApplicationContext();
        mActivity = GateWayStripeActivity.this;
    }
    private void initView(){
        setContentView(R.layout.activity_gateway_stripe);
        cardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
    }
    private void initListener(){
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFunctionality();
            }
        });
    }
    private void initFunctionality(){

        final ProgressDialog progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false);

        Stripe stripe = new Stripe(mContext);
        Card card = cardInputWidget.getCard();
        if (card.validateCard()){
            stripe.createToken(cardInputWidget.getCard(), getString(R.string.stripe_publish_key), new TokenCallback() {
                public void onSuccess(Token token) {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("stripe_token", token.getId());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                    DialogUtils.dismissProgressDialog(progressDialog);
                }

                public void onError(Exception error) {

                    DialogUtils.dismissProgressDialog(progressDialog);

                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();

                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();

                }
            });
        }
    }
}
