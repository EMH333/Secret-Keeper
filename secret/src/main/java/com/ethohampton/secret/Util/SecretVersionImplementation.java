package com.ethohampton.secret.Util;

import com.ethohampton.secret.Objects.Secret;

/**
 * Created by ethohampton on 7/31/17.
 *
 * Keeps track of implementations in order to keep the secret class easy to maintain
 */

public class SecretVersionImplementation {
    public boolean isVotingImplemented(Secret s){
        return s.getVersion() > getVotingImplemented();
    }
    public int getVotingImplemented(){
        return 2;//version 2 is when voting was implemented
    }
}
