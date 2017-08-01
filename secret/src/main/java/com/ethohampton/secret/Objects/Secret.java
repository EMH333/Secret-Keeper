package com.ethohampton.secret.Objects;

import com.ethohampton.secret.Util.Utils;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;


/**
 * Created by ethohampton on 12/14/16.
 * default question type
 */
@Entity
@Cache
public class Secret {

    @Id
    private String id;
    @Index
    private long creationTime;//when was this secret created
    private String secret;

    private int version;//the version of the secret (to help insure we don't break because of outdated secrets)

    @Index
    private long upvotes; //Implemented in version 2: how popular a secret is
    @Index
    private long downvotes; //Implemented in version 2: how popular a secret is

    @Deprecated
    public Secret() {
    }

    public Secret(long creationTime, String secret) {
        this.creationTime = creationTime;
        this.secret = secret;//StringEscapeUtils.escapeHtml4(secret);
        this.id = Utils.hash(secret);
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the creationTime
     */
    public Long getCreationTime() {
        return creationTime;
    }

    /**
     * @param creationTime the creationTime to set
     */
    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(long upvotes) {
        this.upvotes = upvotes;
    }

    public long getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(long downvotes) {
        this.downvotes = downvotes;
    }

    public void addDownVote() {
        downvotes++;
    }

    public void addUpVote() {
        upvotes++;
    }
}

