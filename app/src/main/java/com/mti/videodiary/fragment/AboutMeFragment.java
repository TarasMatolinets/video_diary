package com.mti.videodiary.fragment;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mti.com.videodiary.R;

/**
 * Created by Taras Matolinets on 24.02.15.
 */

public class AboutMeFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_me, container, false);

        TextView tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        TextView tvLinkenIn = (TextView) view.findViewById(R.id.tvLinkenIn);

        setLinkToEmail(tvEmail);
        setLinkLinkenIn(tvLinkenIn);

        return view;
    }

    private void setLinkLinkenIn(TextView tvLinkenIn) {
        String messageLinkenIn = getString(R.string.contact_me_linken_in);
        messageLinkenIn = messageLinkenIn + " " + getResources().getText(R.string.linken_in);
        final SpannableString mesLinkenIn = new SpannableString(messageLinkenIn);
        Linkify.addLinks(mesLinkenIn, Linkify.WEB_URLS);
        tvLinkenIn.setText(messageLinkenIn);
    }

    private void setLinkToEmail(TextView tvEmail) {
        String message = getString(R.string.contact_me_email);
        message = message + " " + getResources().getText(R.string.email);
        final SpannableString mes = new SpannableString(message);
        Linkify.addLinks(mes, Linkify.EMAIL_ADDRESSES);
        tvEmail.setText(mes);
    }
}
