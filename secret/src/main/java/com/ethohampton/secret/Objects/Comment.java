package com.ethohampton.secret.Objects;

import com.ethohampton.secret.Database;
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
public class Comment {

    @Id
    private String id;
    @Index
    private String referencedSecretID;//The id of the secret this comment is about
    private String referencedCommentID = null;//the id of the comment this is replying to, null/empty if this is a root level comment
    @Index
    private long creationTime;//when was this comment created
    private String comment;

    @Index
    private long upvotes; //how popular a comment is
    @Index
    private long downvotes; //how not popular a comment is

    private String creator;//creator of the comment
    private boolean creatorIsCreatorOfSecret = false;//true if the creator of the comment and creator of the secret match

    @Deprecated
    public Comment() {
    }

    public Comment(long creationTime, String comment, String referencedSecretID, String creator) {
        this.creationTime = creationTime;
        this.comment = comment;//StringEscapeUtils.escapeHtml4(comment);
        this.id = Utils.shortHash(comment + referencedSecretID);//required to insure comments have unique ids for each separate secret
        this.referencedSecretID = referencedSecretID;//the reference to the secret
        this.creator = creator;

        Secret secret = Database.getSecret(referencedSecretID);//tests if this creator of this comment is the creator of the secret they are responding to
        if (this.creator != null && secret.getCreator() != null && this.creator.equals(secret.getCreator())) {
            creatorIsCreatorOfSecret = true;
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getReferencedSecretID() {
        return referencedSecretID;
    }

    public void setReferencedSecretID(String referencedSecretID) {
        this.referencedSecretID = referencedSecretID;
    }

    public String getCreator() {
        return creator;
    }

    public Comment setCreator(String creator) {
        this.creator = creator;
        return this;
    }

    public String getReferencedCommentID() {
        return referencedCommentID;
    }

    public Comment setReferencedCommentID(String referencedCommentID) {
        this.referencedCommentID = referencedCommentID;
        return this;
    }

    public boolean isCreatorIsCreatorOfSecret() {
        return creatorIsCreatorOfSecret;
    }

    public Comment setCreatorIsCreatorOfSecret(boolean creatorIsCreatorOfSecret) {
        this.creatorIsCreatorOfSecret = creatorIsCreatorOfSecret;
        return this;
    }
}

