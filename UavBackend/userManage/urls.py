from django.conf.urls import url
from . import views

urlpatterns = [
    url('^send_email/$', views.send_email),
    url('^register/$', views.register),
    url('^login/$', views.login),
    url('^test/$', views.test),
    url('^news_list/$', views.news_list),
    url('^login_back/$', views.login_back, name="login_page"),
    url('^get_uuid/$', views.get_uuid, name="get_uuid"),
    url('^refresh_login_status/$', views.refresh_login_status),
    url("^get_login_status/$", views.get_login_status, name="get_login_status"),
    url('^cancel_login/$', views.cancel_login, name="cancel_login"),
    url('^confirm_login/$', views.confirm_login),
    url('^index/$', views.index, name="index"),
    url('^voice_assistant_key_word_config$', views.key_word, name="key_word_config"),
]
