from django.conf.urls import url
from . import views

urlpatterns = [
    url('^send_email/$', views.send_email),
    url('^register/$', views.register),
    url('^login/$', views.login),
    url('^test/$', views.test),
    url('^news_list/$', views.news_list, name="news_list"),
    url('^get_article_content/$', views.article_content, name="article_content"),
    url('^login_back/$', views.login_back, name="login_page"),
    url('^login_by_account/$', views.login_by_account, name="login_by_account"),
    url('^logout/$', views.logout, name="logout"),
    url('^get_uuid/$', views.get_uuid, name="get_uuid"),
    url('^refresh_login_status/$', views.refresh_login_status),
    url("^get_login_status/$", views.get_login_status, name="get_login_status"),
    url('^cancel_login/$', views.cancel_login, name="cancel_login"),
    url('^confirm_login/$', views.confirm_login),
    url('^index/$', views.index, name="index"),
    url('^voice_assistant_key_word_config/$', views.key_word, name="key_word_config"),
    url('^save_key_word/$', views.save_key_word, name="save_key_word"),
    url('^add_custom_key_word/$', views.add_custom_key_word, name="add_custom_key_word"),
    url('^save_custom_key_word/$', views.save_custom_key_word, name="save_custom_key_word"),
    url('^user_info_app/$', views.user_info_app),  # 用户信息展示页面，用于在app中展示
    url('^app_get_user_key_word/$', views.app_get_key_word),
    # 上传图片
    url('^upload_file/$', views.upload_file),
    # 相册展示
    url('^display_image/$', views.display_image, name="display_image"),
    # 账户设置
    url('^view_account/$', views.view_account, name="view_account"),
    url('^profile_account/$', views.profile_account, name="profile_account"),
    url('^avatar_upload/$', views.avatar_upload, name="avatar_upload"),
    url('^update_user_info/$', views.update_user_info, name="update_user_info"),
    # 下载客户端
    url('^download_client/$', views.download_client),
]
