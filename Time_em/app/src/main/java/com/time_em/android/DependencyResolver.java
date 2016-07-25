package com.time_em.android;

import android.content.Context;
import com.time_em.android.Interface.IDependencyResolver;
import com.time_em.db.PreferenceData;

public class DependencyResolver implements IDependencyResolver {
     public Context mContext;

    public DependencyResolver(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public PreferenceData pref() {
        return new PreferenceData(mContext);
    }
}
