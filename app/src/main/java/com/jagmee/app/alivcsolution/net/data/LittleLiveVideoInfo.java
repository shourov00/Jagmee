package com.jagmee.app.alivcsolution.net.data;

import com.aliyun.player.source.UrlSource;
import com.jagmee.app.alivcsolution.view.video.BaseVideoSourceModel;
import com.jagmee.app.alivcsolution.view.video.videolist.VideoSourceType;

import java.util.List;

public class LittleLiveVideoInfo {
    private java.util.List<LiveListBean> liveList;

    public List<LiveListBean> getLiveList() {
        return liveList;
    }

    public void setLiveList(
            List<LiveListBean> liveList) {
        this.liveList = liveList;
    }

    public static class LiveListBean extends BaseVideoSourceModel {
        /**
         * 直播流id
         */
        private String liveId;
        /**
         * 直播流地址
         */
        private String liveUrl;

        public String getLiveId() {
            return liveId;
        }

        public void setLiveId(String liveId) {
            this.liveId = liveId;
        }

        public String getLiveUrl() {
            return liveUrl;
        }

        public void setLiveUrl(String liveUrl) {
            this.liveUrl = liveUrl;
        }
        @Override
        public VideoSourceType getSourceType() {
            return VideoSourceType.TYPE_LIVE;
        }

        @Override
        public UrlSource getUrlSource() {
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(liveUrl);
            return urlSource;
        }

        @Override
        public String getUUID() {
            return liveId;
        }
    }
}
