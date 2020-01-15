from django.conf.urls import url
from . import views

urlpatterns = [
    url('^send_email/$', views.send_email),
    url('^register/$', views.register),
    url('^login/$', views.login),
    url('^test/$', views.test),
    url('^news_list/$', views.news_list),
]
