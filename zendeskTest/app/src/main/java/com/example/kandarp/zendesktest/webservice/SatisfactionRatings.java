package com.example.kandarp.zendesktest.webservice;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Kandarp on 12/5/2016.
 */

public class SatisfactionRatings {


    @SerializedName("satisfaction_ratings")
    List<review> reviewsList;

    public List<review> getReviewsList(){return reviewsList;}
    public SatisfactionRatings(){}

    public class review
    {
        @SerializedName("url")
        private String url;
        @SerializedName("id")
        private int id;
        @SerializedName("ticket_id")
        private long tid;
        @SerializedName("score")
        private String score;
        @SerializedName("created_at")
        private String cat;
        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("group_id")
        private long gid;
        @SerializedName("comment")
        private String comment;
        @SerializedName("assignee_id")
        private long agentId;
        @SerializedName("requester_id")
        private long requesterId;


    public String getComment() {
        return comment;
    }

    public long getAgentId() {
        return agentId;
    }

    public long getRequesterId() {
        return requesterId;
    }


     public String getDate(){;
         System.out.print("DSFSD");
         try {
             Date date = (new SimpleDateFormat("yyyy-MM-dd")).parse(updatedAt);
             return (new SimpleDateFormat("MM/dd/yyyy")).format(date);
         }
         catch(Exception e) {
             return "";
         }
     }

}


}
