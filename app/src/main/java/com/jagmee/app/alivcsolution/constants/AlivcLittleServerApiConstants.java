package com.jagmee.app.alivcsolution.constants;

public class AlivcLittleServerApiConstants {
    public static final String BASE_URL = "https://alivc-demo.aliyuncs.com";

    /**
     * 视频封面图片地址的base url
     */
    public static final String URL_IMAGE_BASE = "https://alivc-demo-vod.aliyuncs.com/";

    /**
     * 生成新用户 get
     */
    public static final String URL_NEW_GUEST = BASE_URL + "/user/randomUser";

    /**
     * 获取sts信息 get
     */
    public static final String URL_GET_STS_INFO = BASE_URL + "/vod/getSts";

    /**
     * 获取推荐视频列表 get
     */
    public static final String URL_GET_RECOMMEND_VIDEO_LIST = BASE_URL + "/vod/getRecommendVideoList";
    /**
     * 获取直播视频列表 get
     */
    public static final String URL_GET_LIVE_VIDEO_LIST = BASE_URL+"/vod/getRecommendLiveList";
    /**
     * 获取个人中心视频列表 get
     */
    public static final String URL_GET_PERSIONAL_VIDEO_LIST = BASE_URL + "/vod/getPersonalVideoList";

    /**
     * 添加视频(视频上传) post
     */
    public static final String URL_VIDEO_PUBLISH = BASE_URL + "/vod/videoPublish";

    /**
     * 修改昵称 post
     */
    public static final String URL_MODIFY_NICK_NAME = BASE_URL + "/user/updateUser";

    /**
     * 删除视频 delete
     */
    public static final String URL_DELETE_VIDEO = BASE_URL + "/vod/deleteVideoById";
    /**
     *获取视频上传凭证
     */
    public static final String URL_GET_VIDEO_UPLOAD_AUTH = BASE_URL + "/vod/getVideoUploadAuth";
    /**
     * 刷新视频上传凭证
     */
    public static final String URL_REFRESH_VIDEO_UPLOAD_AUTH = BASE_URL + "/vod/refreshVideoUploadAuth";
    /**
     * 获取图片上传凭证
     */
    public static final String URL_GET_IMAGE_UPLOAD_AUTH = BASE_URL + "/vod/getImageUploadAuth";
}
