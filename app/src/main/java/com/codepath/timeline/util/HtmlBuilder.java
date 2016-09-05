package com.codepath.timeline.util;


import com.codepath.timeline.models.Story;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HtmlBuilder {

    private String htmlView;

    public HtmlBuilder(Story story) {
        String html = "<!DOCTYPE html><head><meta name='viewport' content='user-scalable=no, initial-scale=1.0, maximum-scale=1.0, width=device-width'> " +
                "<meta name='apple-mobile-web-app-capable' content='yes'/><meta name='apple-mobile-web-app-status-bar-style' content='black'/> " +
                "<style type='text/css'> body { margin: 0px; font-family: 'arial'}#wrapper { width: 100%; }#story_header { position: relative;}" +
                "#story_image { width: 100%; display: block;}#story_image_shadow { position: absolute; width: 100%; -moz-box-shadow: 0px -24px 30px 20px #333; " +
                "-webkit-box-shadow: 0px -24px 30px 20px #333; box-shadow: 0px -24px 30px 20px #333; opacity: 0.5;}#story_title { color: white; font-size: 30px; " +
                "position: absolute; bottom: 10px; left: 10px; font-weight: bold; z-index: 999;} .story_year_wrapper { background-color: #76CCD7; height: 60px;} " +
                ".story_year { color: white; font-size: 26px; line-height: 60px; margin-left: 10px; } .moments_list { padding: 0px; margin: 0px; " +
                "list-style: none; max-width: 100%;} .moments_list li { position: relative;} .li_padding_top { height: 20px; } .li_padding_bottom { height: 10px;} " +
                ".vertical_line { background-color: #76CCD7; height: 100%; position: absolute; left: 34px; width: 2px;} " +
                ".date_caption_wrapper { position: relative; overflow: auto;} " +
                ".moment_date_circle { display: inline; float: left; margin-left: 10px; width: 50px; height: 50px; -webkit-border-radius: 25px; " +
                "-moz-border-radius: 25px; border-radius: 25px; background-color: #76CCD7; z-index: 2;} .moment_date { display: block; color: white; " +
                "text-align: center; font-size: 14px;} .moment_date_month { padding-top: 10px;} .moment_caption { color: #657786; word-wrap: break-word; " +
                "overflow: hidden; max-height: 50px; padding-left: 10px; margin: 0px; line-height: 50px;} .moment_wrapper { background-color: #E4F3F5; margin-left: 50px; " +
                "margin-right: 10px; margin-top: 10px; padding: 10px;} .collaborator { overflow: auto;}" +
                ".collaborator_image { width: 40px; height: 40px; -webkit-border-radius: 20px; -moz-border-radius: 20px; border-radius: 20px; float: left;} " +
                ".collaborator_name { float: left; margin-left: 10px; margin-top: 3px; font-weight: bold;} " +
                ".collaborator_location_icon { width: 20px; margin-left: 10px; float: left;} " +
                ".collaborator_location { color: #657786; float: left; line-height: 20px;} .moment_image { margin-top: 10px; width: 100%;} " +
                "</style> </head> <body> <div id='wrapper'> <div id='story_header'> <img id='story_image'";
        html += "src='" + story.getBackgroundImageUrl() + "'/>";
        html += "<span id=\"story_title\">" + story.getTitle() + "</span>";
        html += "<div id=\"story_image_shadow\"></div>";


        File f = new File("webView.html");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(html);
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getHtmlView() {
        return htmlView;
    }


}
